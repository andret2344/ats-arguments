/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.example;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.api.annotation.ArgumentFallback;
import eu.andret.arguments.api.annotation.ArgumentResponse;
import eu.andret.arguments.api.annotation.BaseCommand;
import eu.andret.arguments.api.annotation.Completer;
import eu.andret.arguments.api.annotation.Ignore;
import eu.andret.arguments.api.annotation.Mapper;
import eu.andret.arguments.api.annotation.TypeFallback;
import eu.andret.arguments.api.entity.DisplayType;
import eu.andret.arguments.api.entity.ExecutorType;
import eu.andret.arguments.example.entity.SomeEnum;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;

@SuppressWarnings("CheckStyle")
@BaseCommand(value = "test")
public class TestCommand extends AnnotatedCommandExecutor<TestPlugin> {
	public TestCommand(final CommandSender sender, final TestPlugin plugin) {
		super(sender, plugin);
	}

	@Argument(permission = "me.testing")
	public String running() {
		// "/test running", requires permission "me.testing", sender gets "I'm testing" or "I'm secretly testing"
		if (plugin.isSuperSecretSetting()) {
			return "I'm secretly testing!";
		}
		return "I'm testing!";
	}

	@ArgumentFallback("playerMapper")
	public String playerFallback(final String player) {
		return "The player \"" + player + "\" is offline!";
	}

	@Argument
	public String player(@Mapper("playerMapper") @Completer("playerCompleter") final Player player) {
		if (player == null) {
			// "/test player Andret2344", sender gets: "Who do you mean?"
			return "Who do you mean?";
		}
		// "/test player Andret2344", sender gets: "Hello Andret2344, your UUID is: 9070bdef-2c40-4cc9-8309-3fed2c648844"
		return "Hello " + player.getName() + ", your UUID is: " + player.getUniqueId();
	}

	@Argument(executorType = ExecutorType.PLAYER)
	public String distance(@Mapper("playerMapper") @Completer("playerCompleter") final Player... players) {
		final OptionalDouble min = Arrays.stream(players)
				.filter(Objects::nonNull)
				.mapToDouble(player -> player.getLocation().distance(((Player) sender).getLocation()))
				.min();
		if (min.isPresent()) {
			return "The shortest distance is " + min.getAsDouble() + ". Guess whom it is!";
		}
		return "No min distance could be found :(";
	}

	@Argument(displayType = DisplayType.NONE)
	public String notDisplayed() {
		// Argument won't be displayed when "/test" is executed
		return "Not displayed";
	}

	@Argument(displayType = DisplayType.IF_PERMS, permission = "eu.andret.test.conditions")
	public String conditionallyDisplayed() {
		// Argument will be displayed when "/test" will be executed only if sender has permissions
		return "Conditionally displayed";
	}

	@Argument(displayType = DisplayType.ALWAYS, permission = "eu.andret.test.conditions")
	public String alwaysDisplayed() {
		// Argument will be displayed when "/test" will be executed under no conditions
		return "Always displayed";
	}

	@Argument
	public String colored(final boolean value) {
		// automatic suggestions with "true" and "false" will appear.
		// ArgumentResponse will be automatically colored.
		if (value) {
			return "&6You have found something. &dBye!";
		}
		return "&4Nothing to look at here. &bBye!";
	}

	@Argument
	public String ignored(@Ignore final boolean value) {
		// No suggestions will appear.
		// ArgumentResponse will be automatically coloured.
		if (value) {
			return "&6I'm ignored.";
		}
		return "&6I'm ignored too.";
	}

	@Argument
	public String overloaded() {
		return "Overloaded with no arguments";
	}

	@Argument
	public String overloaded(final int argument) {
		return "Overloaded with one integer argument: " + argument;
	}

	@Argument
	public String overloaded(@Mapper("playerMapper") final Player player) {
		return "Overloaded with one player argument: " + player.getName();
	}

	@Argument
	public String overloaded(final int argument1, final int argument2) {
		return "Overloaded with two integer arguments: " + argument1 + ", " + argument2;
	}

	@Argument
	public String overloaded(@Mapper("playerMapper") final Player player, final int argument) {
		return "Overloaded with two arguments: " + player.getName() + ", " + argument;
	}

	@Argument
	public String[] array() {
		return new String[]{"First array line", "Second array line"};
	}

	@Argument
	public List<String> list() {
		return Arrays.asList("First list line", "second list line");
	}

	@Argument
	public String amI(final World world) {
		return ((Player) sender).getWorld().equals(world) ? "Yes!" : "No...";
	}

	@TypeFallback(World.class)
	public String worldFallback(final String world) {
		return "The world \"" + world + "\" is invalid!";
	}

	@Argument
	public String enumeration(final SomeEnum someEnum) {
		return someEnum.name();
	}

	@Argument(executorType = ExecutorType.PLAYER)
	public Player getMe() {
		return ((Player) sender);
	}

	@ArgumentResponse("world")
	@Argument(executorType = ExecutorType.PLAYER)
	public World getMyWorld() {
		return ((Player) sender).getWorld();
	}
}
