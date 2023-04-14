/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.filter.IMethodNameFilter;
import eu.andret.arguments.filter.impl.MethodNameFilter;
import eu.andret.arguments.mapper.IMethodToCompletionMapper;
import eu.andret.arguments.mapper.impl.MethodToCompletionMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Local tab completer, allowing customization of tab completion
 *
 * @author Andret
 * @since Jun 02, 2019
 */
@Value
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NonFinal
@Getter(AccessLevel.NONE)
class LocalTabCompleter<E extends JavaPlugin> implements TabCompleter {
	AnnotatedCommand<E> annotatedCommand;
	Class<? extends AnnotatedCommandExecutor<? extends JavaPlugin>> commandClass;
	Map<Class<?>, BiFunction<List<String>, CommandSender, Collection<String>>> typeCompleterMap = new HashMap<>();
	Map<String, BiFunction<List<String>, CommandSender, Collection<String>>> argumentCompleterMap = new HashMap<>();
	IMethodNameFilter methodNameMapper = new MethodNameFilter();
	IMethodToCompletionMapper methodToCompletionMapper = new MethodToCompletionMapper(typeCompleterMap, argumentCompleterMap);

	@Override
	public List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command,
									  @NotNull final String label, final String[] args) {
		if (args.length == 0) {
			return Collections.emptyList();
		}
		if (args.length == 1) {
			return Stream.concat(
							Stream.of(commandClass.getDeclaredMethods())
									.filter(method -> method.getAnnotation(Argument.class) != null)
									.map(Method::getName),
							Stream.of(commandClass.getDeclaredMethods())
									.filter(method -> method.getAnnotation(Argument.class) != null)
									.flatMap(method -> Stream.of(method.getDeclaredAnnotation(Argument.class).aliases())))
					.filter(text -> text.startsWith(args[0]))
					.collect(Collectors.toList());
		}

		return Arrays.stream(commandClass.getDeclaredMethods())
				.filter(method -> !Modifier.isStatic(method.getModifiers()))
				.filter(method -> methodNameMapper.filterMethodName(method, args[0], annotatedCommand.getOptions()))
				.map(method -> methodToCompletionMapper.mapCommandToCompletion(method, args, sender))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	boolean addArgumentCompleter(final String id, final BiFunction<List<String>, CommandSender, Collection<String>> function) {
		if (argumentCompleterMap.containsKey(id)) {
			return false;
		}
		argumentCompleterMap.put(id, function);
		return true;
	}

	boolean addTypeCompleter(final Class<?> clazz, final BiFunction<List<String>, CommandSender, Collection<String>> function) {
		if (typeCompleterMap.containsKey(clazz)) {
			return false;
		}
		typeCompleterMap.put(clazz, function);
		return true;
	}
}
