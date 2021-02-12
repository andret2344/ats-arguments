/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.entity;

import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.api.annotation.Fallback;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.lang.reflect.Method;

/**
 * The class as a container for the {@link Argument} method and its {@link Fallback} method.
 *
 * @author Andret
 * @since Aug 10, 2020
 */

@Value
@AllArgsConstructor
public class ExecutionCall {
	Method method;
	Method fallbackMethod;

	/**
	 * Constructor for class.
	 *
	 * @param method The method that has no fallback one.
	 */
	public ExecutionCall(final Method method) {
		this(method, null);
	}
}
