/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.entity.ExecutionCall;
import eu.andret.arguments.entity.MappingSet;
import eu.andret.arguments.filter.IArgumentsFilter;
import eu.andret.arguments.filter.IExecutorTypeFilter;
import eu.andret.arguments.filter.IMethodNameFilter;
import eu.andret.arguments.filter.impl.ArgumentsFilter;
import eu.andret.arguments.filter.impl.ExecutorTypeFilter;
import eu.andret.arguments.filter.impl.MethodNameFilter;
import eu.andret.arguments.mapper.ICommandToMethodMapper;
import eu.andret.arguments.mapper.IExecutionCallMapper;
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
	Map<String, MappingSet<?>> argumentMappers;
	Map<Class<?>, MappingSet<?>> typeMappers;
	IExecutionCallMapper executionCallMapper;
	IMethodNameFilter methodNameFilter;
	IExecutorTypeFilter executorTypeFilter;
	IArgumentsFilter argumentsFilter;

	/**
	 * Constructor that initializes fields.
	 *
	 * @param argumentMappers The map of the {@link String}-{@link MappingSet} pair.
	 * @param typeMappers The map of the {@link Class}-{@link MappingSet} pair.
	 */
	public CommandToMethodMapper(final Map<String, MappingSet<?>> argumentMappers, final Map<Class<?>, MappingSet<?>> typeMappers) {
		this(argumentMappers, typeMappers, new ExecutionCallMapper(), new MethodNameFilter(), new ExecutorTypeFilter(), new ArgumentsFilter(argumentMappers, typeMappers));
	}

	@Override
	public Optional<ExecutionCall> mapCommandToMethod(final Method[] methods, final String[] command, final CommandSender sender) {
		return Arrays.stream(methods)
				.map(method -> executionCallMapper.mapExecutionCall(method, methods))
				.filter(executionCall -> methodNameFilter.filterMethodName(executionCall.getMethod(), command))
				.filter(executionCall -> executorTypeFilter.filterExecutorType(executionCall.getMethod(), sender))
				.filter(executionCall -> argumentsFilter.filter(executionCall.getMethod(), command))
				.findFirst();
	}
}
