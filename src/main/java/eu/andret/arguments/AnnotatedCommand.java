/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.api.annotation.Completer;
import eu.andret.arguments.api.annotation.Mapper;
import eu.andret.arguments.api.annotation.TypeFallback;
import eu.andret.arguments.api.entity.FallbackConstants;
import eu.andret.arguments.entity.MappingSet;
import eu.andret.arguments.entity.ResponseMappingSet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Wrapper class for classical {@link PluginCommand}.
 *
 * @author Andret
 * @since Jun 02, 2019
 */
@Value
@NonFinal
@AllArgsConstructor
public class AnnotatedCommand<E extends JavaPlugin> {
	@NotNull
	PluginCommand command;
	@NotNull
	Options options;

	/**
	 * Single argument constructor.
	 *
	 * @param command The plugin command.
	 */
	public AnnotatedCommand(@NotNull final PluginCommand command) {
		this(command, new Options());
	}

	/**
	 * The Options to manipulate the behavior.
	 */
	@Data
	public static class Options {
		private boolean autoTranslateColors;
		private boolean caseSensitive;
	}

	/**
	 * Listener to define an action when the sender performs an unknown sub-command.
	 */
	public interface OnUnknownSubCommandExecutionListener {

		/**
		 * Unknown sub-command executed.
		 *
		 * @param sender The sender that executed an unknown sub-command
		 */
		void unknownSubCommandExecuted(@NotNull CommandSender sender);
	}

	/**
	 * Listener to define an action when the sender has insufficient permissions.
	 */
	public interface OnInsufficientPermissionsListener {

		/**
		 * Insufficient permissions.
		 *
		 * @param sender The sender that executed the command with no permissions.
		 */
		void insufficientPermissions(@NotNull CommandSender sender);
	}

	/**
	 * Listener to define action when sender executes command with no arguments.
	 */
	public interface OnMainCommandExecutionListener {

		/**
		 * Main command.
		 *
		 * @param sender The sender that executed the command with no arguments.
		 */
		void mainCommandExecution(@NotNull CommandSender sender);
	}

	@NotNull
	@SuppressWarnings("unchecked")
	private LocalCommandExecutor<E> getLocalCommandExecutor() {
		return (LocalCommandExecutor<E>) command.getExecutor();
	}

	@SuppressWarnings("unchecked")
	private LocalTabCompleter<E> getLocalTabCompleter() {
		return (LocalTabCompleter<E>) command.getTabCompleter();
	}

	/**
	 * @param sender The sender that is assigned to the desired CommandExecutor.
	 *
	 * @return The command executor assigned to provided sender. Can return null, if provided sender never executed
	 * 		command.
	 */
	public AnnotatedCommandExecutor<E> getCommandExecutor(@NotNull final CommandSender sender) {
		return getLocalCommandExecutor().getCommandExecutor(sender);
	}

	/**
	 * Sets an unknown sub command execution listener.
	 *
	 * @param listener The {@link OnUnknownSubCommandExecutionListener}.
	 */
	public void setOnUnknownSubCommandExecutionListener(final OnUnknownSubCommandExecutionListener listener) {
		getLocalCommandExecutor().setOnUnknownSubCommandExecutionListener(listener);
	}

	/**
	 * Sets an insufficient permissions' listener.
	 *
	 * @param listener The {@link OnInsufficientPermissionsListener}.
	 */
	public void setOnInsufficientPermissionsListener(final OnInsufficientPermissionsListener listener) {
		getLocalCommandExecutor().setOnInsufficientPermissionsListener(listener);
	}

	/**
	 * Sets the main command execution listener.
	 *
	 * @param listener The {@link OnMainCommandExecutionListener}.
	 */
	public void setOnMainCommandExecutionListener(final OnMainCommandExecutionListener listener) {
		getLocalCommandExecutor().setOnMainCommandExecutionListener(listener);
	}

	/**
	 * Adds a mapper that allows to instantly create matching type instead of expecting {@link String}.
	 *
	 * @param clazz The {@link Class} that will be returned from mapper function,
	 * @param mapper The {@link Function} that has the logic how to create the {@code clazz} object of {@link
	 *        String}.
	 * @param fallbackCondition The {@link Predicate} that will verify if fallback should execute.
	 * @param <T> The argument type that can be usd as the @{@link Argument} method's parameter
	 *
	 * @throws IllegalArgumentException if tried to register duplicated {@link Class}.
	 */
	public <T> void addTypeMapper(@NotNull final Class<T> clazz, @NotNull final Function<String, T> mapper,
								  @NotNull final Predicate<Object> fallbackCondition) {
		if (!getLocalCommandExecutor().addTypeMapper(clazz, new MappingSet<>(clazz, mapper, fallbackCondition))) {
			throw new IllegalArgumentException(String.format("Mapper with class %s is already registered!", clazz));
		}
	}

	/**
	 * Adds a mapper that allows to instantly create matching type instead of expecting {@link String}.
	 *
	 * @param clazz The {@link Class} that will be returned from mapper function,
	 * @param mapper The {@link Function} that has the logic how to create the {@code clazz} object of {@link
	 *        String}.
	 * @param <T> The argument type that can be usd as the @{@link Argument} method's parameter
	 *
	 * @throws IllegalArgumentException if tried to register duplicated {@link Class}.
	 */
	public <T> void addTypeMapper(@NotNull final Class<T> clazz, @NotNull final Function<String, T> mapper) {
		addTypeMapper(clazz, mapper, FallbackConstants.NEVER);
	}

	/**
	 * Adds a mapper that allows to instantly create matching type instead of expecting {@link String}.
	 *
	 * @param id The id of mapper that has to be unique. This is passed to {@link Mapper#value()} to precisely select
	 * 		the created mapper.
	 * @param clazz The {@link Class} that will be returned from mapper function,
	 * @param mapper The {@link Function} that has the logic how to create the {@code clazz} object of {@link
	 *        String}.
	 * @param fallbackCondition The {@link Predicate} that will verify if fallback should execute.
	 * @param <T> The argument type that can be usd as the @{@link Argument} method's parameter
	 *
	 * @throws IllegalArgumentException if tried to register duplicated id.
	 */
	public <T> void addArgumentMapper(@NotNull final String id, @NotNull final Class<T> clazz,
									  @NotNull final Function<String, T> mapper, @NotNull final Predicate<Object> fallbackCondition) {
		if (!getLocalCommandExecutor().addArgumentMapper(id, new MappingSet<>(clazz, mapper, fallbackCondition))) {
			throw new IllegalArgumentException(String.format("Mapper with id \"%s\" is already registered!", id));
		}
	}

	/**
	 * Adds a mapper that allows to instantly create matching type instead of expecting {@link String}. {@link
	 * TypeFallback} method will never be called.
	 *
	 * @param id The id of mapper that has to be unique. This is passed to {@link Mapper#value()} to precisely select
	 * 		the created mapper.
	 * @param clazz The {@link Class} that will be returned from mapper function,
	 * @param mapper The {@link Function} that has the logic how to create the {@code clazz} object of String
	 * @param <T> The argument type that can be usd as the @{@link Argument} method's parameter
	 *
	 * @throws IllegalArgumentException if tried to register duplicated id.
	 */
	public <T> void addArgumentMapper(@NotNull final String id, @NotNull final Class<T> clazz,
									  @NotNull final Function<String, T> mapper) {
		addArgumentMapper(id, clazz, mapper, FallbackConstants.NEVER);
	}

	/**
	 * Adds a mapper that allows to instantly create matching enum value instead of expecting {@link String}.
	 *
	 * @param anEnum The {@link Enum} that will be returned from mapper function,
	 * @param fallbackCondition The {@link Predicate} that will verify if fallback should execute.
	 * @param <T> The {@link Enum} type that will be mapped.
	 *
	 * @throws IllegalArgumentException if tried to register duplicated {@link Enum}.
	 */
	public <T extends Enum<T>> void addEnumMapper(@NotNull final Class<T> anEnum,
												  @NotNull final Predicate<Object> fallbackCondition) {
		final Function<String, T> mapper = name -> Enum.valueOf(anEnum, name.toUpperCase());
		if (!getLocalCommandExecutor().addTypeMapper(anEnum, new MappingSet<>(anEnum, mapper, fallbackCondition))) {
			throw new IllegalArgumentException(String.format("Mapper with enum %s is already registered!", anEnum));
		}
	}

	/**
	 * Adds a mapper that allows to instantly create matching enum value instead of expecting {@link String}.
	 *
	 * @param anEnum The {@link Enum} that will be returned from mapper function,
	 * @param <T> The {@link Enum} type that will be mapped.
	 *
	 * @throws IllegalArgumentException if tried to register duplicated {@link Enum}.
	 */
	public <T extends Enum<T>> void addEnumMapper(@NotNull final Class<T> anEnum) {
		addEnumMapper(anEnum, FallbackConstants.NEVER);
	}

	/**
	 * Adds a type completer that allows to suggest values on command writing.
	 *
	 * @param clazz The {@link Class} that will be matched to completer.
	 * @param function The {@link BiFunction} that will be used to create the list of matching values.
	 *
	 * @throws IllegalArgumentException if tried to register duplicated {@link Class}.
	 */
	public void addTypeCompleter(@NotNull final Class<?> clazz,
								 @NotNull final BiFunction<CommandSender, Collection<String>, Collection<String>> function) {
		if (!getLocalTabCompleter().addTypeCompleter(clazz, function)) {
			throw new IllegalArgumentException(String.format("Completer with class %s is already registered!", clazz));
		}
	}

	/**
	 * Adds a type completer that allows to suggest values on command writing.
	 *
	 * @param clazz The {@link Class} that will be matched to completer.
	 * @param function The {@link Function} that will be used to create the list of matching values.
	 *
	 * @throws IllegalArgumentException if tried to register duplicated {@link Class}.
	 */
	public void addTypeCompleter(@NotNull final Class<?> clazz,
								 @NotNull final Function<CommandSender, Collection<String>> function) {
		addTypeCompleter(clazz, (sender, collection) -> function.apply(sender));
	}

	/**
	 * Adds an enum completer that allows to suggest values on command writing.
	 *
	 * @param anEnum The {@link Enum} class that will be matched to completer.
	 * @param <T> The {@link Enum} type that will be mapped.
	 *
	 * @throws IllegalArgumentException if tried to register duplicated {@link Enum}.
	 */
	public <T extends Enum<T>> void addEnumCompleter(@NotNull final Class<T> anEnum) {
		final BiFunction<CommandSender, Collection<String>, Collection<String>> function = (sender, collection) ->
				Arrays.stream(anEnum.getEnumConstants())
						.map(String::valueOf)
						.map(String::toUpperCase)
						.collect(Collectors.toList());
		if (!getLocalTabCompleter().addTypeCompleter(anEnum, function)) {
			throw new IllegalArgumentException(String.format("Completer with enum %s is already registered!", anEnum));
		}
	}

	/**
	 * Adds a type completer that allows to suggest values on command writing.
	 *
	 * @param clazz The {@link Class} that will be matched to completer.
	 * @param supplier The {@link Supplier} that will be used to create the list of matching values.
	 *
	 * @throws IllegalArgumentException if tried to register duplicated {@link Class}.
	 */
	public void addTypeCompleter(@NotNull final Class<?> clazz, @NotNull final Supplier<Collection<String>> supplier) {
		addTypeCompleter(clazz, (sender, collection) -> supplier.get());
	}

	/**
	 * Adds a type completer that allows to suggest values on command writing.
	 *
	 * @param clazz The {@link Class} that will be matched to completer.
	 * @param collection The {@link Collection} that will be the list of matching values.
	 *
	 * @throws IllegalArgumentException if tried to register duplicated {@link Class}.
	 */
	public void addTypeCompleter(@NotNull final Class<?> clazz, @NotNull final Collection<String> collection) {
		addTypeCompleter(clazz, () -> collection);
	}

	/**
	 * Adds an argument completer that allows to suggest values on command writing.
	 *
	 * @param id The id of completer that has to be unique. This is passed to {@link Completer#value()} to precisely
	 * 		select the created completer.
	 * @param function The {@link Function} that will produce list of matching values on basis of the sender.
	 *
	 * @throws IllegalArgumentException if tried to register duplicated id.
	 */
	public void addArgumentCompleter(@NotNull final String id, @NotNull final BiFunction<CommandSender, Collection<String>, Collection<String>> function) {
		if (!getLocalTabCompleter().addArgumentCompleter(id, function)) {
			throw new IllegalArgumentException(String.format("Completer with id \"%s\" is already registered!", id));
		}
	}

	/**
	 * Adds an argument completer that allows to suggest values on command writing.
	 *
	 * @param id The id of completer that has to be unique. This is passed to {@link Completer#value()} to precisely
	 * 		select the created completer.
	 * @param function The {@link Function} that will produce list of matching values.
	 *
	 * @throws IllegalArgumentException if tried to register duplicated id.
	 */
	public void addArgumentCompleter(@NotNull final String id,
									 @NotNull final Function<CommandSender, Collection<String>> function) {
		addArgumentCompleter(id, (sender, collection) -> function.apply(sender));
	}

	/**
	 * Adds an argument completer that allows to suggest values on command writing.
	 *
	 * @param id The id of completer that has to be unique. This is passed to {@link Completer#value()} to precisely
	 * 		select the created completer.
	 * @param supplier The {@link Supplier} that will produce list of matching values.
	 *
	 * @throws IllegalArgumentException if tried to register duplicated id.
	 */
	public void addArgumentCompleter(@NotNull final String id, @NotNull final Supplier<Collection<String>> supplier) {
		addArgumentCompleter(id, sender -> supplier.get());
	}

	/**
	 * Adds an argument completer that allows to suggest values on command writing.
	 *
	 * @param id The id of completer that has to be unique. This is passed to {@link Completer#value()} to precisely
	 * 		select the created completer.
	 * @param collection The {@link Collection} that will be used as list of matching values.
	 *
	 * @throws IllegalArgumentException if tried to register duplicated id.
	 */
	public void addArgumentCompleter(@NotNull final String id, @NotNull final Collection<String> collection) {
		addArgumentCompleter(id, () -> collection);
	}

	/**
	 * Adds a response mapper that allows to map return value to  {@link String}.
	 *
	 * @param clazz The {@link Class} that will be returned from mapper function,
	 * @param function The {@link Function} that has the logic how to create the {@link String} of {@code clazz}
	 * 		object.
	 * @param <T> The argument type that can be usd as the @{@link Argument} method's return type.
	 *
	 * @throws IllegalArgumentException if tried to register duplicated class.
	 */
	public <T> void addTypeResponseMapper(@NotNull final Class<T> clazz, @NotNull final Function<T, String> function) {
		if (!getLocalCommandExecutor().addTypeResponseMapper(clazz, new ResponseMappingSet<>(clazz, function))) {
			throw new IllegalArgumentException(String.format("Response mapper with class %s is already registered!", clazz));
		}
	}

	/**
	 * Adds a response mapper that allows to map return value to  {@link String}.
	 *
	 * @param id The id of the response mapper. The id has to be unique.
	 * @param clazz The {@link Class} that will be returned from mapper function,
	 * @param function The {@link Function} that has the logic how to create the {@link String} of {@code clazz}
	 * 		object.
	 * @param <T> The argument type that can be usd as the @{@link Argument} method's return type.
	 *
	 * @throws IllegalArgumentException if tried to register duplicated class.
	 */
	public <T> void addArgumentResponseMapper(@NotNull final String id, @NotNull final Class<T> clazz,
											  @NotNull final Function<T, String> function) {
		if (!getLocalCommandExecutor().addArgumentResponseMapper(id, new ResponseMappingSet<>(clazz, function))) {
			throw new IllegalArgumentException(String.format("Response mapper with id %s is already registered!", id));
		}
	}
}
