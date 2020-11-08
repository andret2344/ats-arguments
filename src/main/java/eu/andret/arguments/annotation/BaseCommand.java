/*
 * Copyright Andret (c) 2018=2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation used for {@link eu.andret.arguments.AnnotatedCommandExecutor}'s subclass to define
 * base command for all methods.
 *
 * @author Andret
 * @see eu.andret.arguments.AnnotatedCommandExecutor
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
