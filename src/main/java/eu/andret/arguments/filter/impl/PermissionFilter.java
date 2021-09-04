/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.filter.impl;

import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.filter.IPermissionFilter;
import lombok.Value;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.lang.reflect.Method;

/**
 * An implementation for {@link IPermissionFilter}.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
@Value
public class PermissionFilter implements IPermissionFilter {
	@Override
	public boolean filterPermission(final Method method, final CommandSender sender) {
		if (sender.isOp()) {
			return true;
		}
		if (sender instanceof ConsoleCommandSender) {
			return true;
		}
		final Argument argument = method.getAnnotation(Argument.class);
		if (argument.permission().isEmpty()) {
			return true;
		}
		return sender.hasPermission(argument.permission());
	}
}
