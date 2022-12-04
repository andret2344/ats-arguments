/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.entity;

import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * This class remembers the returned from mapping function class. It was needed to have such a class, because generic
 * type is lost after compilation.
 *
 * @param <T> The returned from function type;
 *
 * @author Andret
 * @since Apr 17, 2020
 */
@Value
public class MappingSet<T> {
	@NotNull
	Class<T> clazz;
	@NotNull
	Function<String, T> function;
	@NotNull
	Predicate<Object> fallbackCondition;
}
