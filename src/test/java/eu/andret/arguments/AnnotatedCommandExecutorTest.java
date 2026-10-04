package eu.andret.arguments;

import eu.andret.arguments.provider.EmptyClass;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class AnnotatedCommandExecutorTest {
	@Test
	void insanitiesWithCorrectData() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final JavaPlugin plugin = mock(JavaPlugin.class);

		// when
		final AnnotatedCommandExecutor<JavaPlugin> executor = new EmptyClass(sender, plugin);

		//then
		assertThat(sender).isSameAs(executor.getSender());
		assertThat(plugin).isSameAs(executor.getPlugin());
	}
}
