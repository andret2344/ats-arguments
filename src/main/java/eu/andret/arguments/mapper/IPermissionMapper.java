package eu.andret.arguments.mapper;

import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;

public interface IPermissionMapper {
	boolean mapPermission(Method method, CommandSender sender);
}
