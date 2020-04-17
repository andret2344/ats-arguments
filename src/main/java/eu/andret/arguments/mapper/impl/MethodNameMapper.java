package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.annotation.Argument;
import eu.andret.arguments.mapper.IMethodNameMapper;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class MethodNameMapper implements IMethodNameMapper {
	@Override
	public boolean mapMethodName(Method method, String[] command) {
		return Optional.of(method)
				.filter(m -> !Modifier.isStatic(m.getModifiers()))
				.map(m -> m.getAnnotation(Argument.class))
				.filter(a -> a.position() <= command.length)
				.map(a -> Stream.concat(Arrays.stream(a.aliases()), Stream.of(method.getName()))
						.anyMatch(command[a.position()]::equalsIgnoreCase))
				.orElse(false);
	}
}
