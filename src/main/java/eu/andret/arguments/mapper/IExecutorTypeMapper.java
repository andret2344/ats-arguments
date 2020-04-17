package eu.andret.arguments.mapper;

import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;

public interface IExecutorTypeMapper {
	boolean mapExecutorType(Method method, CommandSender sender);
}
