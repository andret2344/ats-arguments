/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.provider;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.api.annotation.BaseCommand;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

@BaseCommand("arguments")
public class ManyArgumentsClass extends AnnotatedCommandExecutor<JavaPlugin> {
	private final World world;

	public ManyArgumentsClass(final CommandSender sender, final JavaPlugin plugin, final World world) {
		super(sender, plugin);
		this.world = world;
	}

	@Argument
	public String world() {
		return world.toString();
	}
}
