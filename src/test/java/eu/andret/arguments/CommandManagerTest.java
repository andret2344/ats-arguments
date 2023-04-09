/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.provider.EmptyClass;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.assertj.core.api.ThrowableAssert;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.testng.annotations.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommandManagerTest {
	@Test
	void classWithNoAnnotation() {
		// given
		final JavaPlugin javaPlugin = mock(JavaPlugin.class);

		// when
		final ThrowableAssert.ThrowingCallable callable = () -> CommandManager.registerCommand(EmptyClass.class, javaPlugin);

		// then
		assertThatThrownBy(callable).isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	void classWithWrongCommand() {
		// given
		final JavaPlugin javaPlugin = mock(JavaPlugin.class);
		when(javaPlugin.getCommand(anyString())).thenReturn(null);

		// when
		final ThrowableAssert.ThrowingCallable callable = () -> CommandManager.registerCommand(TestMethodsProvider.class, javaPlugin);

		// then
		assertThatThrownBy(callable).isInstanceOf(UnsupportedOperationException.class);
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
		verify(pluginCommand).setExecutor(any());
		verify(pluginCommand).setTabCompleter(any());
	}

	@Test
	void constructorCall() throws Throwable {
		// given
		final Constructor<CommandManager> constructor = CommandManager.class.getDeclaredConstructor();
		constructor.setAccessible(true);

		// when
		final ThrowableAssert.ThrowingCallable callable = constructor::newInstance;

		// then
		assertThatThrownBy(callable)
				.isInstanceOf(InvocationTargetException.class)
				.hasCauseExactlyInstanceOf(UnsupportedOperationException.class);
	}
}
