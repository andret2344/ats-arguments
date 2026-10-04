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
	private final Map<String, ResponseMappingSet<?>> argumentResponseMappers = new HashMap<>();
	private final Map<Class<?>, ResponseMappingSet<?>> typeResponseMappers = new HashMap<>();

	/**
	 * Adds an argument mapper.
	 *
	 * @param key The identification.
	 * @param set The mapping set.
	 * @return {@code true} if successfully added mapping set, {@code false} if mapper with this key exists.
	 */
	public boolean addArgumentMapper(final String key, final MappingSet<?> set) {
		if (argumentMappers.containsKey(key)) {
			return false;
		}
		argumentMappers.put(key, set);
		return true;
	}

	/**
	 * Adds a type mapper.
	 *
	 * @param clazz The identifying class.
	 * @param set   The mapping set.
	 * @return {@code true} if successfully added mapping set, {@code false} if mapper with this class exists.
	 */
	public boolean addTypeMapper(final Class<?> clazz, final MappingSet<?> set) {
		if (typeMappers.containsKey(clazz)) {
			return false;
		}
		typeMappers.put(clazz, set);
		return true;
	}

	/**
	 * Adds an argument response mapper.
	 *
	 * @param id  The identifier.
	 * @param set The response mapping set.
	 * @return {@code true} if successfully added mapping set, {@code false} if mapper with this key exists.
	 */
	public boolean addArgumentResponseMapper(@NotNull final String id, @NotNull final ResponseMappingSet<?> set) {
		if (argumentResponseMappers.containsKey(id)) {
			return false;
		}
		argumentResponseMappers.put(id, set);
		return true;
	}

	/**
	 * Adds a type response mapper.
	 *
	 * @param clazz The identifying class.
	 * @param set   The response mapping set.
	 * @return {@code true} if successfully added mapping set, {@code false} if mapper with this class exists.
	 */
	public boolean addTypeResponseMapper(@NotNull final Class<?> clazz, @NotNull final ResponseMappingSet<?> set) {
		if (typeResponseMappers.containsKey(clazz)) {
			return false;
		}
		typeResponseMappers.put(clazz, set);
		return true;
	}

	/**
	 * Gets the argument mapper.
	 *
	 * @param key The identification.
	 * @return The found mapping set if found, {@code null} otherwise.
	 */
	public MappingSet<?> getArgumentMapper(final String key) {
		return argumentMappers.get(key);
	}

	/**
	 * Gets the type mapper.
	 *
	 * @param clazz The identifying class.
	 * @return The found mapping set if found, {@code null} otherwise.
	 */
	public MappingSet<?> getTypeMapper(final Class<?> clazz) {
		return typeMappers.get(clazz);
	}

	/**
	 * Gets the argument response mapper.
	 *
	 * @param id The identifier.
	 * @return The mapper if found, {@code null} otherwise.
	 */
	public ResponseMappingSet<?> getArgumentResponseMapper(@NotNull final String id) {
		return argumentResponseMappers.get(id);
	}

	/**
	 * Gets the type response mapper.
	 *
	 * @param clazz The identifying class.
	 * @return The mapper if found, {@code null} otherwise.
	 */
	public ResponseMappingSet<?> getTypeResponseMapper(@NotNull final Class<?> clazz) {
		return typeResponseMappers.get(clazz);
	}
}
