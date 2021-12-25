/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;

/**
 * The annotation to mark custom mappings registered via {@link eu.andret.arguments.AnnotatedCommand#addTypeResponseMapper(Class,
 * Function)}.
 *
 * @author Andret
 * @since Nov 25, 2021
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ArgumentResponse {
	/**
	 * The identifier of response mapper.
	 *
	 * @return The identifier of response mapper.
	 */
	String value();
}
