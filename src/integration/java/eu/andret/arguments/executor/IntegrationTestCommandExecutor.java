/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.executor;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.api.annotation.BaseCommand;
import eu.andret.arguments.api.annotation.Mapper;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@BaseCommand("IntegrationTest")
public class IntegrationTestCommandExecutor extends AnnotatedCommandExecutor<JavaPlugin> {
	public IntegrationTestCommandExecutor(final CommandSender sender, final JavaPlugin plugin) {
		super(sender, plugin);
	}

	@Argument
	public String testWithoutParameters() {
		return "none";
	}

	@Argument
	public int testWithParameter(final int x) {
		return x;
	}

	@Argument
	public String testWithParameter(final String s) {
		return s;
	}

	@Argument(position = 1)
	public String testWithChangedPosition(final String x) {
		return x;
	}

	@Argument(permission = "test")
	public String testWithPermission() {
		return "permission";
	}

	@Argument
	public String testWithInt(final int i) {
		return "An int: " + i;
	}

	@Argument
	public String testWithWorld(final World w) {
		return "The world: " + w.getName();
	}

	@Argument
	public String testWithPlayer(final Player p) {
		return "The player: " + p.getName();
	}

	@Argument
	public String testWithWorldMapper(@Mapper("world") final World w) {
		return "The mapped world: " + w.getName();
	}

	@Argument
	public String testWithPlayerMapper(@Mapper("player") final Player p) {
		return "The mapped player: " + p.getName();
	}
}
