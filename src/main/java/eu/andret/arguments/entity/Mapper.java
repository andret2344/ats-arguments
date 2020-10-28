/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.entity;

import lombok.Value;

import java.util.function.Function;

/**
 * This class remembers the returned from mapping function class. It was needed to have such a
 * class, because generic type is lost after compilation.
 *
 * @param <T> The returned from function type;
 *
 * @author Andret
 * @since Apr 17, 2020
 */
@Value
public class Mapper<T> {
	Class<T> clazz;
	Function<String, T> function;
}
