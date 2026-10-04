package eu.andret.arguments.provider;

import eu.andret.arguments.AnnotatedCommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("MissingBaseCommandAnnotation")
public class EmptyClass extends AnnotatedCommandExecutor<JavaPlugin> {
	public EmptyClass(final CommandSender sender, final JavaPlugin plugin) {
		super(sender, plugin);
	}
}
