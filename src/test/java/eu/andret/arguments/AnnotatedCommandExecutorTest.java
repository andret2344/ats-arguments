/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
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
		CommandSender sender = mock(CommandSender.class);
		JavaPlugin plugin = mock(JavaPlugin.class);

		// when
		AnnotatedCommandExecutor executor = new EmptyClass(sender, plugin);

		//then
		assertEquals(sender, executor.sender);
		assertEquals(sender, executor.getSender());
		assertEquals(plugin, executor.plugin);
		assertEquals(plugin, executor.getPlugin());
	}
}
