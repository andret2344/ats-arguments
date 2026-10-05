package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.FallbackException;
import eu.andret.arguments.Util;
import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.api.annotation.Mapper;
import eu.andret.arguments.entity.ExecutionCall;
import eu.andret.arguments.entity.MappingConfig;
import eu.andret.arguments.entity.MappingSet;
import eu.andret.arguments.mapper.IFallbackSelector;
import eu.andret.arguments.mapper.IMethodSelector;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * An implementation of {@link IMethodSelector}.
 *
 * @author Andret
 * @since Sep 03, 2021
 */
public final class MethodSelector implements IMethodSelector {
	private final IFallbackSelector fallbackSelector;
	private final MappingConfig mappingConfig;

	/**
	 * Constructor that sets all fields.
	 *
	 * @param fallbackSelector The fallback selector.
	 * @param mappingConfig The config.
	 */
	public MethodSelector(@NotNull final IFallbackSelector fallbackSelector, @NotNull final MappingConfig mappingConfig) {
		this.fallbackSelector = fallbackSelector;
		this.mappingConfig = mappingConfig;
	}

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
	public <E extends JavaPlugin> ExecutionCall selectMethod(@NotNull final Method method,
			@NotNull final String[] args,
			@NotNull final Class<? extends AnnotatedCommandExecutor<E>> executorClass) {
		try {
			return new ExecutionCall(List.of(method), recalculateArguments(method, args));
		} catch (final FallbackException ex) {
			final List<Method> methods = fallbackSelector.selectFallback(ex.getMapper(), ex.getTargetClass(), executorClass);
			return new ExecutionCall(methods, new Object[]{ex.getValue()});
		}
	}

	@NotNull
	private Object[] recalculateArguments(@NotNull final Method method, @NotNull final String[] args) {
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
			} else {
				data[i] = map(mapper, method.getParameters()[i].getType(), args[i + skip]);
			}
		}
		return data;
	}

	@NotNull
	private <T> T map(@Nullable final Mapper mapper, @NotNull final Class<T> type, @NotNull final String value) {
		return getMappingSet(mapper, type)
				.map(mappingSet -> convert(mapper, mappingSet, type, value))
				.orElseGet(() -> Util.convert(type, value));
	}

	@NotNull
	@SuppressWarnings("unchecked")
	private <T> Optional<MappingSet<T>> getMappingSet(@Nullable final Mapper mapper,
			@NotNull final Class<T> clazz) {
		final Optional<MappingSet<T>> mappingSet = Optional.ofNullable(mapper)
				.map(Mapper::value)
				.map(mappingConfig::getArgumentMapper)
				.filter(set -> set.clazz().equals(clazz))
				.map(set -> (MappingSet<T>) set);
		if (mappingSet.isPresent()) {
			return mappingSet;
		}
		return Optional.of(clazz)
				.map(mappingConfig::getTypeMapper)
				.map(set -> (MappingSet<T>) set);
	}

	@NotNull
	private <T> T convert(@Nullable final Mapper mapper, @NotNull final MappingSet<T> mappingSet,
			@NotNull final Class<T> targetClass, @NotNull final String value) {
		final Object result = mappingSet.function().apply(value);
		if (mappingSet.fallbackCondition().test(result)) {
			throw new FallbackException("Fallback condition failed", mapper, targetClass, value);
		}
		return targetClass.cast(result);
	}
}
