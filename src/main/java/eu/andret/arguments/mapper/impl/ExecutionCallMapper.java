/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.api.annotation.Fallback;
import eu.andret.arguments.entity.ExecutionCall;
import eu.andret.arguments.mapper.IExecutionCallMapper;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * The implementation for {@link IExecutionCallMapper}.
 *
 * @author Andret
 * @since Jun 10, 2020
 */
public class ExecutionCallMapper implements IExecutionCallMapper {
	@Override
	public ExecutionCall mapExecutionCall(final Method method, final Method[] methods) {
		return Arrays.stream(methods)
				.filter(m -> m.isAnnotationPresent(Fallback.class))
				.filter(m -> m.getName().equals(method.getName()))
				.findAny()
				.map(m -> new ExecutionCall(method, m))
				.orElse(new ExecutionCall(method));
	}
}
