/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */
package eu.andret.arguments;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The command executor that is superclass for command class instead of {@link
 * org.bukkit.command.CommandExecutor}. It requires all methods annotated with {@link
 * eu.andret.arguments.annotation.Argument} to be non-static. Subclass should be passed as first
 * argument of {@link eu.andret.arguments.LocalCommandExecutor#LocalCommandExecutor(Class,
 * org.bukkit.plugin.java.JavaPlugin)}
 *
 * @author Andret
 * @see org.bukkit.command.CommandExecutor
 * @see eu.andret.arguments.annotation.Argument
 * @see eu.andret.arguments.LocalCommandExecutor#LocalCommandExecutor(Class,
 * org.bukkit.plugin.java.JavaPlugin)
 * @since May 18, 2019
 */
@AllArgsConstructor
@Value
@NonFinal
public abstract class AnnotatedCommandExecutor {
	/**
	 * The Sender that performed the command.
	 */
	protected CommandSender sender;
	/**
	 * The Plugin that uses this executor.
	 */
	protected JavaPlugin plugin;
}
