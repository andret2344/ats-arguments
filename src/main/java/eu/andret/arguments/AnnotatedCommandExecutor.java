/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.api.annotation.Argument;
import lombok.Value;
import lombok.experimental.NonFinal;
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
@Value
@NonFinal
public abstract class AnnotatedCommandExecutor<T extends JavaPlugin> {
	/**
	 * The Sender that performed the command.
	 */
	protected CommandSender sender;
	/**
	 * The Plugin that uses this executor.
	 */
	protected T plugin;
}
