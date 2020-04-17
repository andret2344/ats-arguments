package eu.andret.arguments.mapper;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.Mapper;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

public interface ICommandToMethodMapper {
	Optional<Method> mapCommandToMethod(Class<? extends AnnotatedCommandExecutor> commandClass,
										String[] command,
										CommandSender sender,
										Map<String, Mapper<?>> mappers);
}
