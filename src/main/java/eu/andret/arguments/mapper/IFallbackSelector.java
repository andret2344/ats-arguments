/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.annotation.Mapper;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.List;

/**
 * The interface to invoke the fallback method.
 *
 * @param <E> The JavaPlugin instance.
 *
 * @author Andret
 * @since Sep 02, 2021
 */
public interface IFallbackSelector<E extends JavaPlugin> extends IMapper {
	/**
	 * Invokes fallback method on basis of annotation value.
	 *
	 * @param mapper The {@link Mapper} annotation of failed mapping.
	 * @param targetClass The {@link Class} that's instance was to be created.
	 * @param executorClass The class reference, where the method was written.
	 *
	 * @return The result of method's invocation providing sender and executorClass instance.
	 */
	@NotNull
	List<Method> selectFallback(@Nullable Mapper mapper,
								@NotNull Class<?> targetClass,
								@NotNull Class<? extends AnnotatedCommandExecutor<E>> executorClass);
}
