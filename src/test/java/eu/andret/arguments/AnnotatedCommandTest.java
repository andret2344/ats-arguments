/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AnnotatedCommandTest {
	private static class TestCommandExecutor extends LocalCommandExecutor<JavaPlugin> {
		TestCommandExecutor(final AnnotatedCommand<JavaPlugin> annotatedCommand, final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass, final JavaPlugin plugin, final Object... parameters) {
			super(annotatedCommand, commandClass, plugin, parameters);
		}
	}

	private static class TestTabCompleter extends LocalTabCompleter<JavaPlugin> {
		TestTabCompleter(final AnnotatedCommand<JavaPlugin> annotatedCommand, final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass) {
			super(annotatedCommand, commandClass);
		}
	}

	@Test
	void correctCommandReturned() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestCommandExecutor executor = mock(TestCommandExecutor.class);
		when(command.getExecutor()).thenReturn(executor);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);

		// when
		final PluginCommand result = annotatedCommand.getCommand();

		// then
		assertSame(command, result);
	}

	@Test
	void correctListenersSetup() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestCommandExecutor executor = mock(TestCommandExecutor.class);
		when(command.getExecutor()).thenReturn(executor);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);

		// when
		annotatedCommand.setOnUnknownSubCommandExecutionListener(sender -> {
		});
		annotatedCommand.setOnInsufficientPermissionsListener(sender -> {
		});

		// then
		verify(executor, times(1)).setOnUnknownSubCommandExecutionListener(any());
		verify(executor, times(1)).setOnInsufficientPermissionsListener(any());
	}

	@Test
	void correctExecutorReturned() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestCommandExecutor executor = mock(TestCommandExecutor.class);
		when(command.getExecutor()).thenReturn(executor);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);

		// when
		final CommandExecutor result = annotatedCommand.getCommand().getExecutor();

		// then
		assertSame(executor, result);
	}

	@Test
	void correctCompleterReturned() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final TestTabCompleter completer = mock(TestTabCompleter.class);
		when(command.getTabCompleter()).thenReturn(completer);

		// when
		final TabCompleter result = annotatedCommand.getCommand().getTabCompleter();

		// then
		assertSame(completer, result);
	}

	@Test
	void correctAddArgumentMapper() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestCommandExecutor executor = mock(TestCommandExecutor.class);
		when(command.getExecutor()).thenReturn(executor);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		when(executor.addArgumentMapper(eq("test"), any())).thenReturn(true);

		// when
		annotatedCommand.addArgumentMapper("test", int.class, Integer::parseInt);

		// then
		verify(executor, times(1)).addArgumentMapper(eq("test"), any());
	}

	@Test
	void correctAddTypeMapper() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestCommandExecutor executor = mock(TestCommandExecutor.class);
		when(command.getExecutor()).thenReturn(executor);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		when(executor.addTypeMapper(eq(boolean.class), any())).thenReturn(true);

		// when
		annotatedCommand.addTypeMapper(boolean.class, Boolean::parseBoolean);

		// then
		verify(executor, times(1)).addTypeMapper(eq(boolean.class), any());
	}

	@Test
	void incorrectAddArgumentMapper() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestCommandExecutor executor = mock(TestCommandExecutor.class);
		when(command.getExecutor()).thenReturn(executor);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		when(executor.addArgumentMapper(eq("test"), any())).thenReturn(false);

		// when
		final Executable result = () -> annotatedCommand.addArgumentMapper("test", int.class, Integer::parseInt);

		// then
		assertThrows(IllegalArgumentException.class, result);
		verify(executor, times(1)).addArgumentMapper(eq("test"), any());
	}

	@Test
	void incorrectAddTypeMapper() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestCommandExecutor executor = mock(TestCommandExecutor.class);
		when(command.getExecutor()).thenReturn(executor);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		when(executor.addTypeMapper(eq(boolean.class), any())).thenReturn(false);

		// when
		final Executable result = () -> annotatedCommand.addTypeMapper(boolean.class, Boolean::parseBoolean);

		// then
		assertThrows(IllegalArgumentException.class, result);
		verify(executor, times(1)).addTypeMapper(eq(boolean.class), any());
	}

	@Test
	void correctAddTypeCompleter() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestTabCompleter completer = mock(TestTabCompleter.class);
		when(command.getTabCompleter()).thenReturn(completer);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		when(completer.addTypeCompleter(any(Class.class), any())).thenReturn(true);
		final ArrayList<String> list = new ArrayList<>();
		list.add("one");
		list.add("two");

		// when
		annotatedCommand.addTypeCompleter(World.class, list);

		// then
		verify(completer, times(1))
				.addTypeCompleter(eq(World.class), argThat(function -> function.apply(null).equals(list)));
	}

	@Test
	void incorrectAddTypeCompleter() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestTabCompleter completer = mock(TestTabCompleter.class);
		when(command.getTabCompleter()).thenReturn(completer);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		when(completer.addTypeCompleter(any(Class.class), any())).thenReturn(false);

		// when
		final Executable result = () -> annotatedCommand.addTypeCompleter(World.class, new ArrayList<>());

		// then
		assertThrows(IllegalArgumentException.class, result);
		verify(completer, times(1)).addTypeCompleter(eq(World.class), any());
	}

	@Test
	void correctAddArgumentCompleter() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestTabCompleter completer = mock(TestTabCompleter.class);
		when(command.getTabCompleter()).thenReturn(completer);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		when(completer.addArgumentCompleter(any(String.class), any())).thenReturn(true);
		final ArrayList<String> list = new ArrayList<>();
		list.add("one");
		list.add("two");

		// when
		annotatedCommand.addArgumentCompleter("testPlayerMapper", list);

		// then
		verify(completer, times(1))
				.addArgumentCompleter(eq("testPlayerMapper"), argThat(function -> function.apply(null).equals(list)));
	}

	@Test
	void incorrectAddArgumentCompleter() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestTabCompleter completer = mock(TestTabCompleter.class);
		when(command.getTabCompleter()).thenReturn(completer);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		when(completer.addArgumentCompleter(any(String.class), any())).thenReturn(false);

		// when
		final Executable result = () -> annotatedCommand.addArgumentCompleter("testPlayerMapper", new ArrayList<>());

		// then
		assertThrows(IllegalArgumentException.class, result);
		verify(completer, times(1)).addArgumentCompleter(eq("testPlayerMapper"), any());
	}
}
