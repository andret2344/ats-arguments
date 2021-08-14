/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.api.annotation;

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
public @interface Fallback {
	Predicate<Object> NEVER = x -> false;
	Predicate<Object> ON_NULL = Objects::isNull;
	Predicate<Object> ALWAYS = x -> true;
}
