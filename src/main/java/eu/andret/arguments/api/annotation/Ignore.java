/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.api.annotation;

import eu.andret.arguments.AnnotatedCommand;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;

/**
 * The annotation that allows to ignore type completion created by {@link
 * AnnotatedCommand#addTypeCompleter(Class, Function)} method.
 *
 * @author Andret
 * @since Nov 08, 2020
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Ignore {
}
