/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.api.annotation;

import eu.andret.arguments.AnnotatedCommandExecutor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation used for {@link AnnotatedCommandExecutor}'s subclass to define base command for all methods.
 *
 * @author Andret
 * @since Jul 01, 2019
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BaseCommand {
	/**
	 * The command all methods will be arguments for.
	 *
	 * @return The base command.
	 */
	String value();
}
