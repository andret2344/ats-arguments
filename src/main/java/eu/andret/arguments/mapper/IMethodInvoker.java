/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.AnnotatedCommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.List;

/**
 * The method invoker interface.
 *
 * @param <E> The JavaPlugin
 *
 * @author Andret
 * @since Nov 25, 2021
 */
public interface IMethodInvoker<E extends JavaPlugin> extends IMapper {
	/**
	 * Invokes the selected method using passed executor and using the data as arguments.
	 *
	 * @param method The method to be invoked.
	 * @param executor Executor that will be the method invoked on.
	 * @param data The array of values that will be passed as method parameters,
	 *
	 * @return The list of values returned from method split by end of line or when returned list or array, or used a
	 * 		return mapper.
	 */
	@NotNull
	List<String> invokeMethod(@NotNull Method method,
							  @NotNull AnnotatedCommandExecutor<E> executor,
							  @NotNull Object[] data);
}
