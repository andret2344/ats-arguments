/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.filter;

import eu.andret.arguments.filter.impl.PermissionFilter;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PermissionFilterTest {
	@Test
	void methodWithPermissionCalledByConsole() throws NoSuchMethodException {
		// given
		final CommandSender commandSender = mock(ConsoleCommandSender.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithPermission");
		final IPermissionFilter mapper = new PermissionFilter();

		// when
		final boolean result = mapper.filterPermission(method, commandSender);

		// then
		assertThat(result).isTrue();
	}

	@Test
	void methodWithPermissionCalledByOpPlayer() throws NoSuchMethodException {
		// given
		final CommandSender commandSender = mock(Player.class);
		when(commandSender.isOp()).thenReturn(true);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithPermission");
		final IPermissionFilter mapper = new PermissionFilter();

		// when
		final boolean result = mapper.filterPermission(method, commandSender);

		// then
		assertThat(result).isTrue();
	}

	@Test
	void methodWithPermissionCalledByPlayerWithPermission() throws NoSuchMethodException {
		// given
		final CommandSender commandSender = mock(Player.class);
		when(commandSender.hasPermission("ats.test.method")).thenReturn(true);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithPermission");
		final IPermissionFilter mapper = new PermissionFilter();

		// when
		final boolean result = mapper.filterPermission(method, commandSender);

		// then
		assertThat(result).isTrue();
	}

	@Test
	void methodWithPermissionCalledByPlayerWithoutPermission() throws NoSuchMethodException {
		// given
		final CommandSender commandSender = mock(Player.class);
		when(commandSender.hasPermission("ats.test.method")).thenReturn(false);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithPermission");
		final IPermissionFilter mapper = new PermissionFilter();

		// when
		final boolean result = mapper.filterPermission(method, commandSender);

		// then
		assertThat(result).isFalse();
	}

	@Test
	void methodWithoutPermissionCalledByConsole() throws NoSuchMethodException {
		// given
		final CommandSender commandSender = mock(ConsoleCommandSender.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethod");
		final IPermissionFilter mapper = new PermissionFilter();

		// when
		final boolean result = mapper.filterPermission(method, commandSender);

		// then
		assertThat(result).isTrue();
	}

	@Test
	void methodWithoutPermissionCalledByOpPlayer() throws NoSuchMethodException {
		// given
		final CommandSender commandSender = mock(Player.class);
		when(commandSender.isOp()).thenReturn(true);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethod");
		final IPermissionFilter mapper = new PermissionFilter();

		// when
		final boolean result = mapper.filterPermission(method, commandSender);

		// then
		assertThat(result).isTrue();
	}

	@Test
	void methodWithoutPermissionCalled() throws NoSuchMethodException {
		// given
		final CommandSender commandSender = mock(Player.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethod");
		final IPermissionFilter mapper = new PermissionFilter();

		// when
		final boolean result = mapper.filterPermission(method, commandSender);

		// then
		assertThat(result).isTrue();
	}
}
