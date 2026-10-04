package eu.andret.arguments.filter.impl;

import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.api.entity.ExecutorType;
import eu.andret.arguments.filter.IExecutorTypeFilter;
import lombok.Value;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

/**
 * An implementation of {@link IExecutorTypeFilter}.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
@Value
public class ExecutorTypeFilter implements IExecutorTypeFilter {
	@Override
	public boolean filterExecutorType(final Method method, final CommandSender sender) {
		final ExecutorType executorType = method.getAnnotation(Argument.class).executorType();
		return executorType.equals(ExecutorType.ALL)
				|| executorType.equals(ExecutorType.CONSOLE) && sender instanceof ConsoleCommandSender
				|| executorType.equals(ExecutorType.PLAYER) && sender instanceof Player;
	}
}
