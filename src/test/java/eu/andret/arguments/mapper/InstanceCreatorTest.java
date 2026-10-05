package eu.andret.arguments.mapper;

import eu.andret.arguments.mapper.impl.InstanceCreator;
import eu.andret.arguments.provider.ExceptionalClass;
import eu.andret.arguments.provider.FewArgumentsClass;
import eu.andret.arguments.provider.ManyArgumentsClass;
import eu.andret.arguments.provider.ManyConstructorsClass;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.assertj.core.api.ThrowableAssert;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class InstanceCreatorTest {
	@Test
	void createInstanceCorrectly() throws ReflectiveOperationException {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final IInstanceCreator creator = new InstanceCreator();
		final Class<TestMethodsProvider> commandClass = TestMethodsProvider.class;

		// when
		final TestMethodsProvider provider = creator.createInstance(sender, plugin, commandClass);

		// then
		assertThat(provider).isNotNull();
	}

	@Test
	void createInstanceWithManyArguments() throws ReflectiveOperationException {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final IInstanceCreator creator = new InstanceCreator();
		final Class<ManyArgumentsClass> commandClass = ManyArgumentsClass.class;

		// when
		final World world = mock(World.class);
		final ManyArgumentsClass provider = creator.createInstance(sender, plugin, commandClass, world);

		// then
		assertThat(provider).isNotNull();
	}

	@Test
	void createInstanceWithException() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final IInstanceCreator creator = new InstanceCreator();
		final Class<ExceptionalClass> commandClass = ExceptionalClass.class;

		// when
		final ThrowableAssert.ThrowingCallable callable = () -> creator.createInstance(sender, plugin, commandClass);

		// then
		assertThatThrownBy(callable).isInstanceOf(InvocationTargetException.class);
	}

	@Test
	void createInstanceWithFewArguments() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final IInstanceCreator creator = new InstanceCreator();
		final Class<FewArgumentsClass> commandClass = FewArgumentsClass.class;

		// when
		final ThrowableAssert.ThrowingCallable callable = () -> creator.createInstance(sender, plugin, commandClass);

		// then
		assertThatThrownBy(callable).isInstanceOf(IllegalStateException.class);
	}

	@Test
	void createInstanceOfMultipleConstructors() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final IInstanceCreator creator = new InstanceCreator();
		final Class<ManyConstructorsClass> commandClass = ManyConstructorsClass.class;

		// when
		final ThrowableAssert.ThrowingCallable callable = () -> creator.createInstance(sender, plugin, commandClass);

		// then
		assertThatThrownBy(callable).isInstanceOf(UnsupportedOperationException.class);
	}
}
