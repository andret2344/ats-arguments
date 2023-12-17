/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.CommandTree;
import eu.andret.arguments.api.annotation.ArgumentFallback;
import eu.andret.arguments.api.annotation.Mapper;
import eu.andret.arguments.api.annotation.TypeFallback;
import eu.andret.arguments.mapper.IFallbackSelector;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An implementation of {@link IFallbackSelector}.
 *
 * @author Andret
 * @since Sep 02, 2021
 */
@Value
@NonFinal
public class FallbackSelector implements IFallbackSelector {
	private static final Class<?>[] VALID_PARAMETERS_ARRAY = {String.class};

	@NotNull
	@Override
	public <E extends JavaPlugin> List<Method> selectFallback(
			@Nullable final Mapper mapper,
			@NotNull final Class<?> targetClass,
			@NotNull final CommandTree<E>.Node node) {
		if (mapper == null) {
			return getTypeFallbacks(targetClass, node);
		}
		return getArgumentFallbacks(mapper.value(), node);
	}

	@NotNull
	private <E extends JavaPlugin> List<Method> getArgumentFallbacks(@NotNull final String argument,
																	 @NotNull final CommandTree<E>.Node node) {
		return node.getBranch().stream()
				.map(CommandTree.Node::getClazz)
				.map(Class::getDeclaredMethods)
				.flatMap(Arrays::stream)
				.filter(method -> method.isAnnotationPresent(ArgumentFallback.class))
				.filter(method -> Arrays.asList(method.getAnnotation(ArgumentFallback.class).value())
						.contains(argument))
				.filter(method -> Arrays.equals(method.getParameterTypes(), VALID_PARAMETERS_ARRAY))
				.sorted((o1, o2) -> o2.getAnnotation(ArgumentFallback.class).priority().getSlot()
						- o1.getAnnotation(ArgumentFallback.class).priority().getSlot())
				.collect(Collectors.toList());
	}

	@NotNull
	private <E extends JavaPlugin> List<Method> getTypeFallbacks(@NotNull final Class<?> type,
																 @NotNull final CommandTree<E>.Node node) {
		return node.getBranch().stream()
				.map(CommandTree.Node::getClazz)
				.map(Class::getDeclaredMethods)
				.flatMap(Arrays::stream)
				.filter(method -> method.isAnnotationPresent(TypeFallback.class))
				.filter(method -> Arrays.asList(method.getAnnotation(TypeFallback.class).value())
						.contains(type))
				.filter(method -> Arrays.equals(method.getParameterTypes(), VALID_PARAMETERS_ARRAY))
				.sorted((o1, o2) -> o2.getAnnotation(TypeFallback.class).priority().getSlot()
						- o1.getAnnotation(TypeFallback.class).priority().getSlot())
				.collect(Collectors.toList());
	}
}
