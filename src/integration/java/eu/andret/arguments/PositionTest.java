/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.executor.IntegrationTestCommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PositionTest {
	@DataProvider(name = "getData")
	static Object[][] getData() {
		return new Object[][]{
				{new String[]{"testWithoutParameters"}, "none"},
				{new String[]{"testWithParameter", "1"}, "1"},
				{new String[]{"testWithParameter", "test"}, "test"},
				{new String[]{"position", "testWithChangedPosition"}, "position"}
		};
	}

	@Test(dataProvider = "getData")
	void simpleCallWithPosition(final String[] args, final String expectedResult) {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> commandExecutor = new LocalCommandExecutor<>(annotatedCommand, IntegrationTestCommandExecutor.class, plugin);

		// when
		final boolean result = commandExecutor.onCommand(sender, command, "IntegrationTest", args);

		// then
		assertThat(result).isTrue();
		verify(sender).sendMessage(expectedResult);
	}
}
