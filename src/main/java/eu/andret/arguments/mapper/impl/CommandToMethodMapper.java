/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */
package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.Mapper;
import eu.andret.arguments.mapper.ICommandToMethodMapper;
import eu.andret.arguments.mapper.IExecutorTypeMapper;
import eu.andret.arguments.mapper.IMethodNameMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor(access = AccessLevel.NONE)
public class CommandToMethodMapper implements ICommandToMethodMapper {
	Map<String, Mapper<?>> mappers;
	IMethodNameMapper methodNameMapper = new MethodNameMapper();
	IExecutorTypeMapper executorTypeMapper = new ExecutorTypeMapper();
	ArgumentsMapper argumentsMapper;

	public CommandToMethodMapper(Map<String, Mapper<?>> mappers) {
		this.mappers = mappers;
		argumentsMapper = new ArgumentsMapper(mappers);
	}

	@Override
	public Optional<Method> mapCommandToMethod(Class<? extends AnnotatedCommandExecutor> commandClass, String[] command, CommandSender sender) {
		return Arrays.stream(commandClass.getDeclaredMethods())
				.filter(method -> methodNameMapper.mapMethodName(method, command))
				.filter(method -> executorTypeMapper.mapExecutorType(method, sender))
				.filter(method -> argumentsMapper.mapArguments(method, command))
				.findFirst();
	}
}
