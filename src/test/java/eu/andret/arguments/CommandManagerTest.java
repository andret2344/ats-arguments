/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.provider.EmptyClass;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommandManagerTest {
	@Test
	public void classWithNoAnnotation() {
		// given
		JavaPlugin javaPlugin = mock(JavaPlugin.class);

		// when
		Executable result = () -> CommandManager.registerCommand(EmptyClass.class, javaPlugin);

		// then
		assertThrows(UnsupportedOperationException.class, result);
	}

	@Test
	public void classWithWrongCommand() {
		// given
		JavaPlugin javaPlugin = mock(JavaPlugin.class);
		when(javaPlugin.getCommand(anyString())).thenReturn(null);

		// when
		Executable result = () -> CommandManager.registerCommand(TestMethodsProvider.class, javaPlugin);

		// then
		assertThrows(UnsupportedOperationException.class, result);
	}

	@Test
	public void classWithCorrectCommand() {
		// given
		JavaPlugin javaPlugin = mock(JavaPlugin.class);
		PluginCommand pluginCommand = mock(PluginCommand.class);
		LocalCommandExecutor executor = mock(LocalCommandExecutor.class);
		when(pluginCommand.getExecutor()).thenReturn(executor);
		when(javaPlugin.getCommand(anyString())).thenReturn(pluginCommand);

		// when
		CommandManager.registerCommand(TestMethodsProvider.class, javaPlugin);

		// then
		verify(pluginCommand, times(1)).setExecutor(any());
		verify(pluginCommand, times(1)).setTabCompleter(any());
	}

	@Test
	public void constructorCall() throws Throwable {
		// given
		Constructor<CommandManager> constructor = CommandManager.class.getDeclaredConstructor();
		constructor.setAccessible(true);

		// when
		Executable result = () -> {
			try {
				constructor.newInstance();
			} catch (InvocationTargetException ex) {
				throw ex.getTargetException();
			}
		};
		assertThrows(UnsupportedOperationException.class, result);
	}
}
