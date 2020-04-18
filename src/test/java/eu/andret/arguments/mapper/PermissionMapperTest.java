/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */
package eu.andret.arguments.mapper;

import eu.andret.arguments.mapper.impl.PermissionMapper;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Method;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PermissionMapperTest {
	private final IPermissionMapper mapper = new PermissionMapper();

	@Test
	public void methodWithPermissionCalledByConsole() throws NoSuchMethodException {
		// given
		CommandSender commandSender = mock(ConsoleCommandSender.class);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithPermission");

		// when
		boolean result = mapper.mapPermission(method, commandSender);

		// then
		assertTrue(result);
	}

	@Test
	public void methodWithPermissionCalledByOpPlayer() throws NoSuchMethodException {
		// given
		CommandSender commandSender = mock(Player.class);
		when(commandSender.isOp()).thenReturn(true);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithPermission");

		// when
		boolean result = mapper.mapPermission(method, commandSender);

		// then
		assertTrue(result);
	}

	@Test
	public void methodWithPermissionCalledByPlayerWithPermission() throws NoSuchMethodException {
		// given
		CommandSender commandSender = mock(Player.class);
		when(commandSender.hasPermission("ats.test.method")).thenReturn(true);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithPermission");

		// when
		boolean result = mapper.mapPermission(method, commandSender);

		// then
		assertTrue(result);
	}

	@Test
	public void methodWithPermissionCalledByPlayerWithoutPermission() throws NoSuchMethodException {
		// given
		CommandSender commandSender = mock(Player.class);
		when(commandSender.hasPermission("ats.test.method")).thenReturn(false);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithPermission");

		// when
		boolean result = mapper.mapPermission(method, commandSender);

		// then
		assertFalse(result);
	}

	@Test
	public void methodWithoutPermissionCalledByConsole() throws NoSuchMethodException {
		// given
		CommandSender commandSender = mock(ConsoleCommandSender.class);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethod");

		// when
		boolean result = mapper.mapPermission(method, commandSender);

		// then
		assertTrue(result);
	}

	@Test
	public void methodWithoutPermissionCalledByOpPlayer() throws NoSuchMethodException {
		// given
		CommandSender commandSender = mock(Player.class);
		when(commandSender.isOp()).thenReturn(true);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethod");

		// when
		boolean result = mapper.mapPermission(method, commandSender);

		// then
		assertTrue(result);
	}

	@Test
	public void methodWithoutPermissionCalledByPlayerWithPermission() throws NoSuchMethodException {
		// given
		CommandSender commandSender = mock(Player.class);
		when(commandSender.hasPermission("ats.test.method")).thenReturn(true);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethod");

		// when
		boolean result = mapper.mapPermission(method, commandSender);

		// then
		assertTrue(result);
	}

	@Test
	public void methodWithoutPermissionCalledByPlayerWithoutPermission() throws NoSuchMethodException {
		// given
		CommandSender commandSender = mock(Player.class);
		when(commandSender.hasPermission("ats.test.method")).thenReturn(false);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethod");

		// when
		boolean result = mapper.mapPermission(method, commandSender);

		// then
		assertTrue(result);
	}
}
