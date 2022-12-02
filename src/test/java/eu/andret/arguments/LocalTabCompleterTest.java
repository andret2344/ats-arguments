/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LocalTabCompleterTest {
	private static class TestCommand extends AnnotatedCommand<JavaPlugin> {
		TestCommand(final PluginCommand command) {
			super(command);
		}
	}

	@Test
	void multipleMethodsMatch() {
		// given
		final TestCommand testCommand = mock(TestCommand.class);
		final TabCompleter tabCompleter = new LocalTabCompleter<>(testCommand, TestMethodsProvider.class);
		final CommandSender sender = mock(CommandSender.class);
		final Command command = mock(Command.class);
		final List<String> providerMethodNames = Arrays.stream(TestMethodsProvider.class.getDeclaredMethods())
				.filter(method -> method.isAnnotationPresent(Argument.class))
				.filter(method -> !Modifier.isStatic(method.getModifiers()))
				.map(Method::getName)
				.collect(Collectors.toList());

		// when
		final List<String> result = tabCompleter.onTabComplete(sender, command, "", new String[]{"testMethod"});

		// then
		assertThat(result).isNotNull()
				.hasSize(38)
				.containsExactlyElementsOf(providerMethodNames);
	}

	@Test
	void aliasMatch() {
		// given
		final TestCommand testCommand = mock(TestCommand.class);
		final TabCompleter tabCompleter = new LocalTabCompleter<>(testCommand, TestMethodsProvider.class);
		final CommandSender sender = mock(CommandSender.class);
		final Command command = mock(Command.class);

		// when
		final List<String> result = tabCompleter.onTabComplete(sender, command, "", new String[]{"testAlias"});

		// then
		assertThat(result).containsExactly("testAlias1");
	}

	@Test
	void noArgumentsNotMatch() {
		// given
		final TestCommand testCommand = mock(TestCommand.class);
		final TabCompleter tabCompleter = new LocalTabCompleter<>(testCommand, TestMethodsProvider.class);
		final CommandSender sender = mock(CommandSender.class);
		final Command command = mock(Command.class);

		// when
		final List<String> result = tabCompleter.onTabComplete(sender, command, "", new String[]{});

		// then
		assertThat(result).isEmpty();
	}

	@Test
	void manyArgumentsNotMatch() {
		// given
		final TestCommand testCommand = mock(TestCommand.class);
		final AnnotatedCommand.Options options = new AnnotatedCommand.Options();
		when(testCommand.getOptions()).thenReturn(options);
		final TabCompleter tabCompleter = new LocalTabCompleter<>(testCommand, TestMethodsProvider.class);
		final CommandSender sender = mock(CommandSender.class);
		final Command command = mock(Command.class);

		// when
		final List<String> result = tabCompleter.onTabComplete(sender, command, "", new String[]{"testMethod", "testAlias"});

		// then
		assertThat(result).isEmpty();
	}

	@Test
	void addTypeCompleter() {
		// given
		final TestCommand testCommand = mock(TestCommand.class);
		final LocalTabCompleter<JavaPlugin> completer = new LocalTabCompleter<>(testCommand, TestMethodsProvider.class);

		// when
		final boolean result1 = completer.addTypeCompleter(Player.class, (sender, collection) -> new ArrayList<>());
		final boolean result2 = completer.addTypeCompleter(Player.class, (sender, collection) -> new ArrayList<>());
		final boolean result3 = completer.addTypeCompleter(World.class, (sender, collection) -> new ArrayList<>());

		// then
		assertThat(result1).isTrue();
		assertThat(result2).isFalse();
		assertThat(result3).isTrue();
	}

	@Test
	void addArgumentCompleter() {
		// given
		final TestCommand testCommand = mock(TestCommand.class);
		final LocalTabCompleter<JavaPlugin> completer = new LocalTabCompleter<>(testCommand, TestMethodsProvider.class);

		// when
		final boolean result1 = completer.addArgumentCompleter("player", (sender, collection) -> new ArrayList<>());
		final boolean result2 = completer.addArgumentCompleter("player", (sender, collection) -> new ArrayList<>());
		final boolean result3 = completer.addArgumentCompleter("world", (sender, collection) -> new ArrayList<>());

		// then
		assertThat(result1).isTrue();
		assertThat(result2).isFalse();
		assertThat(result3).isTrue();
	}
}
