/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */
package eu.andret.arguments;

import eu.andret.arguments.annotation.BaseCommand;
import lombok.experimental.UtilityClass;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Basic class for registering annotated methods.
 *
 * @author Andret
 * @since Jun 02, 2019
 */
@UtilityClass
public class CommandManager {
	/**
	 * Method registering new command classes.
	 *
	 * @param commandClass The class extending {@link eu.andret.arguments.AnnotatedCommandExecutor}
	 * @param plugin The class extending {@link org.bukkit.plugin.java.JavaPlugin} as main class of
	 * plugin
	 *
	 * @return AnnotatedCommand
	 */
	public AnnotatedCommand registerCommand(Class<? extends AnnotatedCommandExecutor> commandClass, JavaPlugin plugin) {
		BaseCommand annotation = commandClass.getAnnotation(BaseCommand.class);
		if (annotation == null) {
			throw new UnsupportedOperationException("Class not annotated with @" + BaseCommand.class.getName());
		}
		PluginCommand pluginCommand = plugin.getCommand(annotation.value());
		if (pluginCommand == null) {
			throw new UnsupportedOperationException("Command not registered in the plugin.yml file!");
		}
		pluginCommand.setExecutor(new LocalCommandExecutor(commandClass, plugin));
		pluginCommand.setTabCompleter(new LocalTabCompleter(commandClass));
		return new AnnotatedCommand(pluginCommand);
	}
}
