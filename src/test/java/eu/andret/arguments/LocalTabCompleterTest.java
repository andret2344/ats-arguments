/*
 * Copyright Andret (c) 2018=2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
	void toStringNotNull() {
		// given
		TabCompleter tabCompleter = new LocalTabCompleter(TestMethodsProvider.class);
		CommandSender sender = mock(CommandSender.class);
		Command command = mock(Command.class);

		// when
		String result = tabCompleter.toString();

		// then
		assertNotNull(result);
	}
}
