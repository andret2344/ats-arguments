/*
 * Copyright Andret (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.executor.ExceptionTestCommandExecutor;
import org.assertj.core.api.ThrowableAssert;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExceptionHandlerTest {
	@Test
	public void testThrowingNonCaughtException() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender commandSender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> commandExecutor = new LocalCommandExecutor<>(annotatedCommand, ExceptionTestCommandExecutor.class, plugin);
		when(command.getExecutor()).thenReturn(commandExecutor);

		// when
		final ThrowableAssert.ThrowingCallable callable = () -> commandExecutor.onCommand(commandSender, command, "ExceptionTestCommandExecutor", new String[]{"throwCheckedException"});

		// then
		assertThatThrownBy(callable)
				.isInstanceOf(RuntimeException.class)
				.cause()
				.isInstanceOf(IllegalAccessException.class);
	}

	@Test
	public void testThrowingCaughtException() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender commandSender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> commandExecutor = new LocalCommandExecutor<>(annotatedCommand, ExceptionTestCommandExecutor.class, plugin);
		when(command.getExecutor()).thenReturn(commandExecutor);

		// when
		final boolean result = commandExecutor.onCommand(commandSender, command, "ExceptionTestCommandExecutor", new String[]{"throwUncheckedException"});

		// then
		assertThat(result).isTrue();
		verify(commandSender, times(1)).sendMessage("no parameter");
		verify(commandSender, times(1)).sendMessage("on purpose");
	}
}
