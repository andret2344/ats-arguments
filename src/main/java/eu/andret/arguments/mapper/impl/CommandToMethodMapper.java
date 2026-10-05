package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.AnnotatedCommand;
import eu.andret.arguments.entity.MappingConfig;
import eu.andret.arguments.filter.IArgumentsFilter;
import eu.andret.arguments.filter.IExecutorTypeFilter;
import eu.andret.arguments.filter.IMethodNameFilter;
import eu.andret.arguments.filter.impl.ArgumentsFilter;
import eu.andret.arguments.filter.impl.ExecutorTypeFilter;
import eu.andret.arguments.filter.impl.MethodNameFilter;
import eu.andret.arguments.mapper.ICommandToMethodMapper;
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
public final class CommandToMethodMapper implements ICommandToMethodMapper {
	private final IMethodNameFilter methodNameFilter;
	private final IExecutorTypeFilter executorTypeFilter;
	private final IArgumentsFilter argumentsFilter;

	/**
	 * Constructor that sets all fields.
	 *
	 * @param methodNameFilter The method name filter.
	 * @param executorTypeFilter The executor type filter.
	 * @param argumentsFilter The arguments filter.
	 */
	public CommandToMethodMapper(final IMethodNameFilter methodNameFilter, final IExecutorTypeFilter executorTypeFilter,
			final IArgumentsFilter argumentsFilter) {
		this.methodNameFilter = methodNameFilter;
		this.executorTypeFilter = executorTypeFilter;
		this.argumentsFilter = argumentsFilter;
	}

	/**
	 * Constructor that initializes fields.
	 *
	 * @param mappingConfig The config of the mappers, passed to the arguments filter.
	 */
	public CommandToMethodMapper(final MappingConfig mappingConfig) {
		this(new MethodNameFilter(), new ExecutorTypeFilter(), new ArgumentsFilter(mappingConfig));
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
