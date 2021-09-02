/*
 * Copyright Andret (c) 2018-2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.annotation.Mapper;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The interface to invoke the fallback method.
 *
 * @param <E> The JavaPlugin instance.
 *
 * @author Andret
 * @since Sep 02, 2021
 */
public interface IFallbackInvoker<E extends JavaPlugin> {
	/**
	 * Invokes fallback method on basis of annotation value.
	 *
	 * @param mapper The {@link Mapper} annotation of failed mapping.
	 * @param text The real command argument.
	 * @param targetClass The {@link Class} that's instance was to be created.
	 * @param executor The class reference, where the method was written.
	 *
	 * @return The result of method's invocation providing sender and executorClass instance.
	 */
	@NotNull
	Object invokeFallback(@Nullable Mapper mapper, @NotNull String text, @NotNull Class<?> targetClass,
						  @NotNull AnnotatedCommandExecutor<E> executor);
}
