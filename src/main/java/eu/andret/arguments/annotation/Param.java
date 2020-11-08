/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.annotation;

import eu.andret.arguments.entity.Mapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation should be put to the argument of method inside class that extends the {@link
 * eu.andret.arguments.AnnotatedCommandExecutor}. It allows to find {@link Mapper} registered to
 * {@link eu.andret.arguments.AnnotatedCommand}.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
	/**
	 * The id of registered mapper.
	 *
	 * @return The id.
	 */
	String value();
}
