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
	MappingConfig mappingConfig;

	@NotNull
	@Override
	@SuppressWarnings("unchecked")
	public List<String> mapResponse(@NotNull final Method method, @NotNull final Object object) {
		final ResponseMappingSet<?> mapper = getResponseMapper(method);
		if (mapper == null) {
			return createResponseStream(object);
		}
		final Function<Object, String> function = (Function<Object, String>) mapper.getFunction();
		return Collections.singletonList(function.apply(object));
	}

	@Nullable
	private ResponseMappingSet<?> getResponseMapper(@NotNull final Method method) {
		final ArgumentResponse annotation = method.getDeclaredAnnotation(ArgumentResponse.class);
		if (annotation != null) {
			return mappingConfig.getArgumentResponseMapper(annotation.value());
		}
		return mappingConfig.getTypeResponseMapper(method.getReturnType());
	}

	@NotNull
	private List<String> createResponseStream(@NotNull final Object result) {
		if (result.getClass().isArray()) {
			return Optional.of(result)
					.map(Object[].class::cast)
					.stream()
					.flatMap(Arrays::stream)
					.map(String::valueOf)
					.collect(Collectors.toList());
		}
		if (Collection.class.isAssignableFrom(result.getClass())) {
			return Optional.of(result)
					.map(x -> (Collection<?>) x)
					.stream()
					.flatMap(Collection::stream)
					.map(String::valueOf)
					.collect(Collectors.toList());
		}
		return Collections.singletonList(String.valueOf(result));
	}
}
