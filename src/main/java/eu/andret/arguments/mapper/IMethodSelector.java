/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.AnnotatedCommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

/**
 * The interface to select the selected method.
 *
 * @param <E> The JavaPlugin instance.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
public interface IMethodSelector<E extends JavaPlugin> extends IMapper {
	/**
	 * Invokes one of  methods inside method and correctly puts all arguments.
	 *
	 * @param method The {@link Method} containing method to be called.
	 * @param command The real command arguments array.
	 * @param sender The {@link CommandSender} who actually performed the command.
	 * @param executorClass The class reference, where the method was written.
	 * @param parameters The parameters that will be put into constructor of {@link AnnotatedCommandExecutor}.
	 *
	 * @return The result of method's invocation providing sender and executorClass instance.
	 */
	@NotNull
	Object invokeMethod(@NotNull Method method,
						@NotNull String[] command,
						@NotNull CommandSender sender,
						@NotNull Class<? extends AnnotatedCommandExecutor<E>> executorClass,
						@NotNull Object... parameters);
}
