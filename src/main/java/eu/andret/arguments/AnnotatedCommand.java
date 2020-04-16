/*
 * Copyright Andret (c) 2019. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import lombok.Value;
import org.bukkit.command.PluginCommand;

import java.util.Map;
import java.util.function.Function;

/**
 * Wrapper class for classical {@link org.bukkit.command.PluginCommand}.
 *
 * @author Andret
 * @since Jun 02, 2019
 */
@Value
public class AnnotatedCommand {
	PluginCommand command;

	LocalCommandExecutor getLocalCommandExecutor() {
		return (LocalCommandExecutor) command.getExecutor();
	}

	LocalTabCompleter getLocalTabCompleter() {
		return (LocalTabCompleter) command.getTabCompleter();
	}

	/**
	 * Sets on unknown sub command execution listener.
	 *
	 * @param listener The {@link eu.andret.arguments.LocalCommandExecutor.OnUnknownSubCommandExecutionListener}.
	 */
	public void setOnUnknownSubCommandExecutionListener(LocalCommandExecutor.OnUnknownSubCommandExecutionListener listener) {
		getLocalCommandExecutor().setOnUnknownSubCommandExecutionListener(listener);
	}

	/**
	 * Sets on insufficient permissions' listener.
	 *
	 * @param listener The {@link eu.andret.arguments.LocalCommandExecutor.OnInsufficientPermissionsListener}.
	 */
	public void setOnInsufficientPermissionsListener(LocalCommandExecutor.OnInsufficientPermissionsListener listener) {
		getLocalCommandExecutor().setOnInsufficientPermissionsListener(listener);
	}

	/**
	 * Sets on usage example listener.
	 *
	 * @param listener The {@link eu.andret.arguments.LocalCommandExecutor.OnUsageExampleListener}.
	 */
	public void setOnUsageExampleListener(LocalCommandExecutor.OnUsageExampleListener listener) {
		getLocalCommandExecutor().setOnUsageExampleListener(listener);
	}

	public <T> void addArgumentMapper(String id, Class<T> clazz, Function<String, T> mapper) {
		Map<String, Mapper<?>> mappers = getLocalCommandExecutor().getMappers();
		if (mappers.containsKey(id)) {
			throw new IllegalArgumentException("Mapper with this id is already registered!");
		}
		mappers.put(id, new Mapper<>(clazz, mapper));
	}
}
