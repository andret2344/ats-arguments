/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */
package eu.andret.arguments.annotation;

import eu.andret.arguments.ExecutorType;
import eu.andret.arguments.ResponseType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation used for {@link eu.andret.arguments.AnnotatedCommandExecutor}'s method to analyze
 * them in search for matching sub-commands of main command.
 *
 * @author Andret
 * @see eu.andret.arguments.AnnotatedCommandExecutor
 * @since May 18, 2019
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Argument {
	/**
	 * The position of argument that's matches the method's n.ame
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
	 * @see eu.andret.arguments.ExecutorType
	 */
	ExecutorType executorType() default ExecutorType.ALL;

	/**
	 * Who should get the returned from method value.
	 *
	 * @return The response type.
	 *
	 * @see eu.andret.arguments.ResponseType
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
	 * Should sender see annotated method as available command if has no perms to perform it.
	 *
	 * @return <code>true</code> if should be shown in help with lack of
	 * perms, <code>false</code> otherwise.
	 */
	boolean showIfNoPerms() default false;
}
