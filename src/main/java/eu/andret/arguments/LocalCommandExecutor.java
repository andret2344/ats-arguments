/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.consumer.IResponseConsumer;
import eu.andret.arguments.consumer.impl.ResponseConsumer;
import eu.andret.arguments.entity.ExecutionCall;
import eu.andret.arguments.entity.MappingConfig;
import eu.andret.arguments.entity.MappingSet;
import eu.andret.arguments.filter.IDisplayTypeFilter;
import eu.andret.arguments.filter.IPermissionFilter;
import eu.andret.arguments.filter.impl.DisplayTypeFilter;
import eu.andret.arguments.filter.impl.PermissionFilter;
import eu.andret.arguments.mapper.ICommandToMethodMapper;
import eu.andret.arguments.mapper.IMethodInvoker;
import eu.andret.arguments.mapper.IMethodToDescriptionMapper;
import eu.andret.arguments.mapper.impl.CommandToMethodMapper;
import eu.andret.arguments.mapper.impl.MethodInvoker;
import eu.andret.arguments.mapper.impl.MethodToDescriptionMapper;
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
import java.util.Optional;

/**
 * Local command executor, allows customization of commands behavior.
 *
 * @author Andret
 * @since May 18, 2020
 */
@Value
@NonFinal
@Getter(AccessLevel.NONE)
class LocalCommandExecutor<E extends JavaPlugin> implements CommandExecutor {
	AnnotatedCommand<E> annotatedCommand;
	MappingConfig mappingConfig = new MappingConfig();
	ICommandToMethodMapper commandToMethodMapper = new CommandToMethodMapper(mappingConfig);
	IMethodToDescriptionMapper methodToDescriptionMapper = new MethodToDescriptionMapper();
	IPermissionFilter permissionFilter = new PermissionFilter();
	IResponseConsumer responseConsumer = new ResponseConsumer();
	IDisplayTypeFilter displayTypeMapper = new DisplayTypeFilter(permissionFilter);
	Class<? extends AnnotatedCommandExecutor<E>> commandClass;
	IMethodInvoker<E> methodInvoker;
	@NonFinal
	AnnotatedCommand.OnUnknownSubCommandExecutionListener onUnknownSubCommandExecutionListener;
	@NonFinal
	AnnotatedCommand.OnInsufficientPermissionsListener onInsufficientPermissionsListener;
	@NonFinal
	AnnotatedCommand.OnMainCommandExecutionListener onMainCommandExecutionListener;
	@Getter(AccessLevel.PACKAGE)
	Object[] parameters;

	LocalCommandExecutor(final AnnotatedCommand<E> annotatedCommand, final Class<? extends AnnotatedCommandExecutor<E>> commandClass, final E plugin, final Object... parameters) {
		this.annotatedCommand = annotatedCommand;
		this.commandClass = commandClass;
		this.parameters = parameters;
		methodInvoker = new MethodInvoker<>(plugin, mappingConfig);
	}

	@Override
	public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
		if (args.length == 0) {
			Optional.ofNullable(onMainCommandExecutionListener)
					.ifPresentOrElse(listener -> listener.mainCommandExecution(sender), () ->
							Arrays.stream(commandClass.getDeclaredMethods())
									.filter(method -> !Modifier.isStatic(method.getModifiers()))
									.filter(method -> method.isAnnotationPresent(Argument.class))
									.filter(method -> displayTypeMapper.mapDisplayType(method, sender))
									.forEach(method -> sender.sendMessage(methodToDescriptionMapper.mapMethodToDescription(method, command.getName()))));
		} else {
			commandToMethodMapper
					.mapCommandToMethod(commandClass.getDeclaredMethods(), args, sender, annotatedCommand.getOptions())
					.ifPresentOrElse(method -> invokeMethod(method, sender, args), () -> noneMethodFound(sender));
		}
		return true;
	}

	/**
	 * Sets on unknown sub command execution listener.
	 *
	 * @param listener The {@link AnnotatedCommand.OnUnknownSubCommandExecutionListener}
	 */
	public void setOnUnknownSubCommandExecutionListener(final AnnotatedCommand.OnUnknownSubCommandExecutionListener listener) {
		onUnknownSubCommandExecutionListener = listener;
	}

	/**
	 * Sets on insufficient permissions' listener.
	 *
	 * @param listener The {@link AnnotatedCommand.OnInsufficientPermissionsListener}
	 */
	public void setOnInsufficientPermissionsListener(final AnnotatedCommand.OnInsufficientPermissionsListener listener) {
		onInsufficientPermissionsListener = listener;
	}

	/**
	 * Sets on main command execution  listener.
	 *
	 * @param listener The {@link AnnotatedCommand.OnMainCommandExecutionListener}
	 */
	public void setOnMainCommandExecutionListener(final AnnotatedCommand.OnMainCommandExecutionListener listener) {
		onMainCommandExecutionListener = listener;
	}

	<M> boolean addArgumentMapper(final String id, final MappingSet<M> mappingSet) {
		if (mappingConfig.exists(id)) {
			return false;
		}
		mappingConfig.add(id, mappingSet);
		return true;
	}

	<M> boolean addTypeMapper(final Class<M> clazz, final MappingSet<M> mappingSet) {
		if (mappingConfig.exists(clazz)) {
			return false;
		}
		mappingConfig.add(clazz, mappingSet);
		return true;
	}

	private void noneMethodFound(final CommandSender sender) {
		Optional.ofNullable(onUnknownSubCommandExecutionListener)
				.ifPresent(listener -> listener.unknownSubCommandExecuted(sender));
	}

	private void invokeMethod(final ExecutionCall method, final CommandSender sender, final String[] args) {
		if (permissionFilter.filterPermission(method.getMethod(), sender)) {
			final Object result = methodInvoker.invokeMethod(method, args, sender, commandClass, parameters);
			responseConsumer.consumeResponse(sender, result, method.getMethod().getAnnotation(Argument.class).responseType(), annotatedCommand.getOptions());
		} else if (onInsufficientPermissionsListener != null) {
			onInsufficientPermissionsListener.insufficientPermissions(sender);
		}
	}
}
