/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.FallbackException;
import eu.andret.arguments.Util;
import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.api.annotation.Mapper;
import eu.andret.arguments.entity.MappingConfig;
import eu.andret.arguments.entity.MappingSet;
import eu.andret.arguments.mapper.IFallbackSelector;
import eu.andret.arguments.mapper.IMethodSelector;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * An implementation of {@link IMethodSelector}.
 *
 * @author Andret
 * @since Sep 03, 2021
 */
@Value
@AllArgsConstructor
@Getter(AccessLevel.NONE)
public class MethodSelector implements IMethodSelector {
	IFallbackSelector fallbackSelector;
	MappingConfig mappingConfig;

	/**
	 * Smallest acceptable constructor.
	 *
	 * @param fallbackSelector The fallback selector.
	 */
	public MethodSelector(@NotNull final IFallbackSelector fallbackSelector) {
		this(fallbackSelector, new MappingConfig());
	}

	@Override
	@NotNull
	public Object[] recalculateArguments(@NotNull final Method method, @NotNull final String... args) {
		final Argument argument = method.getAnnotation(Argument.class);
		final Object[] data = new Object[method.getParameterCount()];
		int skip = 0;
		for (int i = 0; i < method.getParameterCount(); i++) {
			if (i == argument.position()) {
				skip++;
			}
			final Mapper mapper = method.getParameters()[i].getAnnotation(Mapper.class);
			if (method.getParameters()[i].isVarArgs()) {
				final Class<?> type = method.getParameters()[i].getType().getComponentType();
				final int length = args.length - i + skip - 2;
				final Object array = Array.newInstance(type, length);
				for (int j = 0; j < length; j++) {
					Array.set(array, j, map(mapper, type, args[j + i + skip]));
				}
				data[i] = array;
				break;
			} else {
				data[i] = map(mapper, method.getParameters()[i].getType(), args[i + skip]);
			}
		}
		return data;
	}

	@NotNull
	private Object map(@Nullable final Mapper mapper, @NotNull final Class<?> type, @NotNull final String value) {
		return getMappingSet(mapper, type)
				.map(mappingSet -> convert(mapper, mappingSet, type, value))
				.orElseGet(() -> Util.convert(type, value));
	}

	@NotNull
	private Optional<? extends MappingSet<?>> getMappingSet(@Nullable final Mapper mapper,
															@NotNull final Class<?> clazz) {
		final Optional<? extends MappingSet<?>> mappingSet = Optional.ofNullable(mapper)
				.map(Mapper::value)
				.map(mappingConfig::getArgumentMapper)
				.filter(set -> set.getClazz().equals(clazz));
		if (mappingSet.isPresent()) {
			return mappingSet;
		}
		return Optional.of(clazz).map(mappingConfig::getTypeMapper);
	}

	@NotNull
	private Object convert(@Nullable final Mapper mapper, @NotNull final MappingSet<?> mappingSet,
						   @NotNull final Class<?> targetClass, @NotNull final String value) {
		final Object result = mappingSet.getFunction().apply(value);
		if (mappingSet.getFallbackCondition().test(result)) {
			throw new FallbackException("Fallback condition failed", mapper, targetClass, value);
		}
		return targetClass.cast(result);
	}
}
