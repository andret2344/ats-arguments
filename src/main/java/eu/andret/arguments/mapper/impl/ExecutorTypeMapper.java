package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.ExecutorType;
import eu.andret.arguments.annotation.Argument;
import eu.andret.arguments.mapper.IExecutorTypeMapper;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public final class ExecutorTypeMapper implements IExecutorTypeMapper {
	@Override
	public boolean mapExecutorType(Method method, CommandSender sender) {
		ExecutorType executorType = method.getAnnotation(Argument.class).executorType();
		return executorType.equals(ExecutorType.ALL)
				|| executorType.equals(ExecutorType.CONSOLE) && sender instanceof ConsoleCommandSender
				|| executorType.equals(ExecutorType.PLAYER) && sender instanceof Player;
	}
}
