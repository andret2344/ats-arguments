/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.filter.impl;

import eu.andret.arguments.Util;
import eu.andret.arguments.api.annotation.Param;
import eu.andret.arguments.entity.Mapper;
import eu.andret.arguments.filter.IArgumentsFilter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An implementation for {@link IArgumentsFilter}.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
@Value
@Getter(AccessLevel.NONE)
public class ArgumentsFilter implements IArgumentsFilter {
	Map<String, Mapper<?>> mappers;

	@Override
	public boolean filter(final Method method, final String[] command) {
		final int size = Math.min(command.length - 1, method.getParameterCount());
		if (size == 0 && command.length - 1 + method.getParameterCount() != 0) {
			return false;
		}
		final List<Class<?>> list = Stream.of(command).skip(1).map(Util::getRealClass).collect(Collectors.toList());
		return checkParameters(method, list);
	}

	private boolean checkParameters(final Method method, final List<Class<?>> classes) {
		final List<Parameter> parameters = Stream.of(method.getParameters()).collect(Collectors.toList());
		if (classes.size() < parameters.size()) {
			return false;
		}
		for (int i = 0; i < parameters.size(); i++) {
			final Parameter parameter = parameters.get(i);
			if (parameter.getType().isArray() && !parameter.isVarArgs()) {
				throw new IllegalArgumentException("Cannot be the array! Use VarArgs instead. Method " + method);
			}
			final Param param = parameter.getAnnotation(Param.class);
			if (!isOk(parameter, param, classes, i, parameters)) {
				return false;
			}
		}
		return true;
	}

	private boolean isOk(final Parameter parameter, final Param param, final List<Class<?>> classes, final int i, final List<Parameter> parameters) {
		if (parameter.isVarArgs()) {
			return isTypeMatchingVarArgParameter(parameter, param, classes.subList(i, classes.size()));
		}
		return (i != parameters.size() - 1 || i >= classes.size() - 1) && isTypeMatchingParam(parameter, param, classes.get(i));
	}

	private boolean isTypeMatchingVarArgParameter(final Parameter parameter, final Param param, final List<Class<?>> classes) {
		return classes.stream().allMatch(clazz -> clazz.isAssignableFrom(parameter.getType().getComponentType()) ||
				(clazz == String.class && param != null && mappers.get(param.value()).getClazz().isAssignableFrom(parameter.getType().getComponentType())));
	}

	private boolean isTypeMatchingParam(final Parameter parameter, final Param param, final Class<?> clazz) {
		return param == null
				? clazz.isAssignableFrom(parameter.getType())
				: mappers.get(param.value()).getClazz().isAssignableFrom(parameter.getType());
	}
}
