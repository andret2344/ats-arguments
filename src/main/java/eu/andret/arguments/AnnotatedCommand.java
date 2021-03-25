/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.api.annotation.Completer;
import eu.andret.arguments.api.annotation.Fallback;
import eu.andret.arguments.api.annotation.Mapper;
import eu.andret.arguments.entity.MappingSet;
import lombok.Data;
import lombok.Value;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Wrapper class for classical {@link PluginCommand}.
 *
 * @author Andret
 * @since Jun 02, 2019
 */
@Value
public class AnnotatedCommand<E extends JavaPlugin> {
	PluginCommand command;

	/**
	 * The Options to manipulate the behavior.
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

	@SuppressWarnings("unchecked")
	private LocalCommandExecutor<E> getLocalCommandExecutor() {
		return (LocalCommandExecutor<E>) command.getExecutor();
	}

	private LocalTabCompleter getLocalTabCompleter() {
		return (LocalTabCompleter) command.getTabCompleter();
	}

	/**
	 * Sets unknown sub command execution listener.
	 *
	 * @param listener The {@link OnUnknownSubCommandExecutionListener}.
	 */
	public void setOnUnknownSubCommandExecutionListener(final OnUnknownSubCommandExecutionListener listener) {
		getLocalCommandExecutor().setOnUnknownSubCommandExecutionListener(listener);
	}

	/**
	 * Sets insufficient permissions' listener.
	 *
	 * @param listener The {@link OnInsufficientPermissionsListener}.
	 */
	public void setOnInsufficientPermissionsListener(final OnInsufficientPermissionsListener listener) {
		getLocalCommandExecutor().setOnInsufficientPermissionsListener(listener);
	}

	/**
	 * Adds the mapper that allows to instantly create matching type instead of expecting {@link String}.
	 *
	 * @param clazz The {@link Class} that will be returned from mapper function,
	 * @param mapper The {@link Function} that has the logic how to create the {@code clazz} object of {@link
	 *        String}.
	 * @param fallbackCondition The {@link Predicate} that will verify if fallback should execute.
	 * @param <T> The argument type that can be usd as the @{@link Argument} method's parameter
	 */
	public <T> void addTypeMapper(final Class<T> clazz, final Function<String, T> mapper, final Predicate<Object> fallbackCondition) {
		if (!getLocalCommandExecutor().addTypeMapper(clazz, new MappingSet<>(clazz, mapper, fallbackCondition))) {
			throw new IllegalArgumentException("Mapper with this id is already registered!");
		}
	}

	/**
	 * Adds the mapper that allows to instantly create matching type instead of expecting {@link String}.
	 *
	 * @param clazz The {@link Class} that will be returned from mapper function,
	 * @param mapper The {@link Function} that has the logic how to create the {@code clazz} object of {@link
	 *        String}.
	 * @param <T> The argument type that can be usd as the @{@link Argument} method's parameter
	 */
	public <T> void addTypeMapper(final Class<T> clazz, final Function<String, T> mapper) {
		addTypeMapper(clazz, mapper, Fallback.NEVER);
	}

	/**
	 * Adds the mapper that allows to instantly create matching type instead of expecting {@link String}.
	 *
	 * @param id The id of mapper that has to be unique. This is passed to {@link Mapper#value()} to precisely select
	 * 		the created mapper.
	 * @param clazz The {@link Class} that will be returned from mapper function,
	 * @param mapper The {@link Function} that has the logic how to create the {@code clazz} object of {@link
	 *        String}.
	 * @param fallbackCondition The {@link Predicate} that will verify if fallback should execute.
	 * @param <T> The argument type that can be usd as the @{@link Argument} method's parameter
	 */
	public <T> void addArgumentMapper(final String id, final Class<T> clazz, final Function<String, T> mapper, final Predicate<Object> fallbackCondition) {
		if (!getLocalCommandExecutor().addArgumentMapper(id, new MappingSet<>(clazz, mapper, fallbackCondition))) {
			throw new IllegalArgumentException("Mapper with this id is already registered!");
		}
	}

	/**
	 * Adds the mapper that allows to instantly create matching type instead of expecting {@link String}. {@link
	 * Fallback} method will be never called.
	 *
	 * @param id The id of mapper that has to be unique. This is passed to {@link Mapper#value()} to precisely select
	 * 		the created mapper.
	 * @param clazz The {@link Class} that will be returned from mapper function,
	 * @param mapper The {@link Function} that has the logic how to create the {@code clazz} object of String
	 * @param <T> The argument type that can be usd as the @{@link Argument} method's parameter
	 */
	public <T> void addArgumentMapper(final String id, final Class<T> clazz, final Function<String, T> mapper) {
		addArgumentMapper(id, clazz, mapper, Fallback.NEVER);
	}

	/**
	 * Adds the type completer that allows to suggest values on command writing.
	 *
	 * @param clazz The {@link Class} that will be matched to completer.
	 * @param function The {@link Function} that will be used to create the list of matching values.
	 *
	 * @throws IllegalArgumentException if tried to register duplicated {@link Class}.
	 */
	public void addTypeCompleter(final Class<?> clazz, final Function<CommandSender, Collection<String>> function) {
		if (!getLocalTabCompleter().addTypeCompleter(clazz, function)) {
			throw new IllegalArgumentException("Completer for type " + clazz + " is already defined.");
		}
	}

	/**
	 * Adds the type completer that allows to suggest values on command writing.
	 *
	 * @param clazz The {@link Class} that will be matched to completer.
	 * @param supplier The {@link Supplier} that will be used to create the list of matching values.
	 *
	 * @throws IllegalArgumentException if tried to register duplicated {@link Class}.
	 */
	public void addTypeCompleter(final Class<?> clazz, final Supplier<Collection<String>> supplier) {
		addTypeCompleter(clazz, sender -> supplier.get());
	}

	/**
	 * Adds the type completer that allows to suggest values on command writing.
	 *
	 * @param clazz The {@link Class} that will be matched to completer.
	 * @param collection The {@link Collection} that will be the list of matching values.
	 *
	 * @throws IllegalArgumentException if tried to register duplicated {@link Class}.
	 */
	public void addTypeCompleter(final Class<?> clazz, final Collection<String> collection) {
		addTypeCompleter(clazz, () -> collection);
	}

	/**
	 * Adds the argument completer that allows to suggest values on command writing.
	 *
	 * @param id The id of completer that has to be unique. This is passed to {@link Completer#value()} to precisely
	 * 		select the created completer.
	 * @param function The {@link Function} that will produce list of matching values on basis of the sender.
	 *
	 * @throws IllegalArgumentException if tried to register duplicated id.
	 */
	public void addArgumentCompleter(final String id, final Function<CommandSender, Collection<String>> function) {
		if (!getLocalTabCompleter().addArgumentCompleter(id, function)) {
			throw new IllegalArgumentException("Completer with id \"" + id + "\" is already registered!");
		}
	}

	/**
	 * Adds the argument completer that allows to suggest values on command writing.
	 *
	 * @param id The id of completer that has to be unique. This is passed to {@link Completer#value()} to precisely
	 * 		select the created completer.
	 * @param supplier The {@link Supplier} that will produce list of matching values.
	 *
	 * @throws IllegalArgumentException if tried to register duplicated id.
	 */
	public void addArgumentCompleter(final String id, final Supplier<Collection<String>> supplier) {
		addArgumentCompleter(id, sender -> supplier.get());
	}

	/**
	 * Adds the argument completer that allows to suggest values on command writing.
	 *
	 * @param id The id of completer that has to be unique. This is passed to {@link Completer#value()} to precisely
	 * 		select the created completer.
	 * @param collection The {@link Collection} that will be used as list of matching values.
	 *
	 * @throws IllegalArgumentException if tried to register duplicated id.
	 */
	public void addArgumentCompleter(final String id, final Collection<String> collection) {
		addArgumentCompleter(id, () -> collection);
	}

	/**
	 * The accessor method method that allows configuration.
	 *
	 * @return The {@link AnnotatedCommand.Options} instance that allows to configure behavior.
	 */
	public AnnotatedCommand.Options getOptions() {
		return getLocalCommandExecutor().getOptions();
	}
}
