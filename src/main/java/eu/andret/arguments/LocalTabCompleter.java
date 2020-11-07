/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.annotation.Argument;
import eu.andret.arguments.mapper.IMethodNameMapper;
import eu.andret.arguments.mapper.IMethodToCompletionMapper;
import eu.andret.arguments.mapper.impl.MethodNameMapper;
import eu.andret.arguments.mapper.impl.MethodToCompletionMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
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
import java.util.function.Supplier;
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
@Getter(AccessLevel.NONE)
class LocalTabCompleter implements TabCompleter {
	Class<? extends AnnotatedCommandExecutor<? extends JavaPlugin>> commandClass;
	Map<String, Supplier<Collection<String>>> completerMap = new HashMap<>();
	IMethodNameMapper methodNameMapper = new MethodNameMapper();
	IMethodToCompletionMapper methodToCompletionMapper = new MethodToCompletionMapper(completerMap);

	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
		if (args.length == 0) {
			return Collections.emptyList();
		}
		if (args.length == 1) {
			return Stream.concat(
					Stream.of(commandClass.getDeclaredMethods())
							.filter(m -> m.getAnnotation(Argument.class) != null)
							.map(Method::getName),
					Stream.of(commandClass.getDeclaredMethods())
							.filter(m -> m.getAnnotation(Argument.class) != null)
							.flatMap(m -> Stream.of(m.getDeclaredAnnotation(Argument.class).aliases())))
					.filter(s -> s.startsWith(args[0]))
					.collect(Collectors.toList());
		}

		return Arrays.stream(commandClass.getDeclaredMethods())
				.filter(m -> !Modifier.isStatic(m.getModifiers()))
				.filter(m -> methodNameMapper.mapMethodName(m, args))
				.map(m -> methodToCompletionMapper.mapCommandToCompletion(m, args))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	boolean addCompleter(String id, Supplier<Collection<String>> supplier) {
		if (completerMap.containsKey(id)) {
			return false;
		}
		completerMap.put(id, supplier);
		return true;
	}
}
