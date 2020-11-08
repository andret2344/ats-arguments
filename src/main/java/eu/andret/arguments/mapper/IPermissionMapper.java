/*
 * Copyright Andret (c) 2018=2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;

/**
 * An interface for mapping permissions.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
public interface IPermissionMapper extends IMapper {
	/**
	 * Maps the argument permissions.
	 *
	 * @param method The {@link Method} to be analyzed.
	 * @param sender The {@link CommandSender} who performed the command.
	 *
	 * @return {@code true} if sender has permission to execute the command or is op, {@code false}
	 * 		otherwise.
	 */
	boolean mapPermission(Method method, CommandSender sender);
}
