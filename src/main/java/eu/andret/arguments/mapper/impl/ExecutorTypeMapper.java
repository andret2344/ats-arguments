/*
 * Copyright Andret (c) 2018-2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.annotation.Argument;
import eu.andret.arguments.entity.ExecutorType;
import eu.andret.arguments.mapper.IExecutorTypeMapper;
import lombok.Value;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

/**
 * An implementation of {@link eu.andret.arguments.mapper.IExecutorTypeMapper}.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
@Value
public class ExecutorTypeMapper implements IExecutorTypeMapper {
	@Override
	public boolean mapExecutorType(Method method, CommandSender sender) {
		ExecutorType executorType = method.getAnnotation(Argument.class).executorType();
		return executorType.equals(ExecutorType.ALL)
				|| executorType.equals(ExecutorType.CONSOLE) && sender instanceof ConsoleCommandSender
				|| executorType.equals(ExecutorType.PLAYER) && sender instanceof Player;
	}
}
