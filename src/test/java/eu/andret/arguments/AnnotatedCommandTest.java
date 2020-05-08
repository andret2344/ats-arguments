/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AnnotatedCommandTest {
	@Test
	public void correctCommandReturned() {
		// given
		PluginCommand command = mock(PluginCommand.class);
		AnnotatedCommand annotatedCommand = new AnnotatedCommand(command);

		// when
		PluginCommand result = annotatedCommand.getCommand();

		// then
		assertSame(command, result);
	}

	@Test
	public void correctListenersSetup() {
		// given
		PluginCommand command = mock(PluginCommand.class);
		AnnotatedCommand annotatedCommand = new AnnotatedCommand(command);
		LocalCommandExecutor executor = mock(LocalCommandExecutor.class);
		when(command.getExecutor()).thenReturn(executor);

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
	public void correctExecutorReturned() {
		// given
		PluginCommand command = mock(PluginCommand.class);
		AnnotatedCommand annotatedCommand = new AnnotatedCommand(command);
		LocalCommandExecutor executor = mock(LocalCommandExecutor.class);
		when(command.getExecutor()).thenReturn(executor);

		// when
		CommandExecutor result = annotatedCommand.getLocalCommandExecutor();

		// then
		assertSame(executor, result);
	}

	@Test
	public void correctCompleterReturned() {
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
	public void correctAddMapper() {
		// given
		PluginCommand command = mock(PluginCommand.class);
		AnnotatedCommand annotatedCommand = new AnnotatedCommand(command);
		LocalCommandExecutor executor = mock(LocalCommandExecutor.class);
		when(command.getExecutor()).thenReturn(executor);
		when(executor.addMapper(anyString(), any())).thenReturn(true);

		// when
		annotatedCommand.addArgumentMapper("test", int.class, Integer::parseInt);

		// then
		verify(executor, times(1)).addMapper(eq("test"), any());
	}

	@Test
	public void incorrectAddMapper() {
		// given
		PluginCommand command = mock(PluginCommand.class);
		AnnotatedCommand annotatedCommand = new AnnotatedCommand(command);
		LocalCommandExecutor executor = mock(LocalCommandExecutor.class);
		when(command.getExecutor()).thenReturn(executor);
		when(executor.addMapper(anyString(), any())).thenReturn(false);

		// when
		Executable result = () -> annotatedCommand.addArgumentMapper("test", int.class, Integer::parseInt);

		// then
		assertThrows(IllegalArgumentException.class, result);
		verify(executor, times(1)).addMapper(eq("test"), any());
	}
}
