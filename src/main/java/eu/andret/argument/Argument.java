/*
 * Copyright Andret (c) 2019. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.argument;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Andret
 */
@Target(java.lang.annotation.ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Argument {
	String value();

	int position() default 0;

	String permission() default "";

	ExecutorType executorType() default ExecutorType.ALL;

	ResponseType responseType() default ResponseType.SENDER;

	String description() default "";

	boolean showIfNoPerms() default false;
}
