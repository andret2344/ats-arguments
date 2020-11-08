/*
 * Copyright Andret (c) 2018-2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class LocalTabCompleterTest {
	@Test
	void multipleMethodsMatch() {
		// given
		TabCompleter tabCompleter = new LocalTabCompleter(TestMethodsProvider.class);
		CommandSender sender = mock(CommandSender.class);
		Command command = mock(Command.class);

		// when
		List<String> result = tabCompleter.onTabComplete(sender, command, "", new String[]{"testMethod"});

		// then
		assertNotNull(result);
		assertEquals(20, result.size());
		assertTrue(result.containsAll(Arrays.asList(
				"testMethod",
				"testMethodWithCorrectPosition",
				"testMethodWithIntVararg",
				"testMethodWithArray",
				"testMethodWithExceededPosition",
				"testMethodWithExecutorTypeConsole",
				"testMethodWithPermission",
				"testMethodWithArgument",
				"testMethodWithVararg",
				"testMethodWithParamVarArg",
				"testMethodWithAliases",
				"testMethodWithMultipleArguments",
				"testMethodWithParam",
				"testMethodWithExecutorTypeAll",
				"testMethodWithDescription",
				"testMethodWithExecutorTypePlayer",
				"testMethodWithException",
				"testMethodDisplayedAlways",
				"testMethodDisplayedConditionally",
				"testMethodDisplayedNever"
		)));
	}

	@Test
	void aliasMatch() {
		// given
		TabCompleter tabCompleter = new LocalTabCompleter(TestMethodsProvider.class);
		CommandSender sender = mock(CommandSender.class);
		Command command = mock(Command.class);

		// when
		List<String> result = tabCompleter.onTabComplete(sender, command, "", new String[]{"testAlias"});

		// then
		assertNotNull(result);
		assertEquals(1, result.size());
		assertTrue(result.contains("testAlias1"));
	}

	@Test
	void noArgumentsNotMatch() {
		// given
		TabCompleter tabCompleter = new LocalTabCompleter(TestMethodsProvider.class);
		CommandSender sender = mock(CommandSender.class);
		Command command = mock(Command.class);

		// when
		List<String> result = tabCompleter.onTabComplete(sender, command, "", new String[]{});

		// then
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	void manyArgumentsNotMatch() {
		// given
		TabCompleter tabCompleter = new LocalTabCompleter(TestMethodsProvider.class);
		CommandSender sender = mock(CommandSender.class);
		Command command = mock(Command.class);

		// when
		List<String> result = tabCompleter.onTabComplete(sender, command, "", new String[]{"testMethod", "testAlias"});

		// then
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	void addTypeCompleter() {
		// given
		LocalTabCompleter completer = new LocalTabCompleter(TestMethodsProvider.class);

		// when
		boolean result1 = completer.addTypeCompleter(Player.class, sender -> new ArrayList<>());
		boolean result2 = completer.addTypeCompleter(Player.class, sender -> new ArrayList<>());
		boolean result3 = completer.addTypeCompleter(World.class, sender -> new ArrayList<>());

		// then
		assertTrue(result1);
		assertFalse(result2);
		assertTrue(result3);
	}

	@Test
	void addArgumentCompleter() {
		// given
		LocalTabCompleter completer = new LocalTabCompleter(TestMethodsProvider.class);

		// when
		boolean result1 = completer.addArgumentCompleter("player", sender -> new ArrayList<>());
		boolean result2 = completer.addArgumentCompleter("player", sender -> new ArrayList<>());
		boolean result3 = completer.addArgumentCompleter("world", sender -> new ArrayList<>());

		// then
		assertTrue(result1);
		assertFalse(result2);
		assertTrue(result3);
	}
}
