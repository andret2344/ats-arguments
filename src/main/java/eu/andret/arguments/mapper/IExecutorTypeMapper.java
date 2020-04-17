package eu.andret.arguments.mapper;

import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;

/**
 * Mapper for Executor type
 *
 * @author Andret
 * @since Apr 17, 2020
 */
public interface IExecutorTypeMapper {

	/**
	 * @param method The method
	 * @param sender The real command sender
	 *
	 * @return <code>true</code> if command executor matches with the one provided in {@link
	 * eu.andret.arguments.annotation.Argument#executorType()}
	 */
	boolean mapExecutorType(Method method, CommandSender sender);
}
