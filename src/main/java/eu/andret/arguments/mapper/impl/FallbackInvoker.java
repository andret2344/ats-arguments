/*
 * Copyright Andret (c) 2018-2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.annotation.ArgumentFallback;
import eu.andret.arguments.api.annotation.Mapper;
import eu.andret.arguments.api.annotation.TypeFallback;
import eu.andret.arguments.mapper.IFallbackInvoker;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * An implementation of {@link IFallbackInvoker}.
 *
 * @author Andret
 * @since Sep 02, 2021
 */
public class FallbackInvoker<E extends JavaPlugin> implements IFallbackInvoker<E> {
	@NotNull
	@Override
	public Object invokeFallback(@Nullable final Mapper mapper,
								 @NotNull final String text,
								 @NotNull final Class<?> targetClass,
								 @NotNull final AnnotatedCommandExecutor<E> executor) {
		return Optional.ofNullable(mapper)
				.map(x -> getArgumentFallbackMethods(x.value(), executor))
				.or(() -> Optional.of(getTypeFallbackMethods(targetClass, executor)))
				.stream()
				.flatMap(Collection::stream)
				.map(method -> invokeMethod(method, executor, text))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	@NotNull
	private List<Method> getArgumentFallbackMethods(@NotNull final String argument,
													@NotNull final AnnotatedCommandExecutor<E> executor) {
		return Arrays.stream(executor.getClass().getDeclaredMethods())
				.filter(method -> method.isAnnotationPresent(ArgumentFallback.class))
				.filter(method -> Arrays.asList(method.getAnnotation(ArgumentFallback.class).value())
						.contains(argument))
				.filter(method -> method.getParameterCount() == 1)
				.sorted((o1, o2) -> o2.getAnnotation(ArgumentFallback.class).priority().getSlot()
						- o1.getAnnotation(ArgumentFallback.class).priority().getSlot())
				.collect(Collectors.toList());
	}

	@NotNull
	private List<Method> getTypeFallbackMethods(@NotNull final Class<?> type,
												@NotNull final AnnotatedCommandExecutor<E> executor) {
		return Arrays.stream(executor.getClass().getDeclaredMethods())
				.filter(method -> method.isAnnotationPresent(TypeFallback.class))
				.filter(method -> Arrays.asList(method.getAnnotation(TypeFallback.class).value())
						.contains(type))
				.filter(method -> method.getParameterCount() == 1)
				.sorted((o1, o2) -> o2.getAnnotation(TypeFallback.class).priority().getSlot()
						- o1.getAnnotation(TypeFallback.class).priority().getSlot())
				.collect(Collectors.toList());
	}

	@SneakyThrows
	private Object invokeMethod(@NotNull final Method method,
								@NotNull final AnnotatedCommandExecutor<E> executor,
								@NotNull final String text) {
		return method.invoke(executor, text);
	}
}
