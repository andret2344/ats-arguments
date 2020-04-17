package eu.andret.arguments.mapper;

import eu.andret.arguments.AnnotatedCommandExecutor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;

public interface IMethodInvoker {
	void invokeMethod(Method method, String[] command, CommandSender sender, Class<? extends AnnotatedCommandExecutor> executor);
}
