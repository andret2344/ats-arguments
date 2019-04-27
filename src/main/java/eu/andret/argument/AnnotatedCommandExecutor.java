/*
 * Copyright Andret (c) 2019. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.argument;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Andret
 */
@AllArgsConstructor
@Value
@NonFinal
public abstract class AnnotatedCommandExecutor {
	protected final CommandSender sender;
	protected final JavaPlugin plugin;
}
