/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.entity.ExecutionCall;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * An interface to call methods.
 *
 * @param <E> The JavaPlugin instance.
 *
 * @author Andret
 * @since Sep 03, 2021
 */
public interface IMethodInvoker<E extends JavaPlugin> extends IMapper {
	/**
	 * @param executionCall The execution call consisting of methods and its arguments.
	 * @param sender The sender who executed the command.
	 * @param executorClass The class containing {@link Argument} methods.
	 * @param parameters The executor's constructor parameters.
	 *
	 * @return List with results from called methods.
	 */
	List<Object> invokeMethods(@NotNull ExecutionCall executionCall,
							   @NotNull CommandSender sender,
							   @NotNull Class<? extends AnnotatedCommandExecutor<E>> executorClass,
							   @NotNull Object... parameters);
}
