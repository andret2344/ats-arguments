/*
 * Copyright Andret (c) 2018=2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.entity.ExecutionCall;
import eu.andret.arguments.entity.Mapper;
import eu.andret.arguments.mapper.IArgumentsMapper;
import eu.andret.arguments.mapper.ICommandToMethodMapper;
import eu.andret.arguments.mapper.IExecutionCallMapper;
import eu.andret.arguments.mapper.IExecutorTypeMapper;
import eu.andret.arguments.mapper.IMethodNameMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of {@link ICommandToMethodMapper}.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
@Value
@AllArgsConstructor
@Getter(AccessLevel.NONE)
public class CommandToMethodMapper implements ICommandToMethodMapper {
	Map<String, Mapper<?>> mappers;
	IExecutionCallMapper executionCallMapper;
	IMethodNameMapper methodNameMapper;
	IExecutorTypeMapper executorTypeMapper;
	IArgumentsMapper argumentsMapper;

	public CommandToMethodMapper(Map<String, Mapper<?>> mappers) {
		this(mappers, new ExecutionCallMapper(), new MethodNameMapper(), new ExecutorTypeMapper(), new ArgumentsMapper(mappers));
	}

	@Override
	public Optional<ExecutionCall> mapCommandToMethod(Method[] methods, String[] command, CommandSender sender) {
		return Arrays.stream(methods)
				.map(method -> executionCallMapper.mapExecutionCall(method, methods))
				.filter(executionCall -> methodNameMapper.mapMethodName(executionCall.getMethod(), command))
				.filter(executionCall -> executorTypeMapper.mapExecutorType(executionCall.getMethod(), sender))
				.filter(executionCall -> argumentsMapper.mapArguments(executionCall.getMethod(), command))
				.findFirst();
	}
}
