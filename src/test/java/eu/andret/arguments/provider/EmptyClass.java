/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.provider;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.annotation.BaseCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

@BaseCommand("empty")
public class EmptyClass extends AnnotatedCommandExecutor<JavaPlugin> {
	public EmptyClass(final CommandSender sender, final JavaPlugin plugin) {
		super(sender, plugin);
	}
}
