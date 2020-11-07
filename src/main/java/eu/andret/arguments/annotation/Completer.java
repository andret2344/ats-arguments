package eu.andret.arguments.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation should be put to the argument of method inside class that extends the {@link
 * eu.andret.arguments.AnnotatedCommandExecutor}. It allows to find {@link Completer} registered to
 * {@link eu.andret.arguments.AnnotatedCommand}.
 *
 * @author Andret
 * @since Nov 07, 2020
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Completer {
	/**
	 * The id of registered completer..
	 *
	 * @return The id.
	 */
	String value();
}
