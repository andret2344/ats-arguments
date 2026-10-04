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
	@SuppressWarnings("PrimitiveReturn")
	public int testWithParameter(final int number) {
		return number;
	}

	@Argument
	public String testWithParameter(final String text) {
		return text;
	}

	@Argument(position = 1)
	public String testWithChangedPosition(final String text) {
		return text;
	}

	@Argument(permission = "test")
	public String testWithPermission() {
		return "permission";
	}

	@Argument
	public String testWithInt(final int number) {
		return "An int: " + number;
	}

	@Argument
	public String testWithWorld(final World world) {
		return "The world: " + world.getName();
	}

	@Argument
	public String testWithPlayer(final Player player) {
		return "The player: " + player.getName();
	}

	@Argument
	public String testWithWorldMapper(@Mapper("world") final World world) {
		return "The mapped world: " + world.getName();
	}

	@Argument
	public String testWithPlayerMapper(@Mapper("player") final Player player) {
		return "The mapped player: " + player.getName();
	}
}
