/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.api.annotation.SubCommand;
import eu.andret.arguments.consumer.IResponseConsumer;
import eu.andret.arguments.consumer.impl.ResponseConsumer;
import eu.andret.arguments.entity.ExecutionCall;
import eu.andret.arguments.entity.MappingConfig;
import eu.andret.arguments.filter.IDisplayTypeFilter;
import eu.andret.arguments.filter.IPermissionFilter;
import eu.andret.arguments.filter.impl.DisplayTypeFilter;
import eu.andret.arguments.filter.impl.PermissionFilter;
import eu.andret.arguments.mapper.ICommandToMethodMapper;
import eu.andret.arguments.mapper.IExceptionHandler;
import eu.andret.arguments.mapper.IFallbackSelector;
import eu.andret.arguments.mapper.IInstanceCreator;
import eu.andret.arguments.mapper.IMethodInvoker;
import eu.andret.arguments.mapper.IMethodSelector;
import eu.andret.arguments.mapper.IMethodToDescriptionMapper;
import eu.andret.arguments.mapper.impl.CommandToMethodMapper;
import eu.andret.arguments.mapper.impl.ExceptionHandler;
import eu.andret.arguments.mapper.impl.FallbackSelector;
import eu.andret.arguments.mapper.impl.InstanceCreator;
import eu.andret.arguments.mapper.impl.MethodInvoker;
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
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
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
	@Getter
	MappingConfig mappingConfig = new MappingConfig();
	ICommandToMethodMapper commandToMethodMapper = new CommandToMethodMapper(mappingConfig);
	IMethodToDescriptionMapper methodToDescriptionMapper = new MethodToDescriptionMapper();
	IPermissionFilter permissionFilter = new PermissionFilter();
	IResponseConsumer responseConsumer = new ResponseConsumer();
	IDisplayTypeFilter displayTypeMapper = new DisplayTypeFilter(permissionFilter);
	IFallbackSelector fallbackSelector = new FallbackSelector();
	IMethodSelector methodSelector = new MethodSelector(fallbackSelector, mappingConfig);
	IInstanceCreator instanceCreator = new InstanceCreator();
	IMethodInvoker methodInvoker = new MethodInvoker(mappingConfig);
	IExceptionHandler exceptionHandler = new ExceptionHandler(methodInvoker);
	@Getter(AccessLevel.PACKAGE)
	Class<? extends AnnotatedCommandExecutor<E>> commandClass;
	@NonFinal
	Consumer<CommandSender> onUnknownSubCommandExecutionListener;
	@NonFinal
	Consumer<CommandSender> onInsufficientPermissionsListener;
	@NonFinal
	Consumer<CommandSender> onMainCommandExecutionListener;
	@Getter(AccessLevel.PACKAGE)
	Object[] parameters;
	@NotNull
	CommandTree<E> commandTree;

	LocalCommandExecutor(@NotNull final AnnotatedCommand<E> annotatedCommand,
						 @NotNull final Class<? extends AnnotatedCommandExecutor<E>> commandClass,
						 @NotNull final E plugin,
						 @NotNull final Object... parameters) {
		this.annotatedCommand = annotatedCommand;
		this.commandClass = commandClass;
		this.plugin = plugin;
		this.parameters = parameters;
		commandTree = new CommandTree<>(commandClass);
	}

	@Override
	public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command,
							 @NotNull final String label, @NotNull final String[] args) {
		if (args.length == 0) {
			Optional.ofNullable(onMainCommandExecutionListener)
					.ifPresentOrElse(listener -> listener.accept(sender), () ->
							Arrays.stream(commandClass.getDeclaredMethods())
									.filter(method -> !Modifier.isStatic(method.getModifiers()))
									.filter(method -> method.isAnnotationPresent(Argument.class))
									.filter(method -> displayTypeMapper.mapDisplayType(method, sender))
									.forEach(method -> sender.sendMessage(methodToDescriptionMapper
											.mapMethodToDescription(method, command.getName()))));
		} else {
			commandToMethodMapper
					.mapCommandToMethod(commandTree, args, sender, annotatedCommand.getOptions())
					.ifPresentOrElse(method -> invokeMethod(method, sender, args), () -> noneMethodFound(sender));
		}
		return true;
	}

	/**
	 * Sets on unknown sub command execution listener.
	 *
	 * @param listener The {@link Consumer}.
	 */
	public void setOnUnknownSubCommandExecutionListener(@NotNull final Consumer<CommandSender> listener) {
		onUnknownSubCommandExecutionListener = listener;
	}

	/**
	 * Sets on insufficient permissions' listener.
	 *
	 * @param listener The {@link Consumer}.
	 */
	public void setOnInsufficientPermissionsListener(@NotNull final Consumer<CommandSender> listener) {
		onInsufficientPermissionsListener = listener;
	}

	/**
	 * Sets on main command execution listener.
	 *
	 * @param listener The {@link Consumer}.
	 */
	public void setOnMainCommandExecutionListener(@NotNull final Consumer<CommandSender> listener) {
		onMainCommandExecutionListener = listener;
	}

	private void noneMethodFound(@NotNull final CommandSender sender) {
		Optional.ofNullable(onUnknownSubCommandExecutionListener).ifPresent(listener -> listener.accept(sender));
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
			onInsufficientPermissionsListener.accept(sender);
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
				.map(method -> exceptionHandler.handleException(method, commandExecutor, executionCall.getData(), commandClass.getDeclaredMethods()))
				.flatMap(Collection::stream)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	@NotNull
	private AnnotatedCommandExecutor<E> getAnnotatedCommandExecutor(@NotNull final CommandSender sender) {
		if (executors.containsKey(sender)) {
			return executors.get(sender);
		}
		// TODO not commandClass, find in the tree the exact class
		final AnnotatedCommandExecutor<E> commandExecutor = instanceCreator.createInstance(sender, plugin, commandClass, parameters);
		executors.put(sender, commandExecutor);
		return commandExecutor;
	}

	@Nullable
	AnnotatedCommandExecutor<E> getCommandExecutor(@NotNull final CommandSender sender) {
		return executors.get(sender);
	}

	@SuppressWarnings("unchecked")
	void addSubCommand(@NotNull final Class<? extends AnnotatedCommandExecutor<E>> commandClass) {
		final SubCommand annotation = commandClass.getAnnotation(SubCommand.class);
		final Class<? extends AnnotatedCommandExecutor<E>> parent
				= (Class<? extends AnnotatedCommandExecutor<E>>) annotation.parent();
		final CommandTree<E>.Node found = commandTree.search(parent);
		if (found == null) {
			throw new IllegalArgumentException("Parent class is not registered!");
		}
		final boolean dupedValue = found.getChildren().stream()
				.map(CommandTree.Node.class::cast)
				.anyMatch(o -> getValue(o.getClazz()).equals(getValue(parent)));
		if (dupedValue) {
			throw new IllegalArgumentException("SubCommand with this value is already registered!");
		}
		found.add(commandClass);
	}

	@NotNull
	private String getValue(@NotNull final Class<? extends AnnotatedCommandExecutor<E>> clazz) {
		return clazz.getAnnotation(SubCommand.class).value();
	}
}
