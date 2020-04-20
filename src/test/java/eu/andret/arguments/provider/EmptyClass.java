/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.provider;

import eu.andret.arguments.AnnotatedCommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class EmptyClass extends AnnotatedCommandExecutor {
	public EmptyClass(CommandSender sender, JavaPlugin plugin) {
		super(sender, plugin);
	}
}
