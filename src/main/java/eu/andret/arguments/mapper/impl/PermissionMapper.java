package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.annotation.Argument;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.lang.reflect.Method;

public final class PermissionMapper {
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
