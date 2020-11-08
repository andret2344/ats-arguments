/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.annotation.Argument;
import eu.andret.arguments.entity.ExecutionCall;
import eu.andret.arguments.entity.Mapper;
import eu.andret.arguments.mapper.ICommandToMethodMapper;
import eu.andret.arguments.mapper.IDisplayTypeMapper;
import eu.andret.arguments.mapper.IMethodInvoker;
import eu.andret.arguments.mapper.IMethodToDescriptionMapper;
import eu.andret.arguments.mapper.IPermissionMapper;
import eu.andret.arguments.mapper.IResponseMapper;
import eu.andret.arguments.mapper.impl.CommandToMethodMapper;
import eu.andret.arguments.mapper.impl.DisplayTypeMapper;
import eu.andret.arguments.mapper.impl.MethodInvoker;
import eu.andret.arguments.mapper.impl.MethodToDescriptionMapper;
import eu.andret.arguments.mapper.impl.PermissionMapper;
import eu.andret.arguments.mapper.impl.ResponseMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Local command executor, allows customization of commands behavior.
 *
 * @author Andret
 * @since May 18, 2020
 */
@Value
@Getter(AccessLevel.NONE)
class LocalCommandExecutor implements CommandExecutor {
	Map<String, Mapper<?>> mappers = new HashMap<>();
	ICommandToMethodMapper commandToMethodMapper = new CommandToMethodMapper(mappers);
	IMethodToDescriptionMapper methodToDescriptionMapper = new MethodToDescriptionMapper();
	IPermissionMapper permissionMapper = new PermissionMapper();
	IResponseMapper responseMapper = new ResponseMapper();
	IDisplayTypeMapper displayTypeMapper = new DisplayTypeMapper(permissionMapper);
	Class<? extends AnnotatedCommandExecutor<? extends JavaPlugin>> commandClass;
	IMethodInvoker methodInvoker;
	@NonFinal
	AnnotatedCommand.OnUnknownSubCommandExecutionListener onUnknownSubCommandExecutionListener;
	@NonFinal
	AnnotatedCommand.OnInsufficientPermissionsListener onInsufficientPermissionsListener;
	@Getter(AccessLevel.PACKAGE)
	AnnotatedCommand.Options options = new AnnotatedCommand.Options();

	/**
	 * Constructs the LocalCommandExecutor.
	 *
	 * @param commandClass The {@link AnnotatedCommandExecutor} that will be analyzed in search of
	 * 		methods annotated with {@link Argument}
	 * @param plugin The {@link JavaPlugin} superclass of main plugin class.
	 */
	<E extends JavaPlugin> LocalCommandExecutor(Class<? extends AnnotatedCommandExecutor<E>> commandClass, E plugin) {
		this.commandClass = commandClass;
		methodInvoker = new MethodInvoker(plugin, mappers);
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (args.length == 0) {
			Arrays.stream(commandClass.getDeclaredMethods())
					.filter(method -> !Modifier.isStatic(method.getModifiers()))
					.filter(method -> method.isAnnotationPresent(Argument.class))
					.filter(method -> displayTypeMapper.mapDisplayType(method, sender))
					.forEach(method -> sender.sendMessage(methodToDescriptionMapper.mapMethodToDescription(method, command.getName())));
		} else {
			commandToMethodMapper
					.mapCommandToMethod(commandClass.getDeclaredMethods(), args, sender)
					.ifPresentOrElse(method -> invokeMethod(method, sender, args), () -> noneMethodFound(sender));
		}
		return true;
	}

	/**
	 * Sets on unknown sub command execution listener.
	 *
	 * @param listener The {@link AnnotatedCommand.OnUnknownSubCommandExecutionListener}
	 */
	public void setOnUnknownSubCommandExecutionListener(AnnotatedCommand.OnUnknownSubCommandExecutionListener listener) {
		onUnknownSubCommandExecutionListener = listener;
	}

	/**
	 * Sets on insufficient permissions' listener.
	 *
	 * @param listener The {@link AnnotatedCommand.OnInsufficientPermissionsListener}
	 */
	public void setOnInsufficientPermissionsListener(AnnotatedCommand.OnInsufficientPermissionsListener listener) {
		onInsufficientPermissionsListener = listener;
	}

	<E> boolean addMapper(String id, Mapper<E> mapper) {
		if (mappers.containsKey(id)) {
			return false;
		}
		mappers.put(id, mapper);
		return true;
	}

	private void noneMethodFound(CommandSender sender) {
		Optional.ofNullable(onUnknownSubCommandExecutionListener)
				.ifPresent(listener -> listener.unknownSubCommandExecuted(sender));
	}

	private void invokeMethod(ExecutionCall method, CommandSender sender, String[] args) {
		if (permissionMapper.mapPermission(method.getMethod(), sender)) {
			Object result = methodInvoker.invokeMethod(method, args, sender, commandClass);
			responseMapper.mapResponse(sender, result, method.getMethod().getAnnotation(Argument.class).responseType(), options);
		} else if (onInsufficientPermissionsListener != null) {
			onInsufficientPermissionsListener.insufficientPermissions(sender);
		}
	}
}
