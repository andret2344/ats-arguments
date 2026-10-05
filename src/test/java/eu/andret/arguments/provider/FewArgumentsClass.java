package eu.andret.arguments.provider;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.annotation.BaseCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

@BaseCommand("malformed")
public final class FewArgumentsClass extends AnnotatedCommandExecutor<JavaPlugin> {
	@SuppressWarnings("ConstructorParameters")
	public FewArgumentsClass(final CommandSender sender) {
		super(sender, null);
	}
}
