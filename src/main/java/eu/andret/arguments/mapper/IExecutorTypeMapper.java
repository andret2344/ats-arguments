/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;

/**
 * An Interface for mapping Executor type.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
public interface IExecutorTypeMapper extends IMapper {

	/**
	 * @param method The method that will be analyzed.
	 * @param sender The real command sender who performed command.
	 *
	 * @return {@code true} if command executor matches with the one provided in {@link
	 * eu.andret.arguments.annotation.Argument#executorType()}, {@code false} otherwise.
	 */
	boolean mapExecutorType(Method method, CommandSender sender);
}
