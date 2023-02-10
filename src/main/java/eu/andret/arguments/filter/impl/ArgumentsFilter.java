/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.filter.impl;

import eu.andret.arguments.Util;
import eu.andret.arguments.api.annotation.Mapper;
import eu.andret.arguments.entity.MappingConfig;
import eu.andret.arguments.entity.MappingSet;
import eu.andret.arguments.filter.IArgumentsFilter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Optional;
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
	MappingConfig mappingConfig;

	@Override
	public boolean filterArguments(final Method method, final String[] command, final int skip) {
		final int size = Math.min(command.length - skip, method.getParameterCount());
		if (size == 0 && command.length + method.getParameterCount() != skip) {
			return false;
		}
		final List<Class<?>> list = Stream.of(command).skip(skip).map(Util::getRealClass).collect(Collectors.toList());
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
				throw new IllegalArgumentException(String.format("Cannot be the array! Use VarArg instead. Method: %s#%s", method.getDeclaringClass().getName(), method.getName()));
			}
			final Mapper mapper = parameter.getAnnotation(Mapper.class);
			if (!isTypeMatched(parameter, mapper, classes, i, parameters)) {
				return false;
			}
		}
		return true;
	}

	private boolean isTypeMatched(final Parameter parameter, final Mapper mapper, final List<Class<?>> classes, final int i, final List<Parameter> parameters) {
		if (parameter.isVarArgs()) {
			return isTypeMatchingVarArgParameter(parameter.getType().getComponentType(), mapper, classes.subList(i, classes.size()));
		}
		return (i != parameters.size() - 1 || i >= classes.size() - 1) && isTypeMatchingParam(parameter.getType(), mapper, classes.get(i));
	}

	private boolean isTypeMatchingVarArgParameter(final Class<?> parameterClass, final Mapper mapper, final List<Class<?>> classes) {
		return classes.stream().allMatch(clazz -> isTypeMatchingParam(parameterClass, mapper, clazz));
	}

	private boolean isTypeMatchingParam(final Class<?> parameterClass, final Mapper mapper, final Class<?> clazz) {
		if (mapper != null) {
			return Optional.of(mapper)
					.map(Mapper::value)
					.map(mappingConfig::getArgumentMapper)
					.map(MappingSet::getClazz)
					.map(aClass -> aClass.isAssignableFrom(parameterClass))
					.orElse(false);
		}
		if (mappingConfig.getTypeMapper(parameterClass) != null) {
			return true;
		}
		return clazz.isAssignableFrom(parameterClass);
	}
}
