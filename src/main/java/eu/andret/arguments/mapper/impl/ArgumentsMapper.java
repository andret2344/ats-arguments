/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.Mapper;
import eu.andret.arguments.Util;
import eu.andret.arguments.annotation.Param;
import eu.andret.arguments.mapper.IArgumentsMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * An implementation for {@link eu.andret.arguments.mapper.IArgumentsMapper}.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
@Value
@Getter(AccessLevel.NONE)
public class ArgumentsMapper implements IArgumentsMapper {
	Map<String, Mapper<?>> mappers;

	@Override
	public boolean mapArguments(Method method, String[] command) {
		int size = Math.min(command.length - 1, method.getParameterCount());
		if (size == 0 && command.length - 1 + method.getParameterCount() != 0) {
			return false;
		}
		List<Class<?>> list = Stream.of(command).skip(1).map(Util::getRealClass).collect(Collectors.toList());
		return checkParameters(method, list);
	}

	private boolean checkParameters(Method method, List<Class<?>> classes) {
		List<Parameter> parameters = Stream.of(method.getParameters()).collect(Collectors.toList());
		for (int i = 0; i < parameters.size(); i++) {
			Parameter parameter = parameters.get(i);
			if (parameter.getType().isArray() && !parameter.isVarArgs()) {
				throw new IllegalArgumentException("Cannot be the array! Use VarArgs instead. Method " + method);
			}
			Param param = parameter.getAnnotation(Param.class);
			if (parameter.isVarArgs()
					? !isTypeMatchingVarArgParameter(parameter, param, classes, i)
					: !isTypeMatchingParam(parameter, param, classes.get(i))) {
				return false;
			}
		}
		return true;
	}

	private boolean isTypeMatchingVarArgParameter(Parameter parameter, Param param, List<Class<?>> classes, int i) {
		return IntStream.range(i, classes.size())
				.mapToObj(classes::get)
				.allMatch(clazz -> clazz.isAssignableFrom(parameter.getType().getComponentType()) ||
						(clazz == String.class && param != null && mappers.get(param.value()).getClazz().isAssignableFrom(parameter.getType().getComponentType())));
	}

	private boolean isTypeMatchingParam(Parameter parameter, Param param, Class<?> clazz) {
		return param == null
				? clazz.isAssignableFrom(parameter.getType())
				: mappers.get(param.value()).getClazz().isAssignableFrom(parameter.getType());
	}
}
