package eu.andret.arguments;

import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.entity.ExecutionCall;
import eu.andret.arguments.entity.MappingConfig;
import eu.andret.arguments.filter.IDisplayTypeFilter;
import eu.andret.arguments.filter.IPermissionFilter;
import eu.andret.arguments.filter.impl.DisplayTypeFilter;
import eu.andret.arguments.filter.impl.PermissionFilter;
import eu.andret.arguments.mapper.ICommandToMethodMapper;
import eu.andret.arguments.mapper.IExceptionHandler;
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
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Local command executor, allows customization of commands behavior.
 *
 * @author Andret
 * @since May 18, 2020
 */
class LocalCommandExecutor<E extends JavaPlugin> implements CommandExecutor {
	private final JavaPlugin plugin;
	private final AnnotatedCommand<E> annotatedCommand;
	private final MappingConfig mappingConfig = new MappingConfig();
	private final ICommandToMethodMapper commandToMethodMapper = new CommandToMethodMapper(mappingConfig);
	private final IMethodToDescriptionMapper methodToDescriptionMapper = new MethodToDescriptionMapper();
	private final IPermissionFilter permissionFilter = new PermissionFilter();
	private final IDisplayTypeFilter displayTypeMapper = new DisplayTypeFilter(permissionFilter);
	private final IMethodSelector methodSelector = new MethodSelector(new FallbackSelector(), mappingConfig);
	private final IInstanceCreator instanceCreator = new InstanceCreator();
	private final IMethodInvoker methodInvoker = new MethodInvoker(mappingConfig);
	private final IExceptionHandler exceptionHandler = new ExceptionHandler(methodInvoker);
	private final Class<? extends AnnotatedCommandExecutor<E>> commandClass;
	private Consumer<CommandSender> onUnknownSubCommandExecutionListener;
	private Consumer<CommandSender> onInsufficientPermissionsListener;
	private Consumer<CommandSender> onMainCommandExecutionListener;
	private final Object[] parameters;

	LocalCommandExecutor(@NotNull final AnnotatedCommand<E> annotatedCommand,
			@NotNull final Class<? extends AnnotatedCommandExecutor<E>> commandClass,
			@NotNull final E plugin,
			@NotNull final Object... parameters) {
		this.annotatedCommand = annotatedCommand;
		this.commandClass = commandClass;
		this.plugin = plugin;
		this.parameters = parameters;
	}

	@Override
	public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command,
			@NotNull final String label, @NotNull final String @NotNull [] args) {
		if (args.length == 0) {
			Optional.ofNullable(onMainCommandExecutionListener)
					.ifPresentOrElse(listener -> listener.accept(sender), () ->
							Arrays.stream(commandClass.getDeclaredMethods())
									.filter(method -> !Modifier.isStatic(method.getModifiers()))
									.filter(method -> method.isAnnotationPresent(Argument.class))
									.filter(method -> displayTypeMapper.mapDisplayType(method, sender))
									.forEach(method -> sendMessage(sender, methodToDescriptionMapper
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
			Optional.of(methodSelector.selectMethod(method, args, commandClass))
					.map(executionCall -> invokeMethods(executionCall, sender))
					.stream()
					.flatMap(Collection::stream)
					.forEach(message -> sendMessage(sender, message));
		} else if (onInsufficientPermissionsListener != null) {
			onInsufficientPermissionsListener.accept(sender);
		}
	}

	@NotNull
	private List<String> invokeMethods(@NotNull final ExecutionCall executionCall, @NotNull final CommandSender sender) {
		try {
			final AnnotatedCommandExecutor<E> commandExecutor = instanceCreator.createInstance(sender, plugin, commandClass, parameters);
			final List<String> result = new ArrayList<>();
			for (final Method method : executionCall.methods()) {
				result.addAll(exceptionHandler.handleException(method, commandExecutor, executionCall.data(), commandClass.getDeclaredMethods()));
			}
			return result.stream()
					.filter(Objects::nonNull)
					.toList();
		} catch (final ReflectiveOperationException exception) {
			throw new IllegalStateException(exception);
		}
	}

	/**
	 * Gets the config of the mappers.
	 *
	 * @return The config of the mappers.
	 */
	@NotNull
	public MappingConfig getMappingConfig() {
		return mappingConfig;
	}

	private void sendMessage(@NotNull final CommandSender sender, @NotNull final String message) {
		if (annotatedCommand.getOptions().isAutoTranslateColors()) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
		} else {
			sender.sendMessage(message);
		}
	}
}
