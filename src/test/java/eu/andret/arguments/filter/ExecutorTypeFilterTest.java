/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.filter;

import eu.andret.arguments.filter.impl.ExecutorTypeFilter;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ExecutorTypeFilterTest {

	@Test
	void methodWithExecutorTypePlayerCalledByPlayer() throws NoSuchMethodException {
		// given
		final CommandSender commandSender = mock(Player.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithExecutorTypePlayer");
		final IExecutorTypeFilter mapper = new ExecutorTypeFilter();

		// when
		final boolean result = mapper.filterExecutorType(method, commandSender);

		// then
		assertTrue(result);
	}

	@Test
	void methodWithExecutorTypePlayerCalledByConsole() throws NoSuchMethodException {
		// given
		final CommandSender commandSender = mock(ConsoleCommandSender.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithExecutorTypePlayer");
		final IExecutorTypeFilter mapper = new ExecutorTypeFilter();

		// when
		final boolean result = mapper.filterExecutorType(method, commandSender);

		// then
		assertFalse(result);
	}

	@Test
	void methodWithExecutorTypeConsoleCalledByPlayer() throws NoSuchMethodException {
		// given
		final CommandSender commandSender = mock(Player.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithExecutorTypeConsole");
		final IExecutorTypeFilter mapper = new ExecutorTypeFilter();

		// when
		final boolean result = mapper.filterExecutorType(method, commandSender);

		// then
		assertFalse(result);
	}

	@Test
	void methodWithExecutorTypeConsoleCalledByConsole() throws NoSuchMethodException {
		// given
		final CommandSender commandSender = mock(ConsoleCommandSender.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithExecutorTypeConsole");
		final IExecutorTypeFilter mapper = new ExecutorTypeFilter();

		// when
		final boolean result = mapper.filterExecutorType(method, commandSender);

		// then
		assertTrue(result);
	}

	@Test
	void methodWithExecutorTypeAllCalledByPlayer() throws NoSuchMethodException {
		// given
		final CommandSender commandSender = mock(Player.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithExecutorTypeAll");
		final IExecutorTypeFilter mapper = new ExecutorTypeFilter();

		// when
		final boolean result = mapper.filterExecutorType(method, commandSender);

		// then
		assertTrue(result);
	}

	@Test
	void methodWithExecutorTypeAllCalledByConsole() throws NoSuchMethodException {
		// given
		final CommandSender commandSender = mock(ConsoleCommandSender.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithExecutorTypeAll");
		final IExecutorTypeFilter mapper = new ExecutorTypeFilter();

		// when
		final boolean result = mapper.filterExecutorType(method, commandSender);

		// then
		assertTrue(result);
	}
}
