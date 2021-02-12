/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.filter.impl;

import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.filter.IMethodNameFilter;
import lombok.Value;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * An implementation of {@link IMethodNameFilter}.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
@Value
public class MethodNameFilter implements IMethodNameFilter {
	@Override
	public boolean filterMethodName(final Method method, final String[] command) {
		return Optional.of(method)
				.filter(m -> m.isAnnotationPresent(Argument.class))
				.filter(this::verifyNonStatic)
				.filter(m -> verifyArgumentPosition(m, command))
				.map(m -> m.getAnnotation(Argument.class))
				.map(a -> Stream.concat(Arrays.stream(a.aliases()), Stream.of(method.getName()))
						.anyMatch(command[a.position()]::equalsIgnoreCase))
				.orElse(false);
	}

	private boolean verifyNonStatic(final Method method) {
		if (Modifier.isStatic(method.getModifiers())) {
			throw new IllegalStateException("@Argument method cannot be static! Method: " + method);
		}
		return true;
	}

	private boolean verifyArgumentPosition(final Method method, final String[] command) {
		final Argument argument = method.getAnnotation(Argument.class);
		if (argument.position() > command.length) {
			throw new IllegalArgumentException("@Argument.position() cannot be greater than methods arguments count. Method: " + method);
		}
		return true;
	}
}
