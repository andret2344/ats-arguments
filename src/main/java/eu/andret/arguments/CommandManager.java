/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.api.annotation.BaseCommand;
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
	 * @param commandClass The class extending {@link AnnotatedCommandExecutor}
	 * @param plugin The class extending {@link JavaPlugin} as main class of plugin
	 * @param arguments The arguments that will be passed into {@link AnnotatedCommandExecutor} constructor.
	 * @param <E> The main plugin class that extends {@link JavaPlugin}.
	 *
	 * @return AnnotatedCommand
	 */
	public <E extends JavaPlugin> AnnotatedCommand<E> registerCommand(final Class<? extends AnnotatedCommandExecutor<E>> commandClass,
																	  final E plugin,
																	  final Object... arguments) {
		final BaseCommand annotation = commandClass.getAnnotation(BaseCommand.class);
		if (annotation == null) {
			throw new UnsupportedOperationException("Class not annotated with @" + BaseCommand.class.getName());
		}
		final PluginCommand pluginCommand = plugin.getCommand(annotation.value());
		if (pluginCommand == null) {
			throw new UnsupportedOperationException("Command not registered in the plugin.yml file!");
		}
		final AnnotatedCommand<E> annotatedCommand = new AnnotatedCommand<>(pluginCommand);
		pluginCommand.setExecutor(new LocalCommandExecutor<>(annotatedCommand, commandClass, plugin, arguments));
		pluginCommand.setTabCompleter(new LocalTabCompleter<>(annotatedCommand, commandClass));
		return annotatedCommand;
	}
}
