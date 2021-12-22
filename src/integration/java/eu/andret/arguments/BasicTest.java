/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class BasicTest {
	public static Collection<Object[]> getDataSourceForPositionTest() {
		final Object[][] objects = {
				{new String[]{"testWithoutParameters"}, "none"},
				{new String[]{"testWithParameter", "1"}, "1"},
				{new String[]{"testWithParameter", "test"}, "test"},
				{new String[]{"position", "testWithChangedPosition"}, "position"}
		};
		return Arrays.asList(objects);
	}

	@ParameterizedTest(name = "{0} => \"{1}\"")
	@MethodSource("getDataSourceForPositionTest")
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
		assertTrue(result);
		verify(sender, times(1)).sendMessage(expectedResult);
	}
}
