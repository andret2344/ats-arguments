/*
 * Copyright Andret (c) 2019. Copying and modifying allowed only keeping git link reference.
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
	 * @param commandExecutorClass The {@link eu.andret.arguments.AnnotatedCommandExecutor}'s
	 * subclass
	 * @param plugin The {@link org.bukkit.plugin.java.JavaPlugin}'s subclass as main class of
	 * plugin
	 *
	 * @return AnnotatedCommand
	 */
	public static AnnotatedCommand registerCommand(Class<? extends AnnotatedCommandExecutor> commandExecutorClass,
												   JavaPlugin plugin) {
		BaseCommand annotation = commandExecutorClass.getAnnotation(BaseCommand.class);
		if (annotation == null) {
			throw new UnsupportedOperationException("Class not annotated with @" + BaseCommand.class.getName());
		}
		PluginCommand pluginCommand = plugin.getCommand(annotation.value());
		if (pluginCommand == null) {
			throw new UnsupportedOperationException("Command not registered in the plugin.yml file!");
		}
		pluginCommand.setExecutor(new LocalCommandExecutor(commandExecutorClass, plugin));
		pluginCommand.setTabCompleter(new LocalTabCompleter(commandExecutorClass));
		return new AnnotatedCommand(pluginCommand);
	}
}
