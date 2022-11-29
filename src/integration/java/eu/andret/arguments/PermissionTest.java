/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.executor.IntegrationTestCommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PermissionTest {
	@Test
	void insufficientPermissions() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> commandExecutor = new LocalCommandExecutor<>(annotatedCommand, IntegrationTestCommandExecutor.class, plugin);
		when(sender.hasPermission(anyString())).thenReturn(false);
		when(command.getExecutor()).thenReturn(commandExecutor);
		annotatedCommand.setOnInsufficientPermissionsListener(sender1 -> sender1.sendMessage("no perms"));

		// when
		final boolean result = commandExecutor.onCommand(sender, command, "IntegrationTest", new String[]{"testWithPermission"});

		// then
		assertThat(result).isTrue();
		verify(sender, times(0)).sendMessage("permission");
		verify(sender, times(1)).sendMessage("no perms");
	}

	@Test
	void sufficientPermissions() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> commandExecutor = new LocalCommandExecutor<>(annotatedCommand, IntegrationTestCommandExecutor.class, plugin);
		when(sender.hasPermission("test")).thenReturn(true);
		when(command.getExecutor()).thenReturn(commandExecutor);
		annotatedCommand.setOnInsufficientPermissionsListener(sender1 -> sender1.sendMessage("no perms"));

		// when
		final boolean result = commandExecutor.onCommand(sender, command, "IntegrationTest", new String[]{"testWithPermission"});

		// then
		assertThat(result).isTrue();
		verify(sender, times(1)).sendMessage("permission");
		verify(sender, times(0)).sendMessage("no perms");
	}

	@Test
	void opPermissions() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> commandExecutor = new LocalCommandExecutor<>(annotatedCommand, IntegrationTestCommandExecutor.class, plugin);
		when(sender.isOp()).thenReturn(true);
		when(command.getExecutor()).thenReturn(commandExecutor);
		annotatedCommand.setOnInsufficientPermissionsListener(sender1 -> sender1.sendMessage("no perms"));

		// when
		final boolean result = commandExecutor.onCommand(sender, command, "IntegrationTest", new String[]{"testWithPermission"});

		// then
		assertThat(result).isTrue();
		verify(sender, times(1)).sendMessage("permission");
		verify(sender, times(0)).sendMessage("no perms");
	}

	@Test
	void consolePermissions() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final ConsoleCommandSender sender = mock(ConsoleCommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> commandExecutor = new LocalCommandExecutor<>(annotatedCommand, IntegrationTestCommandExecutor.class, plugin);
		when(sender.isOp()).thenReturn(true);
		when(command.getExecutor()).thenReturn(commandExecutor);
		annotatedCommand.setOnInsufficientPermissionsListener(sender1 -> sender1.sendMessage("no perms"));

		// when
		final boolean result = commandExecutor.onCommand(sender, command, "IntegrationTest", new String[]{"testWithPermission"});

		// then
		assertThat(result).isTrue();
		verify(sender, times(1)).sendMessage("permission");
		verify(sender, times(0)).sendMessage("no perms");
	}
}
