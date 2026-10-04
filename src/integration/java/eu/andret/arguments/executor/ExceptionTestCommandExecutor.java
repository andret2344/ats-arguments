package eu.andret.arguments.executor;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.api.annotation.BaseCommand;
import eu.andret.arguments.api.annotation.ExceptionFallback;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

@BaseCommand("ExceptionTestCommandExecutor")
public class ExceptionTestCommandExecutor extends AnnotatedCommandExecutor<JavaPlugin> {
	public ExceptionTestCommandExecutor(final CommandSender sender, final JavaPlugin plugin) {
		super(sender, plugin);
	}

	@Argument
	public String throwCheckedException() throws IllegalAccessException {
		throw new IllegalAccessException("on purpose");
	}

	@Argument
	public String throwUncheckedException() {
		throw new IllegalArgumentException("on purpose");
	}

	@ExceptionFallback(IllegalArgumentException.class)
	public String catchException() {
		return "no parameter";
	}

	@ExceptionFallback(IllegalArgumentException.class)
	public String catchException(final IllegalArgumentException exception) {
		return exception.getMessage();
	}
}
