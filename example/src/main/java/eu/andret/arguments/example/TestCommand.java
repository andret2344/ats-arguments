/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */
package eu.andret.arguments.example;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.ExecutorType;
import eu.andret.arguments.ResponseType;
import eu.andret.arguments.annotation.Argument;
import eu.andret.arguments.annotation.BaseCommand;
import eu.andret.arguments.annotation.Param;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.OptionalDouble;

@BaseCommand(value = "test")
public class TestCommand extends AnnotatedCommandExecutor {
	public TestCommand(CommandSender sender, TestPlugin plugin) {
		super(sender, plugin);
	}

	@Argument(permission = "me.testing")
	public String testing() {
		// "/test testing", requires permission "me.testing", sender gets "I'm testing"
		return "I'm testing!";
	}

	@Argument(responseType = ResponseType.BROADCAST)
	public String broadcast(String... message) {
		// "/test broadcast Welcome to the new server!", everyone on server gets "Welcome to the new server"
		return String.join(" ", message);
	}

	@Argument
	public String player(@Param("basicPlayerMapper") Player player) {
		// "/test player Andret2344", sender gets: "Hello Andret2344, your UUID is: 9070bdef-2c40-4cc9-8309-3fed2c648844
		return "Hello " + player.getName() + ", your UUID is: " + player.getUniqueId();
	}

	@Argument(executorType = ExecutorType.PLAYER)
	public String distance(@Param("basicPlayerMapper") Player... players) {
		// "/test player Andret2344", sender gets: "Hello Andret2344, your UUID is: 9070bdef-2c40-4cc9-8309-3fed2c648844
		OptionalDouble min = Arrays.stream(players)
				.mapToDouble(player -> player.getLocation().distance(((Player) sender).getLocation()))
				.min();
		if (min.isPresent()) {
			return "The shortest distance is " + min + ". Guess whom it is!";
		} else {
			return "No min distance could be found :(";
		}
	}
}
