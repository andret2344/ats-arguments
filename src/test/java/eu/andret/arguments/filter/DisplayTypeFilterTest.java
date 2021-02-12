/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.filter;

import eu.andret.arguments.filter.impl.DisplayTypeFilter;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DisplayTypeFilterTest {

	@Test
	void methodAlwaysDisplayed() throws NoSuchMethodException {
		// given
		final CommandSender commandSender = mock(ConsoleCommandSender.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodDisplayedAlways");
		final IPermissionFilter permissionMapper = mock(IPermissionFilter.class);
		final IDisplayTypeFilter mapper = new DisplayTypeFilter(permissionMapper);

		// when
		final boolean result = mapper.mapDisplayType(method, commandSender);

		// then
		assertTrue(result);
	}

	@Test
	void methodDisplayedIfPerms() throws NoSuchMethodException {
		// given
		final CommandSender commandSender = mock(ConsoleCommandSender.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodDisplayedConditionally");
		final IPermissionFilter permissionMapper = mock(IPermissionFilter.class);
		final IDisplayTypeFilter mapper = new DisplayTypeFilter(permissionMapper);
		when(permissionMapper.filterPermission(method, commandSender)).thenReturn(true);

		// when
		final boolean result = mapper.mapDisplayType(method, commandSender);

		// then
		assertTrue(result);
	}

	@Test
	void methodDisplayedIfNoPerms() throws NoSuchMethodException {
		// given
		final CommandSender commandSender = mock(ConsoleCommandSender.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodDisplayedConditionally");
		final IPermissionFilter permissionMapper = mock(IPermissionFilter.class);
		final IDisplayTypeFilter mapper = new DisplayTypeFilter(permissionMapper);
		when(permissionMapper.filterPermission(method, commandSender)).thenReturn(false);

		// when
		final boolean result = mapper.mapDisplayType(method, commandSender);

		// then
		assertFalse(result);
	}

	@Test
	void methodDisplayedNever() throws NoSuchMethodException {
		// given
		final CommandSender commandSender = mock(ConsoleCommandSender.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodDisplayedNever");
		final IPermissionFilter permissionMapper = mock(IPermissionFilter.class);
		final IDisplayTypeFilter mapper = new DisplayTypeFilter(permissionMapper);

		// when
		final boolean result = mapper.mapDisplayType(method, commandSender);

		// then
		assertFalse(result);
	}
}
