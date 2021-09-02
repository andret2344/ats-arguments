/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.AnnotatedCommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;

/**
 * The interface to invoke the selected method.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
public interface IMethodInvoker<E extends JavaPlugin> extends IMapper {
	/**
	 * Invokes one of  methods inside method and correctly puts all arguments.
	 *
	 * @param method The {@link Method} containing method to be called.
	 * @param command The real command arguments array.
	 * @param sender The {@link CommandSender} who actually performed the command.
	 * @param executor The class reference, where the method was written.
	 * @param parameters The parameters that will be put into constructor of {@link AnnotatedCommandExecutor}
	 *
	 * @return The result of method's invocation providing sender and executorClass instance.
	 */
	Object invokeMethod(Method method, String[] command, CommandSender sender, Class<? extends AnnotatedCommandExecutor<E>> executor, Object... parameters);
}
