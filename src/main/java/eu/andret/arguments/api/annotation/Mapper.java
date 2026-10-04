package eu.andret.arguments.api.annotation;

import eu.andret.arguments.AnnotatedCommand;
import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.entity.MappingSet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation should be put to the argument of method inside class that extends the {@link
 * AnnotatedCommandExecutor}. It allows finding {@link MappingSet} registered to {@link AnnotatedCommand}.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapper {
	/**
	 * The id of registered mapper.
	 *
	 * @return The id.
	 */
	String value();
}
