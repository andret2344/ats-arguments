package eu.andret.arguments.example;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.ResponseType;
import eu.andret.arguments.annotation.Argument;
import eu.andret.arguments.annotation.BaseCommand;
import org.bukkit.command.CommandSender;

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
	public String broadcast(String[] message) {
		// "/test broadcast Welcome to the new server!", everyone on server gets "Welcome to the new server"
		return String.join(" ", message);
	}
}
