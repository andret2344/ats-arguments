package eu.andret.arguments;

import eu.andret.arguments.executor.IntegrationTestCommandExecutor;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AutoTranslateColorsTest {
	@Test
	void playerOnlyMethodWithColorsTranslated() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final Player player = mock(Player.class);
		final PluginCommand command = mock(PluginCommand.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> commandExecutor = new LocalCommandExecutor<>(annotatedCommand, IntegrationTestCommandExecutor.class, plugin);
		when(player.getName()).thenReturn("Steve");
		when(command.getExecutor()).thenReturn(commandExecutor);
		annotatedCommand.getOptions().setAutoTranslateColors(true);

		// when
		commandExecutor.onCommand(player, command, "IntegrationTest", new String[]{"testPlayerOnly"});

		// then
		verify(player).sendMessage(ChatColor.GREEN + "The player: Steve");
	}
}
