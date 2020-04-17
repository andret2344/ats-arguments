/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */
package eu.andret.arguments;

import eu.andret.arguments.mapper.ICommandToMethodMapper;
import eu.andret.arguments.mapper.impl.CommandToMethodMapper;
import eu.andret.arguments.mapper.impl.MethodInvoker;
import eu.andret.arguments.mapper.impl.MethodToDescriptionMapper;
import eu.andret.arguments.mapper.impl.PermissionMapper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

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
	private final MethodToDescriptionMapper mapper = new MethodToDescriptionMapper();
	private final PermissionMapper permissionMapper = new PermissionMapper();
	private final Class<? extends AnnotatedCommandExecutor> executor;
	private final MethodInvoker methodInvoker;
	private AnnotatedCommand.OnUnknownSubCommandExecutionListener onUnknownSubCommandExecutionListener;
	private AnnotatedCommand.OnInsufficientPermissionsListener onInsufficientPermissionsListener;

	/**
	 * Constructs the LocalCommandExecutor.
	 *
	 * @param executor The {@link AnnotatedCommandExecutor} that will be analyzed in search of
	 * methods annotated with {@link eu.andret.arguments.annotation.Argument}
	 * @param plugin The {@link org.bukkit.plugin.java.JavaPlugin} superclass of main plugin class.
	 */
	LocalCommandExecutor(Class<? extends AnnotatedCommandExecutor> executor, JavaPlugin plugin) {
		this.executor = executor;
		methodInvoker = new MethodInvoker(plugin, mappers);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			Arrays.stream(executor.getDeclaredMethods())
					.forEach(method -> sender.sendMessage(mapper.mapMethodToDescription(method, cmd.getName())));
		} else {
			commandToMethodMapper
					.mapCommandToMethod(executor, args, sender)
					.ifPresentOrElse(method -> {
								if (permissionMapper.mapPermission(method, sender)) {
									methodInvoker.invokeMethod(method, args, sender, executor);
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
