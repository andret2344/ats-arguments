package eu.andret.arguments.mapper;

import eu.andret.arguments.AnnotatedCommandExecutor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.List;

public interface ICommandToMethodMapper {
	List<Method> mapCommandToMethod(Class<? extends AnnotatedCommandExecutor> commandClass, String[] command, CommandSender sender);
}
