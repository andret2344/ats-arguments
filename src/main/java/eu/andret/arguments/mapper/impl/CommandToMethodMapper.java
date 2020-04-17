package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.Mapper;
import eu.andret.arguments.mapper.ICommandToMethodMapper;
import eu.andret.arguments.mapper.IExecutorTypeMapper;
import eu.andret.arguments.mapper.IMethodNameMapper;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * Class that maps command to exact method to be called.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
public final class CommandToMethodMapper implements ICommandToMethodMapper {
	private final IMethodNameMapper methodNameMapper = new MethodNameMapper();
	private final IExecutorTypeMapper executorTypeMapper = new ExecutorTypeMapper();
	private final ArgumentsMapper argumentsMapper = new ArgumentsMapper();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Method> mapCommandToMethod(Class<? extends AnnotatedCommandExecutor> commandClass,
											   String[] command,
											   CommandSender sender,
											   Map<String, Mapper<?>> mappers) {
		return Arrays.stream(commandClass.getDeclaredMethods())
				.filter(method -> methodNameMapper.mapMethodName(method, command))
				.filter(method -> executorTypeMapper.mapExecutorType(method, sender))
				.filter(method -> argumentsMapper.mapArguments(method, command, mappers))
				.findFirst();
	}
}
