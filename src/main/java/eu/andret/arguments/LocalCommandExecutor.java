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
import eu.andret.arguments.entity.ResponseMappingSet;
import eu.andret.arguments.filter.IDisplayTypeFilter;
import eu.andret.arguments.filter.IPermissionFilter;
import eu.andret.arguments.filter.impl.DisplayTypeFilter;
import eu.andret.arguments.filter.impl.PermissionFilter;
import eu.andret.arguments.mapper.ICommandToMethodMapper;
import eu.andret.arguments.mapper.IFallbackSelector;
import eu.andret.arguments.mapper.IInstanceCreator;
import eu.andret.arguments.mapper.IInstanceCreator;
import eu.andret.arguments.mapper.IMethodSelector;
import eu.andret.arguments.mapper.IMethodToDescriptionMapper;
import eu.andret.arguments.mapper.impl.CommandToMethodMapper;
import eu.andret.arguments.mapper.impl.FallbackSelector;
import eu.andret.arguments.mapper.impl.InstanceCreator;
import eu.andret.arguments.mapper.impl.InstanceCreator;
import eu.andret.arguments.mapper.impl.MethodSelector;
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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
	JavaPlugin plugin;
	Map<CommandSender, AnnotatedCommandExecutor<E>> executors = new HashMap<>();
	AnnotatedCommand<E> annotatedCommand;
	MappingConfig mappingConfig = new MappingConfig();
	ICommandToMethodMapper commandToMethodMapper = new CommandToMethodMapper(mappingConfig);
	IMethodToDescriptionMapper methodToDescriptionMapper = new MethodToDescriptionMapper();
	IPermissionFilter permissionFilter = new PermissionFilter();
	IResponseConsumer responseConsumer = new ResponseConsumer();
	IDisplayTypeFilter displayTypeMapper = new DisplayTypeFilter(permissionFilter);
	IFallbackSelector<E> fallbackSelector = new FallbackSelector<>();
	IMethodSelector methodSelector = new MethodSelector<>(fallbackSelector, mappingConfig);
	IInstanceCreator<E> instanceCreator = new InstanceCreator<>();
	IMethodInvoker<E> methodInvoker = new MethodInvoker<>(mappingConfig);
	Class<? extends AnnotatedCommandExecutor<E>> commandClass;
	@NonFinal
	AnnotatedCommand.OnUnknownSubCommandExecutionListener onUnknownSubCommandExecutionListener;
	@NonFinal
	AnnotatedCommand.OnInsufficientPermissionsListener onInsufficientPermissionsListener;
	@NonFinal
	AnnotatedCommand.OnMainCommandExecutionListener onMainCommandExecutionListener;
	@Getter(AccessLevel.PACKAGE)
	Object[] parameters;

	LocalCommandExecutor(@NotNull final AnnotatedCommand<E> annotatedCommand,
						 @NotNull final Class<? extends AnnotatedCommandExecutor<E>> commandClass,
						 @NotNull final E plugin,
						 @NotNull final Object... parameters) {
		this.annotatedCommand = annotatedCommand;
		this.commandClass = commandClass;
		this.parameters = parameters;
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command,
							 @NotNull final String label, @NotNull final String[] args) {
		if (args.length == 0) {
			Optional.ofNullable(onMainCommandExecutionListener)
					.ifPresentOrElse(listener -> listener.mainCommandExecution(sender), () ->
							Arrays.stream(commandClass.getDeclaredMethods())
									.filter(method -> !Modifier.isStatic(method.getModifiers()))
									.filter(method -> method.isAnnotationPresent(Argument.class))
									.filter(method -> displayTypeMapper.mapDisplayType(method, sender))
									.forEach(method -> sender.sendMessage(methodToDescriptionMapper
											.mapMethodToDescription(method, command.getName()))));
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
	public void setOnUnknownSubCommandExecutionListener(
			@NotNull final AnnotatedCommand.OnUnknownSubCommandExecutionListener listener) {
		onUnknownSubCommandExecutionListener = listener;
	}

	/**
	 * Sets on insufficient permissions' listener.
	 *
	 * @param listener The {@link AnnotatedCommand.OnInsufficientPermissionsListener}
	 */
	public void setOnInsufficientPermissionsListener(
			@NotNull final AnnotatedCommand.OnInsufficientPermissionsListener listener) {
		onInsufficientPermissionsListener = listener;
	}

	/**
	 * Sets on main command execution listener.
	 *
	 * @param listener The {@link AnnotatedCommand.OnMainCommandExecutionListener}
	 */
	public void setOnMainCommandExecutionListener(
			@NotNull final AnnotatedCommand.OnMainCommandExecutionListener listener) {
		onMainCommandExecutionListener = listener;
	}

	<M> boolean addArgumentMapper(@NotNull final String id, @NotNull final MappingSet<M> mappingSet) {
		if (mappingConfig.existsArgumentMapper(id)) {
			return false;
		}
		mappingConfig.addArgumentMapper(id, mappingSet);
		return true;
	}

	<M> boolean addTypeMapper(@NotNull final Class<M> clazz, @NotNull final MappingSet<M> mappingSet) {
		if (mappingConfig.existsTypeResponseMapper(clazz)) {
			return false;
		}
		mappingConfig.addTypeMapper(clazz, mappingSet);
		return true;
	}

	<T> boolean addTypeResponseMapper(@NotNull final Class<T> clazz, @NotNull final ResponseMappingSet<T> responseMappingSet) {
		if (mappingConfig.existsTypeResponseMapper(clazz)) {
			return false;
		}
		mappingConfig.addTypeResponseMapper(clazz, responseMappingSet);
		return true;
	}

	<T> boolean addArgumentResponseMapper(@NotNull final String id, @NotNull final ResponseMappingSet<T> responseMappingSet) {
		if (mappingConfig.existsArgumentResponseMapper(id)) {
			return false;
		}
		mappingConfig.addArgumentResponseMapper(id, responseMappingSet);
		return true;
	}

	private void noneMethodFound(@NotNull final CommandSender sender) {
		Optional.ofNullable(onUnknownSubCommandExecutionListener)
				.ifPresent(listener -> listener.unknownSubCommandExecuted(sender));
	}

	private void invokeMethod(@NotNull final Method method, @NotNull final CommandSender sender,
							  @NotNull final String[] args) {
		if (permissionFilter.filterPermission(method, sender)) {
			Optional.of(selectMethod(method, args))
					.map(executionCall -> invokeMethods(executionCall, sender))
					.stream()
					.flatMap(Collection::stream)
					.forEach(value -> responseConsumer.consumeResponse(sender, value, annotatedCommand.getOptions()));
		} else if (onInsufficientPermissionsListener != null) {
			onInsufficientPermissionsListener.insufficientPermissions(sender);
		}
	}

	@NotNull
	private ExecutionCall selectMethod(@NotNull final Method method, @NotNull final String[] command) {
		try {
			final Object[] data = methodSelector.recalculateArguments(method, command);
			return new ExecutionCall(List.of(method), data);
		} catch (final FallbackException ex) {
			final List<Method> methods = fallbackSelector.selectFallback(ex.getMapper(), ex.getTargetClass(), commandClass);
			return new ExecutionCall(methods, new Object[]{ex.getValue()});
		}
	}

	@NotNull
	private List<String> invokeMethods(@NotNull final ExecutionCall executionCall, @NotNull final CommandSender sender) {
		final AnnotatedCommandExecutor<E> commandExecutor = getAnnotatedCommandExecutor(sender);
		return executionCall.getMethods().stream()
				.map(method -> methodInvoker.invokeMethod(method, commandExecutor, executionCall.getData()))
				.flatMap(Collection::stream)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	@NotNull
	private AnnotatedCommandExecutor<E> getAnnotatedCommandExecutor(@NotNull final CommandSender sender) {
		if (executors.containsKey(sender)) {
			return executors.get(sender);
		}
		final AnnotatedCommandExecutor<E> commandExecutor = instanceCreator.createInstance(sender, plugin, commandClass, parameters);
		executors.put(sender, commandExecutor);
		return commandExecutor;
	}

	@SneakyThrows
	private Object invokeMethod(@NotNull final Method method,
								@NotNull final AnnotatedCommandExecutor<E> executor,
								@NotNull final Object[] data) {
		return method.invoke(executor, data);
	private AnnotatedCommandExecutor<E> getAnnotatedCommandExecutor(@NotNull final CommandSender sender) {
		if (executors.containsKey(sender)) {
			return executors.get(sender);
		}
		final AnnotatedCommandExecutor<E> commandExecutor = instanceCreator.createInstance(sender, plugin, commandClass, parameters);
		executors.put(sender, commandExecutor);
		return commandExecutor;
	}

	AnnotatedCommandExecutor<E> getCommandExecutor(@NotNull final CommandSender sender) {
		return executors.get(sender);
	}
}
