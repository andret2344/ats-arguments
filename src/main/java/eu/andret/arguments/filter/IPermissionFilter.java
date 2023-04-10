/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.filter;

import eu.andret.arguments.IMapper;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;

/**
 * An interface for mapping permissions.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
public interface IPermissionFilter extends IMapper {
	/**
	 * Maps the argument permissions.
	 *
	 * @param method The {@link Method} to be analyzed.
	 * @param sender The {@link CommandSender} who performed the command.
	 * @return {@code true} if sender has permission to execute the command or is op, {@code false} otherwise.
	 */
	boolean filterPermission(Method method, CommandSender sender);
}
