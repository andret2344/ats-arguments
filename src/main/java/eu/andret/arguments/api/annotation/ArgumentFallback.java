/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.api.annotation;

import eu.andret.arguments.api.entity.FallbackPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * The interface to decide which method should execute in case when {@link Argument} annotated command param passes its
 * fallback predicate.
 *
 * @author Andret
 * @since Jun 10, 2020
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ArgumentFallback {
	Predicate<Object> NEVER = x -> false;
	Predicate<Object> ON_NULL = Objects::isNull;
	Predicate<Object> ALWAYS = x -> true;

	/**
	 * The array of {@link Mapper} values which will fall back into annotated method if they met their fallback
	 * conditions.
	 *
	 * @return The list of values.
	 */
	String[] value();

	/**
	 * The {@link FallbackPriority} to determine the call order. Methods with equal priority will be called randomly.
	 *
	 * @return The method's priority.
	 */
	FallbackPriority priority() default FallbackPriority.NORMAL;
}
