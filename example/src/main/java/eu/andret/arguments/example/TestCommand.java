/*
 * Copyright Andret (c) 2018-2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.example;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.annotation.Argument;
import eu.andret.arguments.annotation.BaseCommand;
import eu.andret.arguments.annotation.Completer;
import eu.andret.arguments.annotation.Fallback;
import eu.andret.arguments.annotation.Ignore;
import eu.andret.arguments.annotation.Param;
import eu.andret.arguments.entity.DisplayType;
import eu.andret.arguments.entity.ExecutorType;
import eu.andret.arguments.entity.ResponseType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Objects;
import java.util.OptionalDouble;

@BaseCommand(value = "test")
public class TestCommand extends AnnotatedCommandExecutor<TestPlugin> {
	public TestCommand(CommandSender sender, TestPlugin plugin) {
		super(sender, plugin);
	}

	@Argument(permission = "me.testing")
	public String testing() {
		// "/test testing", requires permission "me.testing", sender gets "I'm testing" or "I'm secretly testing"
		if (plugin.isSuperSecretSetting()) {
			return "I'm secretly testing!";
		}
		return "I'm testing!";
	}

	@Argument(responseType = ResponseType.BROADCAST)
	public String broadcast(String... message) {
		// "/test broadcast Welcome to the new server!", everyone on server gets "Welcome to the new server"
		return String.join(" ", message);
	}

	@Fallback
	public String player(String player) {
		return "The " + player + " is offline!";
	}

	@Argument
	public String player(@Param("basicPlayerMapper") @Completer("basicPlayerCompleter") Player player) {
		if (player == null) {
			// "/test player Andret2344", sender gets: "Who do you mean?"
			return "Who do you mean?";
		}
		// "/test player Andret2344", sender gets: "Hello Andret2344, your UUID is: 9070bdef-2c40-4cc9-8309-3fed2c648844"
		return "Hello " + player.getName() + ", your UUID is: " + player.getUniqueId();
	}

	@Argument(executorType = ExecutorType.PLAYER)
	public String distance(@Param("basicPlayerMapper") @Completer("basicPlayerCompleter") Player... players) {
		OptionalDouble min = Arrays.stream(players)
				.filter(Objects::nonNull)
				.mapToDouble(player -> player.getLocation().distance(((Player) sender).getLocation()))
				.min();
		if (min.isPresent()) {
			return "The shortest distance is " + min.getAsDouble() + ". Guess whom it is!";
		}
		return "No min distance could be found :(";
	}

	@Argument(displayType = DisplayType.NONE)
	public void notDisplayed() {
		// Argument won't be displayed when "/test" will be executed
	}

	@Argument(displayType = DisplayType.IF_PERMS, permission = "eu.andret.test.conditions")
	public void conditionallyDisplayed() {
		// Argument will be displayed when "/test" will be executed only if sender has permissions
	}

	@Argument(displayType = DisplayType.ALWAYS, permission = "eu.andret.test.conditions")
	public void alwaysDisplayed() {
		// Argument will be displayed when "/test" will be executed under no conditions
	}

	@Argument
	public String colored(boolean value) {
		// automatic suggestions with "true" and "false" will appear.
		// Response will be automatically colored.
		if (value) {
			return "&6You have found something. &dBye!";
		}
		return "&4Nothing to look at here. &bBye!";
	}

	@Argument
	public String ignored(@Ignore boolean value) {
		// No suggestions will appear.
		// Response will be automatically coloured.
		if (value) {
			return "&6I'm ignored.";
		}
		return "&6I'm ignored too.";
	}
}
