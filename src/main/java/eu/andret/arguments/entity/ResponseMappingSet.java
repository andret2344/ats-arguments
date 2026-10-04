package eu.andret.arguments.entity;

import java.util.function.Function;

/**
 * This class stores the return mapping settings. It was needed to have such a class, because generic type is lost after
 * compilation.
 *
 * @param <T> The returned from function type;
 *
 * @author Andret
 * @since Nov 25, 2020
 */
public record ResponseMappingSet<T>(Class<T> clazz, Function<T, String> function) {
}
