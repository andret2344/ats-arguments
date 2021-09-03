/*
 * Copyright Andret (c) 2018-2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.annotation.Argument;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.List;

/**
 * An interface to call methods.
 *
 * @param <E> The JavaPlugin instance.
 *
 * @author Andret
 * @since Sep 03, 2021
 */
public interface IMethodInvoker<E extends JavaPlugin> {
	/**
	 * @param methods Methods to be called.
	 * @param data The data to be passed as methods' arguments.
	 * @param sender The sender who executed the command.
	 * @param executorClass The class containing {@link Argument} methods.
	 * @param parameters The executor's constructor parameters.
	 *
	 * @return List with results from called methods.
	 */
	List<Object> invokeMethods(@NotNull List<Method> methods,
							   @NotNull Object[] data,
							   @NotNull CommandSender sender,
							   @NotNull Class<? extends AnnotatedCommandExecutor<E>> executorClass,
							   @NotNull Object... parameters);
}
