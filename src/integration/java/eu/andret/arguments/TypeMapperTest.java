/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.executor.IntegrationTestCommandExecutor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class TypeMapperTest {
	@Test
	void customTypeMapperCorrectCall() {
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final World world = mock(World.class);
		when(world.getName()).thenReturn("testWorldName");
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> commandExecutor = new LocalCommandExecutor<>(annotatedCommand, IntegrationTestCommandExecutor.class, plugin);
		when(command.getExecutor()).thenReturn(commandExecutor);

		annotatedCommand.addTypeMapper(World.class, ignored -> world);
		final String[] args = {"testWithWorld", "test"};

		annotatedCommand.setOnUnknownSubCommandExecutionListener(commandSender -> commandSender.sendMessage("unknown"));

		// when
		final boolean result = commandExecutor.onCommand(sender, command, "IntegrationTest", args);

		// then
		assertTrue(result);
		verify(sender, times(1)).sendMessage("The world: testWorldName");
		verify(sender, times(0)).sendMessage("unknown");
	}

	@Test
	void customTypeMapperIncorrectCall() {
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final Player player = mock(Player.class);
		when(player.getName()).thenReturn("testPlayerName");
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> commandExecutor = new LocalCommandExecutor<>(annotatedCommand, IntegrationTestCommandExecutor.class, plugin);
		when(command.getExecutor()).thenReturn(commandExecutor);

		final String[] args = {"testWithPlayer", "test"};

		annotatedCommand.setOnUnknownSubCommandExecutionListener(commandSender -> commandSender.sendMessage("unknown"));

		// when
		final boolean result = commandExecutor.onCommand(sender, command, "IntegrationTest", args);

		// then
		assertTrue(result);
		verify(sender, times(1)).sendMessage("unknown");
		verifyNoMoreInteractions(sender);
	}

	@Test
	void defaultTypeMapperCorrectCall() {
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final Player player = mock(Player.class);
		when(player.getName()).thenReturn("testPlayerName");
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> commandExecutor = new LocalCommandExecutor<>(annotatedCommand, IntegrationTestCommandExecutor.class, plugin);
		when(command.getExecutor()).thenReturn(commandExecutor);

		final String[] args = {"testWithInt", "1"};

		annotatedCommand.setOnUnknownSubCommandExecutionListener(commandSender -> commandSender.sendMessage("unknown"));

		// when
		final boolean result = commandExecutor.onCommand(sender, command, "IntegrationTest", args);

		// then
		assertTrue(result);
		verify(sender, times(1)).sendMessage("An int: 1");
		verify(sender, times(0)).sendMessage("unknown");
	}
}
