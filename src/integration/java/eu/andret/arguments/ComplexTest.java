/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.entity.SomeEnum;
import eu.andret.arguments.executor.ComplexIntegrationTestCommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ComplexTest {
	@Test
	void testWithNoArguments() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender commandSender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> commandExecutor = new LocalCommandExecutor<>(annotatedCommand, ComplexIntegrationTestCommandExecutor.class, plugin);
		when(command.getExecutor()).thenReturn(commandExecutor);
		annotatedCommand.addEnumMapper(SomeEnum.class);
		final String[] args = {"list"};

		annotatedCommand.setOnUnknownSubCommandExecutionListener(sender -> sender.sendMessage("unknown"));

		// when
		final boolean result = commandExecutor.onCommand(commandSender, command, "ComplexIntegrationTest", args);

		// then
		assertTrue(result);
		verify(commandSender, times(0)).sendMessage("unknown");
		verify(commandSender, times(1)).sendMessage("I've got 1");
	}

	@Test
	void testWithIntArgument() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender commandSender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> commandExecutor = new LocalCommandExecutor<>(annotatedCommand, ComplexIntegrationTestCommandExecutor.class, plugin);
		when(command.getExecutor()).thenReturn(commandExecutor);
		annotatedCommand.addEnumMapper(SomeEnum.class);
		final String[] args = {"list", "2"};

		annotatedCommand.setOnUnknownSubCommandExecutionListener(sender -> sender.sendMessage("unknown"));

		// when
		final boolean result = commandExecutor.onCommand(commandSender, command, "ComplexIntegrationTest", args);

		// then
		assertTrue(result);
		verify(commandSender, times(0)).sendMessage("unknown");
		verify(commandSender, times(1)).sendMessage("I've got 2");
	}

	@Test
	void testWithEnumArgument() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender commandSender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> commandExecutor = new LocalCommandExecutor<>(annotatedCommand, ComplexIntegrationTestCommandExecutor.class, plugin);
		when(command.getExecutor()).thenReturn(commandExecutor);
		annotatedCommand.addEnumMapper(SomeEnum.class);
		final String[] args = {"list", "TWO"};

		annotatedCommand.setOnUnknownSubCommandExecutionListener(sender -> sender.sendMessage("unknown"));

		// when
		final boolean result = commandExecutor.onCommand(commandSender, command, "ComplexIntegrationTest", args);

		// then
		assertTrue(result);
		verify(commandSender, times(0)).sendMessage("unknown");
		verify(commandSender, times(1)).sendMessage("I've got TWO 1 times");
	}

	@Test
	void testWithBothArguments() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender commandSender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> commandExecutor = new LocalCommandExecutor<>(annotatedCommand, ComplexIntegrationTestCommandExecutor.class, plugin);
		when(command.getExecutor()).thenReturn(commandExecutor);
		annotatedCommand.addEnumMapper(SomeEnum.class);
		final String[] args = {"list", "3", "ONE"};

		annotatedCommand.setOnUnknownSubCommandExecutionListener(sender -> sender.sendMessage("unknown"));

		// when
		final boolean result = commandExecutor.onCommand(commandSender, command, "ComplexIntegrationTest", args);

		// then
		assertTrue(result);
		verify(commandSender, times(0)).sendMessage("unknown");
		verify(commandSender, times(1)).sendMessage("I've got ONE 3 times");
	}
}
