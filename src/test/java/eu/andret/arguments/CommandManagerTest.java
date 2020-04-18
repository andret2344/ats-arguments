/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */
package eu.andret.arguments;

import eu.andret.arguments.provider.EmptyClass;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommandManagerTest {
	@Test(expected = UnsupportedOperationException.class)
	public void classWithNoAnnotation() {
		// given
		JavaPlugin javaPlugin = mock(JavaPlugin.class);

		// when
		CommandManager.registerCommand(EmptyClass.class, javaPlugin);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void classWithWrongCommand() {
		// given
		JavaPlugin javaPlugin = mock(JavaPlugin.class);
		when(javaPlugin.getCommand(anyString())).thenReturn(null);

		// when
		CommandManager.registerCommand(TestMethodsProvider.class, javaPlugin);
	}

	@Test
	public void classWithCorrectCommand() {
		// given
		JavaPlugin javaPlugin = mock(JavaPlugin.class);
		PluginCommand pluginCommand = mock(PluginCommand.class);
		when(javaPlugin.getCommand(anyString())).thenReturn(pluginCommand);

		// when
		CommandManager.registerCommand(TestMethodsProvider.class, javaPlugin);

		// then
		verify(pluginCommand, times(1)).setExecutor(any());
		verify(pluginCommand, times(1)).setTabCompleter(any());
	}
}
