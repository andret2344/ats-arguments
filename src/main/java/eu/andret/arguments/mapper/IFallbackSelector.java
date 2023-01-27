/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.CommandTree;
import eu.andret.arguments.IMapper;
import eu.andret.arguments.api.annotation.Mapper;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.List;

/**
 * The interface to invoke the fallback method.
 *
 * @author Andret
 * @since Sep 02, 2021
 */
public interface IFallbackSelector extends IMapper {
	/**
	 * Invokes fallback method on basis of annotation value.
	 *
	 * @param mapper The {@link Mapper} annotation of failed mapping.
	 * @param targetClass The {@link Class} that's instance was to be created.
	 * @param node The node to search for fallbacks in its value class or any ancestor.
	 * @param <E> The {@link JavaPlugin} subclass.
	 *
	 * @return The result of method's invocation providing sender and executorClass instance.
	 */
	@NotNull <E extends JavaPlugin> List<Method> selectFallback(@Nullable Mapper mapper,
																@NotNull Class<?> targetClass,
																@NotNull CommandTree<E>.Node node);
}
