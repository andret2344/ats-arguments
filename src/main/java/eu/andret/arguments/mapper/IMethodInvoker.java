package eu.andret.arguments.mapper;

import eu.andret.arguments.AnnotatedCommandExecutor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;

/**
 * The interface to invoke the selected method.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
public interface IMethodInvoker {
	/**
	 * Invokes the method and correctly puts all arguments.
	 *
	 * @param method The {@link java.lang.reflect.Method} to be called.
	 * @param command The real command arguments array.
	 * @param sender The {@link org.bukkit.command.CommandSender} who actually performed the
	 * command.
	 * @param executor The class reference, where the method was written.
	 */
	void invokeMethod(Method method, String[] command, CommandSender sender, Class<? extends AnnotatedCommandExecutor> executor);
}
