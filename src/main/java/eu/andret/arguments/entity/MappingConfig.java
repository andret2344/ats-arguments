/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.entity;

import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.api.annotation.Mapper;
import eu.andret.arguments.api.annotation.TypeFallback;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The combination of all set mappings.
 *
 * @author Andret
 * @since Aug 13, 2021
 */
public class MappingConfig {
	private final Map<String, MappingSet<?>> argumentMappers = new HashMap<>();
	private final Map<Class<?>, MappingSet<?>> typeMappers = new HashMap<>();
	private final Map<String, ResponseMappingSet<?>> argumentResponseMappers = new HashMap<>();
	private final Map<Class<?>, ResponseMappingSet<?>> typeResponseMappers = new HashMap<>();

	/**
	 * Adds a mapper that allows to instantly create matching type instead of expecting {@link String}.
	 *
	 * @param id The id of mapper that has to be unique. This is passed to {@link Mapper#value()} to precisely select
	 * 		the created mapper.
	 * @param clazz The {@link Class} that will be returned from mapper function,
	 * @param mapper The {@link Function} that has the logic how to create the {@code clazz} object of
	 *        {@link String}.
	 * @param fallbackCondition The {@link Predicate} that will verify if fallback should execute.
	 * @param <T> The argument type that can be usd as the @{@link Argument} method's parameter
	 *
	 * @throws IllegalArgumentException if tried to register duplicated id.
	 */
	public <T> void addArgumentMapper(@NotNull final String id,
									  @NotNull final Class<T> clazz,
									  @NotNull final Function<String, T> mapper,
									  @NotNull final Predicate<Object> fallbackCondition) {
		if (argumentMappers.containsKey(id)) {
			throw new IllegalArgumentException(String.format("Mapper with id \"%s\" is already registered!", id));
		}
		argumentMappers.put(id, new MappingSet<>(clazz, mapper, fallbackCondition));
	}

	/**
	 * Adds a mapper that allows to instantly create matching type instead of expecting {@link String}.
	 * {@link TypeFallback} method will never be called.
	 *
	 * @param id The id of mapper that has to be unique. This is passed to {@link Mapper#value()} to precisely select
	 * 		the created mapper.
	 * @param clazz The {@link Class} that will be returned from mapper function,
	 * @param mapper The {@link Function} that has the logic how to create the {@code clazz} object of String
	 * @param <T> The argument type that can be usd as the @{@link Argument} method's parameter
	 *
	 * @throws IllegalArgumentException if tried to register duplicated id.
	 */
	public <T> void addArgumentMapper(@NotNull final String id,
									  @NotNull final Class<T> clazz,
									  @NotNull final Function<String, T> mapper) {
		addArgumentMapper(id, clazz, mapper, Objects::isNull);
	}

	/**
	 * Adds a mapper that allows to instantly create matching type instead of expecting {@link String}.
	 *
	 * @param clazz The {@link Class} that will be returned from mapper function,
	 * @param mapper The {@link Function} that has the logic how to create the {@code clazz} object of
	 *        {@link String}.
	 * @param fallbackCondition The {@link Predicate} that will verify if fallback should execute.
	 * @param <T> The argument type that can be usd as the @{@link Argument} method's parameter
	 *
	 * @throws IllegalArgumentException if tried to register duplicated {@link Class}.
	 */
	public <T> void addTypeMapper(@NotNull final Class<T> clazz, @NotNull final Function<String, T> mapper,
								  @NotNull final Predicate<Object> fallbackCondition) {
		if (typeMappers.containsKey(clazz)) {
			throw new IllegalArgumentException(String.format("Mapper with class %s is already registered!", clazz));
		}
		typeMappers.put(clazz, new MappingSet<>(clazz, mapper, fallbackCondition));
	}

	/**
	 * Adds a mapper that allows to instantly create matching type instead of expecting {@link String}.
	 *
	 * @param clazz The {@link Class} that will be returned from mapper function,
	 * @param mapper The {@link Function} that has the logic how to create the {@code clazz} object of
	 *        {@link String}.
	 * @param <T> The argument type that can be usd as the @{@link Argument} method's parameter
	 *
	 * @throws IllegalArgumentException if tried to register duplicated {@link Class}.
	 */
	public <T> void addTypeMapper(@NotNull final Class<T> clazz, @NotNull final Function<String, T> mapper) {
		addTypeMapper(clazz, mapper, Objects::isNull);
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
		if (typeMappers.containsKey(anEnum)) {
			throw new IllegalArgumentException(String.format("Mapper with enum %s is already registered!", anEnum));
		}
		final Function<String, T> mapper = name -> Enum.valueOf(anEnum, name.toUpperCase());
		typeMappers.put(anEnum, new MappingSet<>(anEnum, mapper, fallbackCondition));
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
		addEnumMapper(anEnum, Objects::isNull);
	}

	/**
	 * Adds a response mapper that allows to map return value to {@link String}.
	 *
	 * @param id The id of the response mapper. The id has to be unique.
	 * @param clazz The {@link Class} that will be returned from mapper function.
	 * @param function The {@link Function} that has the logic how to create the {@link String} of {@code clazz}
	 * 		object.
	 * @param <T> The argument type that can be usd as the @{@link Argument} method's return type.
	 *
	 * @throws IllegalArgumentException if tried to register duplicated class.
	 */
	public <T> void addArgumentResponseMapper(@NotNull final String id,
											  @NotNull final Class<T> clazz,
											  @NotNull final Function<T, String> function) {
		if (argumentResponseMappers.containsKey(id)) {
			throw new IllegalArgumentException(String.format("Response mapper with id %s is already registered!", id));
		}
		argumentResponseMappers.put(id, new ResponseMappingSet<>(clazz, function));
	}

	/**
	 * Adds a response mapper that allows to map return value to {@link String}.
	 *
	 * @param clazz The {@link Class} that will be returned from mapper function.
	 * @param function The {@link Function} that has the logic how to create the {@link String} of {@code clazz}
	 * 		object.
	 * @param <T> The argument type that can be usd as the @{@link Argument} method's return type.
	 *
	 * @throws IllegalArgumentException if tried to register duplicated class.
	 */
	public <T> void addTypeResponseMapper(@NotNull final Class<T> clazz, @NotNull final Function<T, String> function) {
		if (typeResponseMappers.containsKey(clazz)) {
			throw new IllegalArgumentException(String.format("Response mapper with class %s is already registered!", clazz));
		}
		typeResponseMappers.put(clazz, new ResponseMappingSet<>(clazz, function));
	}

	/**
	 * Gets the argument mapper.
	 *
	 * @param key The identification.
	 * @return The found mapping set if found, {@code null} otherwise.
	 */
	@Nullable
	public MappingSet<?> getArgumentMapper(@NotNull final String key) {
		return argumentMappers.get(key);
	}

	/**
	 * Gets the type mapper.
	 *
	 * @param clazz The identifying class.
	 * @return The found mapping set if found, {@code null} otherwise.
	 */
	@Nullable
	public MappingSet<?> getTypeMapper(@NotNull final Class<?> clazz) {
		return typeMappers.get(clazz);
	}

	/**
	 * Gets the argument response mapper.
	 *
	 * @param id The identifier.
	 * @return The mapper if found, {@code null} otherwise.
	 */
	@Nullable
	public ResponseMappingSet<?> getArgumentResponseMapper(@NotNull final String id) {
		return argumentResponseMappers.get(id);
	}

	/**
	 * Gets the type response mapper.
	 *
	 * @param clazz The identifying class.
	 * @return The mapper if found, {@code null} otherwise.
	 */
	@Nullable
	public ResponseMappingSet<?> getTypeResponseMapper(@NotNull final Class<?> clazz) {
		return typeResponseMappers.get(clazz);
	}
}
