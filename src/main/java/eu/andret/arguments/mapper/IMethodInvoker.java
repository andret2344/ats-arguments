/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.entity.ExecutionCall;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The interface to invoke the selected method.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
public interface IMethodInvoker extends IMapper {
	/**
	 * Invokes one of  methods inside Execution call and correctly puts all arguments.
	 *
	 * @param call The {@link ExecutionCall} containing method to be called.
	 * @param command The real command arguments array.
	 * @param sender The {@link CommandSender} who actually performed the command.
	 * @param executor The class reference, where the method was written.
	 *
	 * @return The result of method's invocation providing sender and executorClass instance.
	 */
	Object invokeMethod(ExecutionCall call, String[] command, CommandSender sender, Class<? extends AnnotatedCommandExecutor<? extends JavaPlugin>> executor);
}
