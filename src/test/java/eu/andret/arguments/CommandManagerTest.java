/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
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

class CommandManagerTest {
	@Test
	void classWithNoAnnotation() {
		// given
		final JavaPlugin javaPlugin = mock(JavaPlugin.class);

		// when
		final Executable result = () -> CommandManager.registerCommand(EmptyClass.class, javaPlugin);

		// then
		assertThrows(UnsupportedOperationException.class, result);
	}

	@Test
	void classWithWrongCommand() {
		// given
		final JavaPlugin javaPlugin = mock(JavaPlugin.class);
		when(javaPlugin.getCommand(anyString())).thenReturn(null);

		// when
		final Executable result = () -> CommandManager.registerCommand(TestMethodsProvider.class, javaPlugin);

		// then
		assertThrows(UnsupportedOperationException.class, result);
	}

	@Test
	void classWithCorrectCommand() {
		// given
		final JavaPlugin javaPlugin = mock(JavaPlugin.class);
		final PluginCommand pluginCommand = mock(PluginCommand.class);
		class TestCommandExecutor extends LocalCommandExecutor<JavaPlugin> {
			TestCommandExecutor(final AnnotatedCommand<JavaPlugin> annotatedCommand, final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass, final JavaPlugin plugin, final Object... parameters) {
				super(annotatedCommand, commandClass, plugin, parameters);
			}
		}
		final TestCommandExecutor executor = mock(TestCommandExecutor.class);
		when(pluginCommand.getExecutor()).thenReturn(executor);
		when(javaPlugin.getCommand(anyString())).thenReturn(pluginCommand);

		// when
		CommandManager.registerCommand(TestMethodsProvider.class, javaPlugin);

		// then
		verify(pluginCommand, times(1)).setExecutor(any());
		verify(pluginCommand, times(1)).setTabCompleter(any());
	}

	@Test
	void constructorCall() throws Throwable {
		// given
		final Constructor<CommandManager> constructor = CommandManager.class.getDeclaredConstructor();
		constructor.setAccessible(true);

		// when
		final Executable result = () -> {
			try {
				constructor.newInstance();
			} catch (final InvocationTargetException ex) {
				throw ex.getTargetException();
			}
		};
		assertThrows(UnsupportedOperationException.class, result);
	}
}
