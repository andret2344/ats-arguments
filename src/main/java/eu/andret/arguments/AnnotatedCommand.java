/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.annotation.Fallback;
import eu.andret.arguments.entity.Mapper;
import lombok.Data;
import lombok.Value;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Wrapper class for classical {@link org.bukkit.command.PluginCommand}.
 *
 * @author Andret
 * @since Jun 02, 2019
 */
@Value
public class AnnotatedCommand {
	PluginCommand command;

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
	 * @param fallbackCondition The {@link Predicate<T>} that will verify if fallback should
	 * 		execute.
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
	 * Fallback method will be never called.
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
		addArgumentMapper(id, clazz, mapper, Fallback.NEVER);
	}

	/**
	 * Adds the argument completer that allows to suggest values on command writing.
	 *
	 * @param id The id of completer that has to be unique. This is passed to {@link
	 *        eu.andret.arguments.annotation.Completer#value()} to precisely select the created
	 * 		completer.
	 * @param function The {@link java.util.function.Function} that will produce list of matching
	 * 		values on basis of the sender.
	 *
	 * @throws java.lang.IllegalArgumentException if tried to register duplicated id.
	 */
	public void addArgumentCompleter(String id, Function<CommandSender, Collection<String>> function) {
		if (!getLocalTabCompleter().addArgumentCompleter(id, function)) {
			throw new IllegalArgumentException("Completer with id \"" + id + "\" is already registered!");
		}
	}

	/**
	 * Adds the argument completer that allows to suggest values on command writing.
	 *
	 * @param id The id of completer that has to be unique. This is passed to {@link
	 *        eu.andret.arguments.annotation.Completer#value()} to precisely select the created
	 * 		completer.
	 * @param supplier The {@link java.util.function.Supplier} that will produce list of matching
	 * 		values.
	 *
	 * @throws java.lang.IllegalArgumentException if tried to register duplicated id.
	 */
	public void addArgumentCompleter(String id, Supplier<Collection<String>> supplier) {
		addArgumentCompleter(id, sender -> supplier.get());
	}

	/**
	 * Adds the argument completer that allows to suggest values on command writing.
	 *
	 * @param id The id of completer that has to be unique. This is passed to {@link
	 *        eu.andret.arguments.annotation.Completer#value()} to precisely select the created
	 * 		completer.
	 * @param collection The {@link java.util.Collection} that will be used as list of matching
	 * 		values.
	 *
	 * @throws java.lang.IllegalArgumentException if tried to register duplicated id.
	 */
	public void addArgumentCompleter(String id, Collection<String> collection) {
		addArgumentCompleter(id, () -> collection);
	}

	/**
	 * Adds the type completer that allows to suggest values on command writing.
	 *
	 * @param clazz The clazz that will be matched to completer.
	 * @param function The {@link java.util.function.Function} that will be used to create the
	 * 		list of matching values.
	 *
	 * @throws java.lang.IllegalArgumentException if tried to register duplicated clazz.
	 */
	public void addTypeCompleter(Class<?> clazz, Function<CommandSender, Collection<String>> function) {
		if (!getLocalTabCompleter().addTypeCompleter(clazz, function)) {
			throw new IllegalArgumentException("Completer for type " + clazz + " is already defined.");
		}
	}

	/**
	 * Adds the type completer that allows to suggest values on command writing.
	 *
	 * @param clazz The clazz that will be matched to completer.
	 * @param supplier The {@link java.util.function.Supplier} that will be used to create the
	 * 		list of matching values.
	 *
	 * @throws java.lang.IllegalArgumentException if tried to register duplicated clazz.
	 */
	public void addTypeCompleter(Class<?> clazz, Supplier<Collection<String>> supplier) {
		addTypeCompleter(clazz, sender -> supplier.get());
	}

	/**
	 * Adds the type completer that allows to suggest values on command writing.
	 *
	 * @param clazz The clazz that will be matched to completer.
	 * @param collection The {@link java.util.Collection} that will be the list of matching
	 * 		values.
	 *
	 * @throws java.lang.IllegalArgumentException if tried to register duplicated clazz.
	 */
	public void addTypeCompleter(Class<?> clazz, Collection<String> collection) {
		addTypeCompleter(clazz, () -> collection);
	}

	/**
	 * The config method that changes the color translation behavior.
	 *
	 * @param autoTranslateColors If chat color sign {@code &} should be translated in responses
	 * 		into real color, no.
	 */
	public void setAutoTranslateColors(boolean autoTranslateColors) {
		getLocalCommandExecutor().getOptions().setAutoTranslateColors(autoTranslateColors);
	}
}
