/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.filter.impl;

import eu.andret.arguments.AnnotatedCommand;
import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.filter.IMethodNameFilter;
import lombok.Value;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;
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
	public boolean filterMethodName(final Method method, final String name, final AnnotatedCommand.Options options) {
		return Optional.of(method)
				.filter(theMethod -> theMethod.isAnnotationPresent(Argument.class))
				.filter(this::verifyNonStatic)
				.map(theMethod -> theMethod.getAnnotation(Argument.class))
				.map(argument -> nameMatches(argument, method, name, options.isCaseSensitive()))
				.orElse(false);
	}

	private boolean nameMatches(final Argument argument, final Method method, final String name,
								final boolean caseSensitive) {
		final Predicate<String> predicate = caseSensitive ? name::equals : name::equalsIgnoreCase;
		return getAllNamesStream(argument, method).anyMatch(predicate);
	}

	private Stream<String> getAllNamesStream(final Argument argument, final Method method) {
		return Stream.concat(Arrays.stream(argument.aliases()), Stream.of(method.getName()));
	}

	private boolean verifyNonStatic(final Method method) {
		if (Modifier.isStatic(method.getModifiers())) {
			throw new IllegalStateException(String.format("@Argument method cannot be static! Method: %s#%s", method.getDeclaringClass().getName(), method.getName()));
		}
		return true;
	}
}
