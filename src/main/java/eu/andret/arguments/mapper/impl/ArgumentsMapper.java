package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.Mapper;
import eu.andret.arguments.Util;
import eu.andret.arguments.annotation.Param;
import eu.andret.arguments.mapper.IArgumentMapper;
import lombok.Value;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * An implementation for {@link eu.andret.arguments.mapper.IArgumentMapper}.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
@Value
public class ArgumentsMapper implements IArgumentMapper {
	Util util = Util.getInstance();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean mapArguments(Method method, String[] command, Map<String, Mapper<?>> mappers) {
		int size = Math.min(method.getParameters().length, method.getParameterCount());
		if (size == 0 && method.getParameters().length + method.getParameterCount() != 0) {
			return false;
		}
		List<Class<?>> list = Stream.of(command).skip(1).map(util::getRealClass).collect(Collectors.toList());
		return checkParameters(method, list, mappers);
	}

	private boolean checkParameters(Method method, List<Class<?>> classes, Map<String, Mapper<?>> mappers) {
		List<Parameter> parameters = Stream.of(method.getParameters()).collect(Collectors.toList());
		for (int i = 0; i < parameters.size(); i++) {
			Parameter parameter = parameters.get(i);
			if (parameter.getType().isArray() && !parameter.isVarArgs()) {
				throw new IllegalArgumentException("Cannot be the array! Use VarArgs instead. Method " + method);
			}
			if (!parameter.isVarArgs()) {
				Param param = parameter.getAnnotation(Param.class);
				if ((param != null && !mappers.get(param.value()).getClazz().isAssignableFrom(parameter.getType())) &&
						!classes.get(i).isAssignableFrom(parameter.getType())) {
					return false;
				}
			} else {
				if (!isTypeMatchingVarArgParameter(parameter, classes, i)) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean isTypeMatchingVarArgParameter(Parameter parameter, List<Class<?>> classes, int i) {
		return IntStream.range(i, classes.size())
				.mapToObj(classes::get)
				.allMatch(clazz -> clazz.isAssignableFrom(parameter.getType().getComponentType()));
	}
}
