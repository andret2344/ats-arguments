/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */
package eu.andret.arguments;

import eu.andret.arguments.mapper.ICommandToMethodMapper;
import eu.andret.arguments.mapper.impl.CommandToMethodMapper;
import eu.andret.arguments.mapper.impl.MethodInvoker;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

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
	private final ICommandToMethodMapper commandToMethodMapper = new CommandToMethodMapper();
	private final Class<? extends AnnotatedCommandExecutor> executor;
	private final MethodInvoker methodInvoker;
	@Getter
	private final Map<String, Mapper<?>> mappers = new HashMap<>();

	/**
	 * Constructs the LocalCommandExecutor.
	 *
	 * @param executor The {@link AnnotatedCommandExecutor} that will be analyzed in search of
	 * methods annotated with {@link eu.andret.arguments.annotation.Argument}
	 * @param plugin The {@link org.bukkit.plugin.java.JavaPlugin} superclass of main plugin class.
	 */
	LocalCommandExecutor(Class<? extends AnnotatedCommandExecutor> executor, JavaPlugin plugin) {
		this.executor = executor;
		methodInvoker = new MethodInvoker(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		commandToMethodMapper
				.mapCommandToMethod(executor, args, sender, mappers)
				.ifPresent(method -> methodInvoker.invokeMethod(method, args, sender, executor));
		return true;
	}
}
