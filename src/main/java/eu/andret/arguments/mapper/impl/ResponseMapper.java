/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.api.annotation.ArgumentResponse;
import eu.andret.arguments.entity.MappingConfig;
import eu.andret.arguments.entity.ResponseMappingSet;
import eu.andret.arguments.mapper.IResponseMapper;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The response mapper implementation.
 *
 * @author Andret
 * @since Nov 25, 2021
 */
@Value
public class ResponseMapper implements IResponseMapper {
	@NotNull
	MappingConfig mappingConfig;

	@NotNull
	@Override
	public List<String> mapResponse(@NotNull final Method method, @NotNull final Object result) {
		return createResponseList(result).stream()
				.map(getMappingFunction(method))
				.collect(Collectors.toList());
	}

	@NotNull
	@SuppressWarnings("unchecked")
	private Function<Object, String> getMappingFunction(@NotNull final Method method) {
		return (Function<Object, String>) Optional.of(method)
				.map(this::getResponseMapper)
				.map(ResponseMappingSet::getFunction)
				.orElse(String::valueOf);
	}

	@Nullable
	private ResponseMappingSet<?> getResponseMapper(@NotNull final Method method) {
		final ArgumentResponse annotation = method.getDeclaredAnnotation(ArgumentResponse.class);
		if (annotation != null) {
			return mappingConfig.getArgumentResponseMapper(annotation.value());
		}
		final Class<?> returnType = method.getReturnType();
		if (returnType.isArray()) {
			return mappingConfig.getTypeResponseMapper(returnType.getComponentType());
		}
		if (Collection.class.isAssignableFrom(returnType)) {
			final Type[] arguments = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments();
			return mappingConfig.getTypeResponseMapper((Class<?>) arguments[0]);
		}
		return mappingConfig.getTypeResponseMapper(returnType);
	}

	@NotNull
	private List<Object> createResponseList(@NotNull final Object result) {
		if (result.getClass().isArray()) {
			return Optional.of(result)
					.map(Object[].class::cast)
					.stream()
					.flatMap(Arrays::stream)
					.collect(Collectors.toList());
		}
		if (Collection.class.isAssignableFrom(result.getClass())) {
			return Optional.of(result)
					.map(object -> (Collection<?>) object)
					.stream()
					.flatMap(Collection::stream)
					.collect(Collectors.toList());
		}
		return Collections.singletonList(result);
	}
}
