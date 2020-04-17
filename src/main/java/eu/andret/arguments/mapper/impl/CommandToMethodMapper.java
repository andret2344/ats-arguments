package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.Mapper;
import eu.andret.arguments.mapper.ICommandToMethodMapper;
import eu.andret.arguments.mapper.IExecutorTypeMapper;
import eu.andret.arguments.mapper.IMethodNameMapper;
import lombok.Value;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of {@link eu.andret.arguments.mapper.ICommandToMethodMapper}.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
@Value
public class CommandToMethodMapper implements ICommandToMethodMapper {
	IMethodNameMapper methodNameMapper = new MethodNameMapper();
	IExecutorTypeMapper executorTypeMapper = new ExecutorTypeMapper();
	ArgumentsMapper argumentsMapper = new ArgumentsMapper();
	Map<String, Mapper<?>> mappers;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Method> mapCommandToMethod(Class<? extends AnnotatedCommandExecutor> commandClass, String[] command, CommandSender sender) {
		return Arrays.stream(commandClass.getDeclaredMethods())
				.filter(method -> methodNameMapper.mapMethodName(method, command))
				.filter(method -> executorTypeMapper.mapExecutorType(method, sender))
				.filter(method -> argumentsMapper.mapArguments(method, command, mappers))
				.findFirst();
	}
}
