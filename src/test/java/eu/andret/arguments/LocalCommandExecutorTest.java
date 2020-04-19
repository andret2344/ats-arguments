/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.mapper.ICommandToMethodMapper;
import eu.andret.arguments.mapper.IMapper;
import eu.andret.arguments.mapper.IMethodToDescriptionMapper;
import eu.andret.arguments.mapper.impl.CommandToMethodMapper;
import eu.andret.arguments.mapper.impl.MethodToDescriptionMapper;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LocalCommandExecutorTest {
	@Test
	public void noCommandArguments() {
		// given
		CommandSender sender = mock(CommandSender.class);
		Command command = mock(Command.class);
		IMethodToDescriptionMapper mapper = mock(MethodToDescriptionMapper.class);
		LocalCommandExecutor executor = new LocalCommandExecutor(TestMethodsProvider.class, null);
		injectMapper(executor, mapper, "methodToDescriptionMapper");
		when(mapper.mapMethodToDescription(any(Method.class), anyString())).thenReturn("/test testString");
		when(command.getName()).thenReturn("test");

		// when
		executor.onCommand(sender, command, "test", new String[0]);

		// then
		verify(sender, times(17)).sendMessage(eq("/test testString"));
	}

	@Test
	@Disabled
	public void incorrectArgumentsWithoutListener() {
		// given
		CommandSender sender = mock(CommandSender.class);
		Command command = mock(Command.class);
		JavaPlugin plugin = mock(JavaPlugin.class);
		ICommandToMethodMapper mapper = new CommandToMethodMapper(new HashMap<>());
		LocalCommandExecutor executor = new LocalCommandExecutor(TestMethodsProvider.class, plugin);
		when(mapper.mapCommandToMethod(any(), any(), any())).thenReturn(Optional.empty());
		injectMapper(executor, mapper, "commandToMethodMapper");
		when(command.getName()).thenReturn("test");
		String[] args = {"testMethod"};

		// when
		boolean result = executor.onCommand(sender, command, "test", args);

		// then
		assertTrue(result);
	}

	private void injectMapper(LocalCommandExecutor executor, IMapper mapper, String mapperName) {
		try {
			Field field = executor.getClass().getDeclaredField(mapperName);
			field.setAccessible(true);
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			field.set(executor, mapper);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
