/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.annotation.Argument;
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
import java.util.Collections;
import java.util.List;
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
	Class<? extends AnnotatedCommandExecutor<? extends JavaPlugin>> commandExecutorClass;

	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
		if (args.length != 1) {
			return Collections.emptyList();
		}
		return Stream.concat(
				Stream.of(commandExecutorClass.getDeclaredMethods())
						.filter(m -> m.getAnnotation(Argument.class) != null)
						.map(Method::getName),
				Stream.of(commandExecutorClass.getDeclaredMethods())
						.filter(m -> m.getAnnotation(Argument.class) != null)
						.flatMap(m -> Stream.of(m.getDeclaredAnnotation(Argument.class).aliases())))
				.filter(s -> s.startsWith(args[0]))
				.collect(Collectors.toList());
	}
}
