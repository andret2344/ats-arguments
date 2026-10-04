package eu.andret.arguments.filter;

import eu.andret.arguments.IMapper;
import eu.andret.arguments.api.annotation.Argument;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;

/**
 * An Interface for mapping Executor type.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
@FunctionalInterface
public interface IExecutorTypeFilter extends IMapper {
	/**
	 * @param method The method that will be analyzed.
	 * @param sender The real command sender who performed command.
	 *
	 * @return {@code true} if command executor matches with the one provided in {@link Argument#executorType()}, {@code
	 * 		false} otherwise.
	 */
	boolean filterExecutorType(Method method, CommandSender sender);
}
