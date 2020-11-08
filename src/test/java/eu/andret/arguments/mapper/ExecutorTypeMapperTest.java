/*
 * Copyright Andret (c) 2018=2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.mapper.impl.ExecutorTypeMapper;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ExecutorTypeMapperTest {
	private final IExecutorTypeMapper mapper = new ExecutorTypeMapper();

	@Test
	void methodWithExecutorTypePlayerCalledByPlayer() throws NoSuchMethodException {
		// given
		CommandSender commandSender = mock(Player.class);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithExecutorTypePlayer");

		// when
		boolean result = mapper.mapExecutorType(method, commandSender);

		// then
		assertTrue(result);
	}

	@Test
	void methodWithExecutorTypePlayerCalledByConsole() throws NoSuchMethodException {
		// given
		CommandSender commandSender = mock(ConsoleCommandSender.class);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithExecutorTypePlayer");

		// when
		boolean result = mapper.mapExecutorType(method, commandSender);

		// then
		assertFalse(result);
	}

	@Test
	void methodWithExecutorTypeConsoleCalledByPlayer() throws NoSuchMethodException {
		// given
		CommandSender commandSender = mock(Player.class);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithExecutorTypeConsole");

		// when
		boolean result = mapper.mapExecutorType(method, commandSender);

		// then
		assertFalse(result);
	}

	@Test
	void methodWithExecutorTypeConsoleCalledByConsole() throws NoSuchMethodException {
		// given
		CommandSender commandSender = mock(ConsoleCommandSender.class);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithExecutorTypeConsole");

		// when
		boolean result = mapper.mapExecutorType(method, commandSender);

		// then
		assertTrue(result);
	}

	@Test
	void methodWithExecutorTypeAllCalledByPlayer() throws NoSuchMethodException {
		// given
		CommandSender commandSender = mock(Player.class);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithExecutorTypeAll");

		// when
		boolean result = mapper.mapExecutorType(method, commandSender);

		// then
		assertTrue(result);
	}

	@Test
	void methodWithExecutorTypeAllCalledByConsole() throws NoSuchMethodException {
		// given
		CommandSender commandSender = mock(ConsoleCommandSender.class);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithExecutorTypeAll");

		// when
		boolean result = mapper.mapExecutorType(method, commandSender);

		// then
		assertTrue(result);
	}
}
