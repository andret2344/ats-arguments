package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.mapper.ICommandToMethodMapper;
import eu.andret.arguments.mapper.IExecutorTypeMapper;
import eu.andret.arguments.mapper.IMethodNameMapper;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandToMethodMapper implements ICommandToMethodMapper {
	private final IMethodNameMapper methodNameMapper = new MethodNameMapper();
	private final IExecutorTypeMapper executorTypeMapper = new ExecutorTypeMapper();

	@Override
	public List<Method> mapCommandToMethod(Class<? extends AnnotatedCommandExecutor> commandClass, String[] command, CommandSender sender) {
		return Arrays.stream(commandClass.getDeclaredMethods())
				.filter(method -> methodNameMapper.mapMethodName(method, command))
				.filter(method -> executorTypeMapper.mapExecutorType(method, sender))
				.collect(Collectors.toList());
	}
}
