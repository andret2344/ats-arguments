/*
 * Copyright Andret (c) 2018=2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.annotation.Argument;
import eu.andret.arguments.mapper.IMethodNameMapper;
import lombok.Value;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * An implementation of {@link IMethodNameMapper}.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
@Value
public class MethodNameMapper implements IMethodNameMapper {
	@Override
	public boolean mapMethodName(Method method, String[] command) {
		return Optional.of(method)
				.filter(m -> m.isAnnotationPresent(Argument.class))
				.filter(this::verifyNonStatic)
				.filter(m -> verifyArgumentPosition(m, command))
				.map(m -> m.getAnnotation(Argument.class))
				.map(a -> Stream.concat(Arrays.stream(a.aliases()), Stream.of(method.getName()))
						.anyMatch(command[a.position()]::equalsIgnoreCase))
				.orElse(false);
	}

	private boolean verifyNonStatic(Method method) {
		if (Modifier.isStatic(method.getModifiers())) {
			throw new IllegalStateException("@Argument method cannot be static! Method: " + method);
		}
		return true;
	}

	private boolean verifyArgumentPosition(Method method, String[] command) {
		Argument argument = method.getAnnotation(Argument.class);
		if (argument.position() > command.length) {
			throw new IllegalArgumentException("@Argument.position() cannot be greater than methods arguments count. Method: " + method);
		}
		return true;
	}
}
