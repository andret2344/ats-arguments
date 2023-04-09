/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.AnnotatedCommand;
import eu.andret.arguments.entity.MappingConfig;
import eu.andret.arguments.entity.MappingSet;
import eu.andret.arguments.filter.IArgumentsFilter;
import eu.andret.arguments.filter.IExecutorTypeFilter;
import eu.andret.arguments.filter.IMethodNameFilter;
import eu.andret.arguments.filter.impl.ArgumentsFilter;
import eu.andret.arguments.filter.impl.ExecutorTypeFilter;
import eu.andret.arguments.filter.impl.MethodNameFilter;
import eu.andret.arguments.mapper.ICommandToMethodMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.Arrays;
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
	MappingConfig mappingConfig;
	IMethodNameFilter methodNameFilter;
	IExecutorTypeFilter executorTypeFilter;
	IArgumentsFilter argumentsFilter;

	/**
	 * Constructor that initializes fields.
	 *
	 * @param mappingConfig The map of the {@link String}-{@link MappingSet} pair.
	 */
	public CommandToMethodMapper(final MappingConfig mappingConfig) {
		this(mappingConfig, new MethodNameFilter(), new ExecutorTypeFilter(), new ArgumentsFilter(mappingConfig));
	}

	@Override
	public Optional<Method> mapCommandToMethod(final Method[] methods, final String[] command, final CommandSender sender, final AnnotatedCommand.Options options) {
		return Arrays.stream(methods)
				.filter(method -> methodNameFilter.filterMethodName(method, command, options))
				.filter(method -> executorTypeFilter.filterExecutorType(method, sender))
				.filter(method -> argumentsFilter.filterArguments(method, command))
				.findFirst();
	}
}
