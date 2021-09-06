/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.annotation.ArgumentFallback;
import eu.andret.arguments.api.annotation.Mapper;
import eu.andret.arguments.api.annotation.TypeFallback;
import eu.andret.arguments.mapper.IFallbackSelector;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An implementation of {@link IFallbackSelector}.
 *
 * @param <E> The JavaPlugin instance.
 *
 * @author Andret
 * @since Sep 02, 2021
 */
@Value
@NonFinal
public class FallbackSelector<E extends JavaPlugin> implements IFallbackSelector<E> {
	private static final Class<?>[] VALID_PARAMETERS_ARRAY = {String.class};
	Map<CommandSender, AnnotatedCommandExecutor<E>> executors = new HashMap<>();

	@NotNull
	@Override
	public List<Method> selectFallback(@Nullable final Mapper mapper,
									   @NotNull final Class<?> targetClass,
									   @NotNull final Class<? extends AnnotatedCommandExecutor<E>> executorClass) {
		if (mapper == null) {
			return getTypeFallbacks(targetClass, executorClass);
		}
		return getArgumentFallbacks(mapper.value(), executorClass);
	}

	@NotNull
	private List<Method> getArgumentFallbacks(@NotNull final String argument,
											  @NotNull final Class<? extends AnnotatedCommandExecutor<E>> executor) {
		return Arrays.stream(executor.getDeclaredMethods())
				.filter(method -> method.isAnnotationPresent(ArgumentFallback.class))
				.filter(method -> Arrays.asList(method.getAnnotation(ArgumentFallback.class).value())
						.contains(argument))
				.filter(method -> Arrays.equals(method.getParameterTypes(), VALID_PARAMETERS_ARRAY))
				.sorted((o1, o2) -> o2.getAnnotation(ArgumentFallback.class).priority().getSlot()
						- o1.getAnnotation(ArgumentFallback.class).priority().getSlot())
				.collect(Collectors.toList());
	}

	@NotNull
	private List<Method> getTypeFallbacks(@NotNull final Class<?> type,
										  @NotNull final Class<? extends AnnotatedCommandExecutor<E>> executor) {
		return Arrays.stream(executor.getDeclaredMethods())
				.filter(method -> method.isAnnotationPresent(TypeFallback.class))
				.filter(method -> Arrays.asList(method.getAnnotation(TypeFallback.class).value())
						.contains(type))
				.filter(method -> Arrays.equals(method.getParameterTypes(), VALID_PARAMETERS_ARRAY))
				.sorted((o1, o2) -> o2.getAnnotation(TypeFallback.class).priority().getSlot()
						- o1.getAnnotation(TypeFallback.class).priority().getSlot())
				.collect(Collectors.toList());
	}
}
