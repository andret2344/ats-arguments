/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.mapper.impl.DisplayTypeMapper;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DisplayTypeMapperTest {
	@Mock
	private IPermissionMapper permissionMapper;

	@InjectMocks
	private DisplayTypeMapper mapper;

	@BeforeEach
	public void setup() {
		Mockito.mockitoSession()
				.initMocks(this)
				.startMocking()
				.finishMocking();
	}


	@Test
	public void methodAlwaysDisplayed() throws NoSuchMethodException {
		// given
		CommandSender commandSender = mock(ConsoleCommandSender.class);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodDisplayedAlways");

		// when
		boolean result = mapper.mapDisplayType(method, commandSender);

		// then
		assertTrue(result);
	}

	@Test
	public void methodDisplayedIfPerms() throws NoSuchMethodException {
		// given
		CommandSender commandSender = mock(ConsoleCommandSender.class);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodDisplayedConditionally");
		when(permissionMapper.mapPermission(method, commandSender)).thenReturn(true);

		// when
		boolean result = mapper.mapDisplayType(method, commandSender);

		// then
		assertTrue(result);
	}

	@Test
	public void methodDisplayedIfNoPerms() throws NoSuchMethodException {
		// given
		CommandSender commandSender = mock(ConsoleCommandSender.class);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodDisplayedConditionally");
		when(permissionMapper.mapPermission(method, commandSender)).thenReturn(false);

		// when
		boolean result = mapper.mapDisplayType(method, commandSender);

		// then
		assertFalse(result);
	}

	@Test
	public void methodDisplayedNever() throws NoSuchMethodException {
		// given
		CommandSender commandSender = mock(ConsoleCommandSender.class);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodDisplayedNever");

		// when
		boolean result = mapper.mapDisplayType(method, commandSender);

		// then
		assertFalse(result);
	}
}
