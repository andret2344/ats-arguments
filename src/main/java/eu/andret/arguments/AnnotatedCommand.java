/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.entity.Mapper;
import lombok.Data;
import lombok.Value;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Wrapper class for classical {@link org.bukkit.command.PluginCommand}.
 *
 * @author Andret
 * @since Jun 02, 2019
 */
@Value
public class AnnotatedCommand {
	PluginCommand command;
	Options options = new Options();

	AnnotatedCommand(PluginCommand command) {
		this.command = command;
		getLocalCommandExecutor().setOptions(options);
	}

	/**
	 * The Options to manipulate the behavior
	 */
	@Data
	public static class Options {
		private boolean autoTranslateColors;
	}

	/**
	 * Listener to define action when sender performs unknown sub-command.
	 */
	public interface OnUnknownSubCommandExecutionListener {

		/**
		 * Unknown sub-command executed.
		 *
		 * @param sender The sender that executed the unknown sub-command
		 */
		void unknownSubCommandExecuted(CommandSender sender);
	}

	/**
	 * Listener to define action when sender has insufficient permissions.
	 */
	public interface OnInsufficientPermissionsListener {

		/**
		 * Insufficient permissions.
		 *
		 * @param sender The sender that executed the command with no permissions.
		 */
		void insufficientPermissions(CommandSender sender);
	}

	LocalCommandExecutor getLocalCommandExecutor() {
		return (LocalCommandExecutor) command.getExecutor();
	}

	LocalTabCompleter getLocalTabCompleter() {
		return (LocalTabCompleter) command.getTabCompleter();
	}

	/**
	 * Sets on unknown sub command execution listener.
	 *
	 * @param listener The {@link OnUnknownSubCommandExecutionListener}.
	 */
	public void setOnUnknownSubCommandExecutionListener(OnUnknownSubCommandExecutionListener listener) {
		getLocalCommandExecutor().setOnUnknownSubCommandExecutionListener(listener);
	}

	/**
	 * Sets on insufficient permissions' listener.
	 *
	 * @param listener The {@link OnInsufficientPermissionsListener}.
	 */
	public void setOnInsufficientPermissionsListener(OnInsufficientPermissionsListener listener) {
		getLocalCommandExecutor().setOnInsufficientPermissionsListener(listener);
	}

	/**
	 * Adds the mapper that allows to instantly create matching type instead of expecting String.
	 *
	 * @param id The id of mapper that has to be unique. This is passed to {@link
	 *        eu.andret.arguments.annotation.Param#value()} to precisely select the created mapper.
	 * @param clazz The {@link java.lang.Class} that will be returned from mapper function,
	 * @param mapper The {@link java.util.function.Function} that has the logic how to create the
	 *        {@code clazz} object of String
	 * @param fallbackCondition The {@link Predicate<T>} that will verify if fallback should execute.
	 * @param <T> The argument type that can be usd as the @{@link eu.andret.arguments.annotation.Argument}
	 * 		method's parameter
	 */
	public <T> void addArgumentMapper(String id, Class<T> clazz, Function<String, T> mapper, Predicate<Object> fallbackCondition) {
		if (!getLocalCommandExecutor().addMapper(id, new Mapper<>(clazz, mapper, fallbackCondition))) {
			throw new IllegalArgumentException("Mapper with this id is already registered!");
		}
	}

	/**
	 * Adds the mapper that allows to instantly create matching type instead of expecting String.
	 *
	 * @param id The id of mapper that has to be unique. This is passed to {@link
	 *        eu.andret.arguments.annotation.Param#value()} to precisely select the created mapper.
	 * @param clazz The {@link java.lang.Class} that will be returned from mapper function,
	 * @param mapper The {@link java.util.function.Function} that has the logic how to create the
	 *        {@code clazz} object of String
	 * @param <T> The argument type that can be usd as the @{@link eu.andret.arguments.annotation.Argument}
	 * 		method's parameter
	 */
	public <T> void addArgumentMapper(String id, Class<T> clazz, Function<String, T> mapper) {
		addArgumentMapper(id, clazz, mapper, Objects::isNull);
	}

	/**
	 * The config method that changes the color translation behavior.
	 *
	 * @param autoTranslateColors If chat color sign {@code &} should be translated in responses
	 * 		into real color, no.
	 */
	public void setAutoTranslateColors(boolean autoTranslateColors) {
		options.setAutoTranslateColors(autoTranslateColors);
	}
}
