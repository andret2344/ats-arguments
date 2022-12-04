/*
 * Copyright Andret (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.entity;

import eu.andret.arguments.api.annotation.Completer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CompletingConfig {
	private final Map<String, BiFunction<List<String>, CommandSender, Collection<String>>> argumentCompleters = new HashMap<>();
	private final Map<Class<?>, BiFunction<List<String>, CommandSender, Collection<String>>> typeCompleters = new HashMap<>();

	/**
	 * Adds an argument completer that allows to suggest values on command writing.
	 *
	 * @param id The id of completer that has to be unique. This is passed to {@link Completer#value()} to precisely
	 * 		select the created completer.
	 * @param function The {@link Function} that will produce list of matching values on basis of the sender.
	 *
	 * @throws IllegalArgumentException if tried to register duplicated id.
	 */
	public void addArgumentCompleter(@NotNull final String id, @NotNull final BiFunction<List<String>, CommandSender, Collection<String>> function) {
		if (argumentCompleters.containsKey(id)) {
			throw new IllegalArgumentException(String.format("Completer with id \"%s\" is already registered!", id));
		}
		argumentCompleters.put(id, function);
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
									 @NotNull final Function<List<String>, Collection<String>> function) {
		addArgumentCompleter(id, (collection, sender) -> function.apply(collection));
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
	 * Adds an enum completer that allows to suggest values on command writing.
	 *
	 * @param anEnum The {@link Enum} class that will be matched to completer.
	 * @param <T> The {@link Enum} type that will be mapped.
	 *
	 * @throws IllegalArgumentException if tried to register duplicated {@link Enum}.
	 */
	public <T extends Enum<T>> void addEnumCompleter(@NotNull final Class<T> anEnum) {
		if (typeCompleters.containsKey(anEnum)) {
			throw new IllegalArgumentException(String.format("Completer with enum %s is already registered!", anEnum));
		}
		final BiFunction<List<String>, CommandSender, Collection<String>> function = (sender, collection) ->
				Arrays.stream(anEnum.getEnumConstants())
						.map(String::valueOf)
						.map(String::toUpperCase)
						.collect(Collectors.toList());
		typeCompleters.put(anEnum, function);
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
								 @NotNull final BiFunction<List<String>, CommandSender, Collection<String>> function) {
		if (typeCompleters.containsKey(clazz)) {
			throw new IllegalArgumentException(String.format("Completer with class %s is already registered!", clazz));
		}
		typeCompleters.put(clazz, function);
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
								 @NotNull final Function<List<String>, Collection<String>> function) {
		addTypeCompleter(clazz, (collection, sender) -> function.apply(collection));
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
	 * Gets the argument completer.
	 *
	 * @param id The identifier.
	 *
	 * @return The completer if found, {@code null} otherwise.
	 */
	@Nullable
	public BiFunction<List<String>, CommandSender, Collection<String>> getArgumentCompleter(@NotNull final String id) {
		return argumentCompleters.get(id);
	}

	/**
	 * Gets the type completer.
	 *
	 * @param clazz The identifying class.
	 *
	 * @return The completer if found, {@code null} otherwise.
	 */
	@Nullable
	public BiFunction<List<String>, CommandSender, Collection<String>> getTypeCompleter(@NotNull final Class<?> clazz) {
		return typeCompleters.get(clazz);
	}
}
