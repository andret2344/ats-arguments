package eu.andret.arguments.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Using this annotation allows to execute method in case when {@link Argument} annotated method throws an exception.
 *
 * @author Andret
 * @since Jun 10, 2020
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExceptionFallback {
	/**
	 * The list of exceptions to be caught.
	 *
	 * @return The list of exceptions to be caught.
	 */
	Class<? extends Throwable>[] value();
}
