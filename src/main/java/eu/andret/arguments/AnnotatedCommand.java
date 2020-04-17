/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor(access = AccessLevel.PACKAGE)
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

	/**
	 * Adds the mapper that allows to instantly create matching type instead of expecting String.
	 *
	 * @param id The id of mapper that has to be unique. This is passed to {@link
	 * eu.andret.arguments.annotation.Param#value()} to precisely select the created mapper.
	 * @param clazz The {@link java.lang.Class} that will be returned from mapper function,
	 * @param mapper The {@link java.util.function.Function} that has the logic how to create the
	 * <code>clazz</code> object of String
	 * @param <T> The argument type that can be usd as the @{@link eu.andret.arguments.annotation.Argument}
	 * method's parameter
	 */
	public <T> void addArgumentMapper(String id, Class<T> clazz, Function<String, T> mapper) {
		Map<String, Mapper<?>> mappers = getLocalCommandExecutor().getMappers();
		if (mappers.containsKey(id)) {
			throw new IllegalArgumentException("Mapper with this id is already registered!");
		}
		mappers.put(id, new Mapper<>(clazz, mapper));
	}
}
