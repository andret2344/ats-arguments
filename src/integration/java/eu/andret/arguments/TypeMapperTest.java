package eu.andret.arguments;

import eu.andret.arguments.executor.IntegrationTestCommandExecutor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class TypeMapperTest {
	private static final String UNKNOWN = "unknown";
	public static final String INTEGRATION_TEST = "IntegrationTest";

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

		annotatedCommand.setOnUnknownSubCommandExecutionListener(commandSender -> commandSender.sendMessage(UNKNOWN));

		// when
		commandExecutor.onCommand(sender, command, INTEGRATION_TEST, args);

		// then
		verify(sender).sendMessage("The world: testWorldName");
		verify(sender, never()).sendMessage(UNKNOWN);
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

		annotatedCommand.setOnUnknownSubCommandExecutionListener(commandSender -> commandSender.sendMessage(UNKNOWN));

		// when
		commandExecutor.onCommand(sender, command, INTEGRATION_TEST, args);

		// then
		verify(sender).sendMessage(UNKNOWN);
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

		annotatedCommand.setOnUnknownSubCommandExecutionListener(commandSender -> commandSender.sendMessage(UNKNOWN));

		// when
		commandExecutor.onCommand(sender, command, INTEGRATION_TEST, args);

		// then
		verify(sender).sendMessage("An int: 1");
		verify(sender, never()).sendMessage(UNKNOWN);
	}
}
