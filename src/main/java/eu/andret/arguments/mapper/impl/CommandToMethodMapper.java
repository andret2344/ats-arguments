/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.AnnotatedCommand;
import eu.andret.arguments.CommandTree;
import eu.andret.arguments.api.annotation.SubCommand;
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
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

	@RequiredArgsConstructor
	private static class CollectedResult {
		private final int skip;
		private final List<Method> methods;
	}

	/**
	 * Constructor that initializes fields.
	 *
	 * @param mappingConfig The map of the {@link String}-{@link MappingSet} pair.
	 */
	public CommandToMethodMapper(@NotNull final MappingConfig mappingConfig) {
		this(mappingConfig, new MethodNameFilter(), new ExecutorTypeFilter(), new ArgumentsFilter(mappingConfig));
	}

	@Override
	public Optional<Method> mapCommandToMethod(@NotNull final CommandTree<? extends JavaPlugin> commandTree,
											   @NotNull final String[] command,
											   @NotNull final CommandSender sender,
											   @NotNull final AnnotatedCommand.Options options) {
		final CollectedResult collectedResult = gatherMethods(commandTree, command, options);
		return collectedResult.methods
				.stream()
				.filter(method -> executorTypeFilter.filterExecutorType(method, sender))
				.filter(method -> argumentsFilter.filterArguments(method, command, collectedResult.skip))
				.findFirst();
	}

	@NotNull
	private CollectedResult gatherMethods(@NotNull final CommandTree<? extends JavaPlugin> commandTree,
										  @NotNull final String[] command,
										  @NotNull final AnnotatedCommand.Options options) {
		final List<Method> result = new ArrayList<>();
		CommandTree<? extends JavaPlugin>.Node node = commandTree.getRoot();
		int arg = 0;
		while (node != null) {
			if (arg >= command.length) {
				break;
			}
			final String name = command[arg];
			final int requiredCount = command.length - arg - 1;
			Arrays.stream(node.getClazz().getDeclaredMethods())
					.filter(method -> methodNameFilter.filterMethodName(method, name, options))
					.filter(method -> method.getParameterCount() == requiredCount)
					.forEach(result::add);
			node = node.getChildren()
					.stream()
					.filter(child -> getValue(child).equals(name))
					.findFirst()
					.orElse(null);
			arg++;
		}
		return new CollectedResult(arg, result);
	}

	@NotNull
	private String getValue(@NotNull final CommandTree<? extends JavaPlugin>.Node node) {
		return node.getClazz().getAnnotation(SubCommand.class).value();
	}
}
