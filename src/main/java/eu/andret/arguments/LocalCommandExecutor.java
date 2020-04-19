/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */
package eu.andret.arguments;

import eu.andret.arguments.annotation.Argument;
import eu.andret.arguments.mapper.ICommandToMethodMapper;
import eu.andret.arguments.mapper.IMethodToDescriptionMapper;
import eu.andret.arguments.mapper.IPermissionMapper;
import eu.andret.arguments.mapper.IResponseMapper;
import eu.andret.arguments.mapper.impl.CommandToMethodMapper;
import eu.andret.arguments.mapper.impl.MethodInvoker;
import eu.andret.arguments.mapper.impl.MethodToDescriptionMapper;
import eu.andret.arguments.mapper.impl.PermissionMapper;
import eu.andret.arguments.mapper.impl.ResponseMapper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Local command executor, allows customization of commands behavior.
 *
 * @author Andret
 * @since May 18, 2020
 */
class LocalCommandExecutor implements CommandExecutor {
	private final Util util = Util.getInstance();
	private final Map<String, Mapper<?>> mappers = new HashMap<>();
	private final ICommandToMethodMapper commandToMethodMapper = new CommandToMethodMapper(mappers);
	private final IMethodToDescriptionMapper methodToDescriptionMapper = new MethodToDescriptionMapper();
	private final IPermissionMapper permissionMapper = new PermissionMapper();
	private final IResponseMapper responseMapper = new ResponseMapper();
	private final Class<? extends AnnotatedCommandExecutor> commandClass;
	private final MethodInvoker methodInvoker;
	private AnnotatedCommand.OnUnknownSubCommandExecutionListener onUnknownSubCommandExecutionListener;
	private AnnotatedCommand.OnInsufficientPermissionsListener onInsufficientPermissionsListener;

	/**
	 * Constructs the LocalCommandExecutor.
	 *
	 * @param commandClass The {@link AnnotatedCommandExecutor} that will be analyzed in search of
	 * methods annotated with {@link eu.andret.arguments.annotation.Argument}
	 * @param plugin The {@link org.bukkit.plugin.java.JavaPlugin} superclass of main plugin class.
	 */
	LocalCommandExecutor(Class<? extends AnnotatedCommandExecutor> commandClass, JavaPlugin plugin) {
		this.commandClass = commandClass;
		methodInvoker = new MethodInvoker(plugin, mappers);
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (args.length == 0) {
			Arrays.stream(commandClass.getDeclaredMethods())
					.filter(method -> !Modifier.isStatic(method.getModifiers()))
					.filter(method -> method.isAnnotationPresent(Argument.class))
					.forEach(method -> sender.sendMessage(methodToDescriptionMapper.mapMethodToDescription(method, command.getName())));
		} else {
			commandToMethodMapper
					.mapCommandToMethod(commandClass.getDeclaredMethods(), args, sender)
					.ifPresentOrElse(method -> {
								if (permissionMapper.mapPermission(method, sender)) {
									Object result = methodInvoker.invokeMethod(method, args, sender, commandClass);
									responseMapper.mapResponse(sender, result, method);
								} else {
									onInsufficientPermissionsListener.insufficientPermissions(sender);
								}
							},
							() -> onUnknownSubCommandExecutionListener.unknownSubCommandExecuted(sender)
					);
		}
		return true;
	}

	public <E> boolean addMapper(String id, Mapper<E> mapper) {
		if (mappers.containsKey(id)) {
			return false;
		}
		mappers.put(id, mapper);
		return true;
	}

	/**
	 * Sets on unknown sub command execution listener.
	 *
	 * @param listener The {@link AnnotatedCommand.OnUnknownSubCommandExecutionListener}
	 */
	void setOnUnknownSubCommandExecutionListener(AnnotatedCommand.OnUnknownSubCommandExecutionListener listener) {
		onUnknownSubCommandExecutionListener = listener;
	}

	/**
	 * Sets on insufficient permissions' listener.
	 *
	 * @param listener The {@link AnnotatedCommand.OnInsufficientPermissionsListener}
	 */
	void setOnInsufficientPermissionsListener(AnnotatedCommand.OnInsufficientPermissionsListener listener) {
		onInsufficientPermissionsListener = listener;
	}
}
