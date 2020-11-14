/*
 * Copyright Andret (c) 2018-2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.entity;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.lang.reflect.Method;

/**
 * The class as a container for method and its fallback.
 *
 * @author Andret
 * @see eu.andret.arguments.annotation.Fallback
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
	public ExecutionCall(Method method) {
		this(method, null);
	}
}
