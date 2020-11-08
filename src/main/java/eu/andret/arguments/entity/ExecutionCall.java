/*
 * Copyright Andret (c) 2018-2020 Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.entity;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.lang.reflect.Method;

@Value
@AllArgsConstructor
public class ExecutionCall {
	Method method;
	Method fallbackMethod;

	public ExecutionCall(Method method) {
		this(method, null);
	}
}
