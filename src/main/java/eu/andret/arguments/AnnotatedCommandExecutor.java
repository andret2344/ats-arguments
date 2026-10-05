package eu.andret.arguments;

import eu.andret.arguments.api.annotation.Argument;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The command executor that is superclass for command class instead of {@link CommandExecutor}. It requires all methods
 * annotated with {@link Argument} to be non-static. Subclass should be passed as first argument of {@link
 * CommandManager#registerCommand(Class, JavaPlugin, Object...)}
 *
 * @param <T> The {@link JavaPlugin} that will be administrating commands.
 *
 * @author Andret
 * @since May 18, 2019
 */
public abstract class AnnotatedCommandExecutor<T extends JavaPlugin> {
	/**
	 * The Sender that performed the command.
	 */
	protected final CommandSender sender;
	/**
	 * The Plugin that uses this executor.
	 */
	protected final T plugin;

	/**
	 * A constructor.
	 *
	 * @param sender The Sender that performed the command.
	 * @param plugin The Plugin that uses this executor.
	 */
	protected AnnotatedCommandExecutor(final CommandSender sender, final T plugin) {
		this.sender = sender;
		this.plugin = plugin;
	}

	/**
	 * Gets the sender that performed the command.
	 *
	 * @return The Sender that performed the command.
	 */
	public CommandSender getSender() {
		return sender;
	}

	/**
	 * Gets the plugin that uses this executor.
	 *
	 * @return The Plugin that uses this executor.
	 */
	public T getPlugin() {
		return plugin;
	}
}
