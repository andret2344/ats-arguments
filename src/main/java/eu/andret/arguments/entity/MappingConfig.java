/*
 * Copyright Andret (c) 2018-2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.entity;

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

	/**
	 * Adds an argument mapper.
	 *
	 * @param key The identification.
	 * @param set The mapping set/
	 */
	public void add(final String key, final MappingSet<?> set) {
		argumentMappers.put(key, set);
	}

	/**
	 * Adds a type mapper.
	 *
	 * @param clazz The identifying class.
	 * @param set The mapping set/
	 */
	public void add(final Class<?> clazz, final MappingSet<?> set) {
		typeMappers.put(clazz, set);
	}

	/**
	 * Gets the argument mapper.
	 *
	 * @param key The identification.
	 *
	 * @return The found mapping set if found, {@code null} otherwise.
	 */
	public MappingSet<?> get(final String key) {
		return argumentMappers.get(key);
	}

	/**
	 * Gets the type mapper.
	 *
	 * @param clazz The identifying class.
	 *
	 * @return The found mapping set if found, {@code null} otherwise.
	 */
	public MappingSet<?> get(final Class<?> clazz) {
		return typeMappers.get(clazz);
	}

	/**
	 * Checks if the argument mapper exists.
	 *
	 * @param key The identification.
	 *
	 * @return {@code true} if mapping set is found, {@code false} otherwise.
	 */
	public boolean exists(final String key) {
		return argumentMappers.containsKey(key);
	}

	/**
	 * Checks if the type mapper exists.
	 *
	 * @param clazz The identifying class.
	 *
	 * @return {@code true} if mapping set is found, {@code false} otherwise.
	 */
	public boolean exists(final Class<?> clazz) {
		return typeMappers.containsKey(clazz);
	}
}
