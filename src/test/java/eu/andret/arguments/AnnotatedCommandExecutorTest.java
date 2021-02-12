/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.provider.EmptyClass;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class AnnotatedCommandExecutorTest {
	@Test
	void insanitiesWithCorrectData() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final JavaPlugin plugin = mock(JavaPlugin.class);

		// when
		final AnnotatedCommandExecutor<JavaPlugin> executor = new EmptyClass(sender, plugin);

		//then
		assertEquals(sender, executor.getSender());
		assertEquals(plugin, executor.getPlugin());
	}
}
