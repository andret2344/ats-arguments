package eu.andret.arguments.provider;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.annotation.BaseCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

@BaseCommand("malformed")
public final class ExceptionalClass extends AnnotatedCommandExecutor<JavaPlugin> {
	public ExceptionalClass(final CommandSender sender, final JavaPlugin plugin) {
		super(sender, plugin);
		throw new IllegalArgumentException();
	}
}
