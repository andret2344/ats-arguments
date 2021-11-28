/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.entity;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * The combination of all set mappings.
 *
 * @author Andret
 * @since Aug 13, 2021
 */
public class MappingConfig {
	private final Map<String, MappingSet<?>> argumentMappers = new HashMap<>();
	private final Map<Class<?>, MappingSet<?>> typeMappers = new HashMap<>();
	private final Map<Class<?>, ResponseMappingSet<?>> typeResponseMappers = new HashMap<>();
	private final Map<String, ResponseMappingSet<?>> argumentResponseMappers = new HashMap<>();

	/**
	 * Adds an argument mapper.
	 *
	 * @param key The identification.
	 * @param set The mapping set/
	 */
	public void addArgumentMapper(final String key, final MappingSet<?> set) {
		argumentMappers.put(key, set);
	}

	/**
	 * Adds a type mapper.
	 *
	 * @param clazz The identifying class.
	 * @param set The mapping set.
	 */
	public void addTypeMapper(final Class<?> clazz, final MappingSet<?> set) {
		typeMappers.put(clazz, set);
	}

	/**
	 * Adds a argument response mapper.
	 *
	 * @param id The identifier.
	 * @param set The response mapping set.
	 */
	public void addArgumentResponseMapper(@NotNull final String id, @NotNull final ResponseMappingSet<?> set) {
		argumentResponseMappers.put(id, set);
	}

	/**
	 * Adds a type response mapper.
	 *
	 * @param clazz The identifying class.
	 * @param set The response mapping set.
	 */
	public void addTypeResponseMapper(@NotNull final Class<?> clazz, @NotNull final ResponseMappingSet<?> set) {
		typeResponseMappers.put(clazz, set);
	}

	/**
	 * Gets the argument mapper.
	 *
	 * @param key The identification.
	 *
	 * @return The found mapping set if found, {@code null} otherwise.
	 */
	public MappingSet<?> getArgumentMapper(final String key) {
		return argumentMappers.get(key);
	}

	/**
	 * Gets the type mapper.
	 *
	 * @param clazz The identifying class.
	 *
	 * @return The found mapping set if found, {@code null} otherwise.
	 */
	public MappingSet<?> getTypeMapper(final Class<?> clazz) {
		return typeMappers.get(clazz);
	}

	/**
	 * Gets the argument response mapper.
	 *
	 * @param id The identifier..
	 *
	 * @return The found mapper if found, {@code null} otherwise.
	 */
	public ResponseMappingSet<?> getArgumentResponseMapper(@NotNull final String id) {
		return argumentResponseMappers.get(id);
	}

	/**
	 * Gets the type response mapper.
	 *
	 * @param clazz The identifying class.
	 *
	 * @return The found mapper if found, {@code null} otherwise.
	 */
	public ResponseMappingSet<?> getTypeResponseMapper(@NotNull final Class<?> clazz) {
		return typeResponseMappers.get(clazz);
	}

	/**
	 * Checks if the argument mapper exists.
	 *
	 * @param key The identification.
	 *
	 * @return {@code true} if mapping set is found, {@code false} otherwise.
	 */
	public boolean existsArgumentMapper(final String key) {
		return argumentMappers.containsKey(key);
	}

	/**
	 * Checks if the type mapper exists.
	 *
	 * @param clazz The identifying class.
	 *
	 * @return {@code true} if mapping set is found, {@code false} otherwise.
	 */
	public boolean existsTypeMapper(final Class<?> clazz) {
		return typeMappers.containsKey(clazz);
	}

	/**
	 * Checks if the argument response mapper exists.
	 *
	 * @param id The identifier.
	 *
	 * @return {@code true} if mapper is found, {@code false} otherwise.
	 */
	public boolean existsArgumentResponseMapper(@NotNull final String id) {
		return argumentResponseMappers.containsKey(id);
	}

	/**
	 * Checks if the type response mapper exists.
	 *
	 * @param clazz The identifying class.
	 *
	 * @return {@code true} if mapper is found, {@code false} otherwise.
	 */
	public boolean existsTypeResponseMapper(@NotNull final Class<?> clazz) {
		return typeResponseMappers.containsKey(clazz);
	}
}
