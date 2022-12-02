/*
 * Copyright Andret (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.entity.SomeEnum;
import eu.andret.arguments.executor.ComplexIntegrationTestCommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ComplexTest {
	@DataProvider(name = "getParameters")
	static Object[][] getParameters() {
		return new Object[][]{
				{new String[]{"list"}, "I've got 1"},
				{new String[]{"list", "2"}, "I've got 2"},
				{new String[]{"list", "TWO"}, "I've got TWO 1 times"},
				{new String[]{"list", "3", "ONE"}, "I've got ONE 3 times"},
				{new String[]{"list", "X"}, "X is not a valid value!"}
		};
	}

	@Test(dataProvider = "getParameters")
	void testWithNoArguments(final String[] args, final String result) {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender commandSender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> commandExecutor = new LocalCommandExecutor<>(annotatedCommand, ComplexIntegrationTestCommandExecutor.class, plugin);
		when(command.getExecutor()).thenReturn(commandExecutor);
		annotatedCommand.addEnumMapper(SomeEnum.class);
		annotatedCommand.setOnUnknownSubCommandExecutionListener(sender -> sender.sendMessage("unknown"));

		// when
		final boolean commandResult = commandExecutor.onCommand(commandSender, command, "ComplexIntegrationTest", args);

		// then
		assertThat(commandResult).isTrue();
		verify(commandSender, times(0)).sendMessage("unknown");
		verify(commandSender, times(1)).sendMessage(result);
	}
}
