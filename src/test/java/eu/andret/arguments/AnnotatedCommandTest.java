/*
 * Copyright Andret (c) 2018-2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AnnotatedCommandTest {
	@Test
	void correctCommandReturned() {
		// given
		PluginCommand command = mock(PluginCommand.class);
		LocalCommandExecutor executor = mock(LocalCommandExecutor.class);
		when(command.getExecutor()).thenReturn(executor);
		AnnotatedCommand annotatedCommand = new AnnotatedCommand(command);

		// when
		PluginCommand result = annotatedCommand.getCommand();

		// then
		assertSame(command, result);
	}

	@Test
	void correctListenersSetup() {
		// given
		PluginCommand command = mock(PluginCommand.class);
		LocalCommandExecutor executor = mock(LocalCommandExecutor.class);
		when(command.getExecutor()).thenReturn(executor);
		AnnotatedCommand annotatedCommand = new AnnotatedCommand(command);

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
		PluginCommand command = mock(PluginCommand.class);
		LocalCommandExecutor executor = mock(LocalCommandExecutor.class);
		when(command.getExecutor()).thenReturn(executor);
		AnnotatedCommand annotatedCommand = new AnnotatedCommand(command);

		// when
		CommandExecutor result = annotatedCommand.getLocalCommandExecutor();

		// then
		assertSame(executor, result);
	}

	@Test
	void correctCompleterReturned() {
		// given
		PluginCommand command = mock(PluginCommand.class);
		AnnotatedCommand annotatedCommand = new AnnotatedCommand(command);
		LocalTabCompleter completer = mock(LocalTabCompleter.class);
		when(command.getTabCompleter()).thenReturn(completer);

		// when
		TabCompleter result = annotatedCommand.getLocalTabCompleter();

		// then
		assertSame(completer, result);
	}

	@Test
	void correctAddMapper() {
		// given
		PluginCommand command = mock(PluginCommand.class);
		LocalCommandExecutor executor = mock(LocalCommandExecutor.class);
		when(command.getExecutor()).thenReturn(executor);
		AnnotatedCommand annotatedCommand = new AnnotatedCommand(command);
		when(executor.addMapper(anyString(), any())).thenReturn(true);

		// when
		annotatedCommand.addArgumentMapper("test", int.class, Integer::parseInt);

		// then
		verify(executor, times(1)).addMapper(eq("test"), any());
	}

	@Test
	void incorrectAddMapper() {
		// given
		PluginCommand command = mock(PluginCommand.class);
		LocalCommandExecutor executor = mock(LocalCommandExecutor.class);
		when(command.getExecutor()).thenReturn(executor);
		AnnotatedCommand annotatedCommand = new AnnotatedCommand(command);
		when(executor.addMapper(anyString(), any())).thenReturn(false);

		// when
		Executable result = () -> annotatedCommand.addArgumentMapper("test", int.class, Integer::parseInt);

		// then
		assertThrows(IllegalArgumentException.class, result);
		verify(executor, times(1)).addMapper(eq("test"), any());
	}

	@Test
	void correctAddTypeCompleter() {
		// given
		PluginCommand command = mock(PluginCommand.class);
		LocalTabCompleter completer = mock(LocalTabCompleter.class);
		when(command.getTabCompleter()).thenReturn(completer);
		AnnotatedCommand annotatedCommand = new AnnotatedCommand(command);
		when(completer.addTypeCompleter(any(Class.class), any())).thenReturn(true);
		ArrayList<String> list = new ArrayList<>();
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
		PluginCommand command = mock(PluginCommand.class);
		LocalTabCompleter completer = mock(LocalTabCompleter.class);
		when(command.getTabCompleter()).thenReturn(completer);
		AnnotatedCommand annotatedCommand = new AnnotatedCommand(command);
		when(completer.addTypeCompleter(any(Class.class), any())).thenReturn(false);

		// when
		Executable result = () -> annotatedCommand.addTypeCompleter(World.class, new ArrayList<>());

		// then
		assertThrows(IllegalArgumentException.class, result);
		verify(completer, times(1)).addTypeCompleter(eq(World.class), any());
	}

	@Test
	void correctAddArgumentCompleter() {
		// given
		PluginCommand command = mock(PluginCommand.class);
		LocalTabCompleter completer = mock(LocalTabCompleter.class);
		when(command.getTabCompleter()).thenReturn(completer);
		AnnotatedCommand annotatedCommand = new AnnotatedCommand(command);
		when(completer.addArgumentCompleter(any(String.class), any())).thenReturn(true);
		ArrayList<String> list = new ArrayList<>();
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
		PluginCommand command = mock(PluginCommand.class);
		LocalTabCompleter completer = mock(LocalTabCompleter.class);
		when(command.getTabCompleter()).thenReturn(completer);
		AnnotatedCommand annotatedCommand = new AnnotatedCommand(command);
		when(completer.addArgumentCompleter(any(String.class), any())).thenReturn(false);

		// when
		Executable result = () -> annotatedCommand.addArgumentCompleter("testPlayerMapper", new ArrayList<>());

		// then
		assertThrows(IllegalArgumentException.class, result);
		verify(completer, times(1)).addArgumentCompleter(eq("testPlayerMapper"), any());
	}

	@Test
	void correctAutoTranslateColors() {
		// given
		PluginCommand command = mock(PluginCommand.class);
		LocalCommandExecutor executor = mock(LocalCommandExecutor.class);
		when(command.getExecutor()).thenReturn(executor);
		when(executor.getOptions()).thenReturn(new AnnotatedCommand.Options());
		AnnotatedCommand annotatedCommand = new AnnotatedCommand(command);

		// when
		annotatedCommand.getOptions().setAutoTranslateColors(true);

		// then
		assertTrue(annotatedCommand.getLocalCommandExecutor().getOptions().isAutoTranslateColors());
	}
}
