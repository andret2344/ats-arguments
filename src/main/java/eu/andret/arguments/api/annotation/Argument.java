/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.api.annotation;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.entity.DisplayType;
import eu.andret.arguments.api.entity.ExecutorType;
import eu.andret.arguments.api.entity.ResponseType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation used for {@link AnnotatedCommandExecutor}'s method to analyze them in search for
 * matching sub-commands of main command.
 *
 * @author Andret
 * @since May 18, 2019
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Argument {
	/**
	 * The position of argument that's matches the method's name.
	 *
	 * @return The position.
	 */
	int position() default 0;

	/**
	 * Permission whether sender can perform the command.
	 *
	 * @return The permission.
	 */
	String permission() default "";

	/**
	 * Executor type that is allowed to execute the command.
	 *
	 * @return The executor type.
	 *
	 * @see ExecutorType
	 */
	ExecutorType executorType() default ExecutorType.ALL;

	/**
	 * Who should get the returned from method value.
	 *
	 * @return The response type.
	 *
	 * @see ResponseType
	 */
	ResponseType responseType() default ResponseType.SENDER;

	/**
	 * The description od the method to appear in help.
	 *
	 * @return The description.
	 */
	String description() default "";

	/**
	 * Aliases for argument, eg. "cmd" as alias for "command", and so on.
	 *
	 * @return The list of aliases.
	 */
	String[] aliases() default {};

	/**
	 * Under what circumstances sender should see an argument in help.
	 *
	 * @return The display type.
	 */
	DisplayType displayType() default DisplayType.ALWAYS;
}
