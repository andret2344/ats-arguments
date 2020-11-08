/*
 * Copyright Andret (c) 2018-2020 Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.annotation.Argument;
import eu.andret.arguments.mapper.IPermissionMapper;
import lombok.Value;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.lang.reflect.Method;

/**
 * An implementation for {@link eu.andret.arguments.mapper.IPermissionMapper}.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
@Value
public class PermissionMapper implements IPermissionMapper {
	@Override
	public boolean mapPermission(Method method, CommandSender sender) {
		if (sender.isOp()) {
			return true;
		}
		if (sender instanceof ConsoleCommandSender) {
			return true;
		}
		Argument argument = method.getAnnotation(Argument.class);
		if (argument.permission().equals("")) {
			return true;
		}
		return sender.hasPermission(argument.permission());
	}
}
