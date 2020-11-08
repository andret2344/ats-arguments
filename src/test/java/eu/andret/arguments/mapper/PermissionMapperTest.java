/*
 * Copyright Andret (c) 2018-2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.mapper.impl.PermissionMapper;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PermissionMapperTest {
	@Test
	void methodWithPermissionCalledByConsole() throws NoSuchMethodException {
		// given
		CommandSender commandSender = mock(ConsoleCommandSender.class);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithPermission");
		IPermissionMapper mapper = new PermissionMapper();

		// when
		boolean result = mapper.mapPermission(method, commandSender);

		// then
		assertTrue(result);
	}

	@Test
	void methodWithPermissionCalledByOpPlayer() throws NoSuchMethodException {
		// given
		CommandSender commandSender = mock(Player.class);
		when(commandSender.isOp()).thenReturn(true);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithPermission");
		IPermissionMapper mapper = new PermissionMapper();

		// when
		boolean result = mapper.mapPermission(method, commandSender);

		// then
		assertTrue(result);
	}

	@Test
	void methodWithPermissionCalledByPlayerWithPermission() throws NoSuchMethodException {
		// given
		CommandSender commandSender = mock(Player.class);
		when(commandSender.hasPermission("ats.test.method")).thenReturn(true);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithPermission");
		IPermissionMapper mapper = new PermissionMapper();

		// when
		boolean result = mapper.mapPermission(method, commandSender);

		// then
		assertTrue(result);
	}

	@Test
	void methodWithPermissionCalledByPlayerWithoutPermission() throws NoSuchMethodException {
		// given
		CommandSender commandSender = mock(Player.class);
		when(commandSender.hasPermission("ats.test.method")).thenReturn(false);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithPermission");
		IPermissionMapper mapper = new PermissionMapper();

		// when
		boolean result = mapper.mapPermission(method, commandSender);

		// then
		assertFalse(result);
	}

	@Test
	void methodWithoutPermissionCalledByConsole() throws NoSuchMethodException {
		// given
		CommandSender commandSender = mock(ConsoleCommandSender.class);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethod");
		IPermissionMapper mapper = new PermissionMapper();

		// when
		boolean result = mapper.mapPermission(method, commandSender);

		// then
		assertTrue(result);
	}

	@Test
	void methodWithoutPermissionCalledByOpPlayer() throws NoSuchMethodException {
		// given
		CommandSender commandSender = mock(Player.class);
		when(commandSender.isOp()).thenReturn(true);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethod");
		IPermissionMapper mapper = new PermissionMapper();

		// when
		boolean result = mapper.mapPermission(method, commandSender);

		// then
		assertTrue(result);
	}

	@Test
	void methodWithoutPermissionCalled() throws NoSuchMethodException {
		// given
		CommandSender commandSender = mock(Player.class);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethod");
		IPermissionMapper mapper = new PermissionMapper();

		// when
		boolean result = mapper.mapPermission(method, commandSender);

		// then
		assertTrue(result);
	}
}
