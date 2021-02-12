/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.provider;

import eu.andret.arguments.AnnotatedCommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class EmptyClass extends AnnotatedCommandExecutor<JavaPlugin> {
	public EmptyClass(final CommandSender sender, final JavaPlugin plugin) {
		super(sender, plugin);
	}
}
