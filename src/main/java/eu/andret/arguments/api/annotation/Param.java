/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.api.annotation;

import eu.andret.arguments.AnnotatedCommand;
import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.entity.Mapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation should be put to the argument of method inside class that extends the {@link
 * AnnotatedCommandExecutor}. It allows to find {@link Mapper} registered to {@link
 * AnnotatedCommand}.
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
