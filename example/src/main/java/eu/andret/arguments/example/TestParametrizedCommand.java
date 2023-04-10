/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.example;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.api.annotation.BaseCommand;
import eu.andret.arguments.api.entity.ExecutorType;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@BaseCommand("parameters")
public class TestParametrizedCommand extends AnnotatedCommandExecutor<TestPlugin> {
	private final World world;
	private final int x;

	public TestParametrizedCommand(final CommandSender sender, final TestPlugin plugin, final World world, final int x) {
		super(sender, plugin);
		this.world = world;
		this.x = x;
	}

	@Argument(executorType = ExecutorType.PLAYER)
	public String amI() {
		return ((Player) sender).getWorld().equals(world) ? "Yes!" : "No...";
	}

	@SuppressWarnings("PrimitiveReturn")
	@Argument(executorType = ExecutorType.PLAYER)
	public int x() {
		final Player player = (Player) sender;
		final int blockX = player.getLocation().getBlockX();
		return blockX - x;
	}

	@Argument
	public String caseSensitive() {
		return "This is case sensitive command";
	}
}
