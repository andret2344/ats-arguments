/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.mapper.impl.InstanceCreator;
import eu.andret.arguments.provider.ExceptionalClass;
import eu.andret.arguments.provider.FewArgumentsClass;
import eu.andret.arguments.provider.ManyArgumentsClass;
import eu.andret.arguments.provider.ManyConstructorsClass;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class InstanceCreatorTest {
	@Test
	void createInstanceCorrectly() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final IInstanceCreator creator = new InstanceCreator();
		final Class<TestMethodsProvider> commandClass = TestMethodsProvider.class;

		// when
		final TestMethodsProvider provider = creator.createInstance(sender, plugin, commandClass);

		// then
		assertNotNull(provider);
	}

	@Test
	void createInstanceWithManyArguments() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final IInstanceCreator creator = new InstanceCreator();
		final Class<ManyArgumentsClass> commandClass = ManyArgumentsClass.class;

		// when
		final World world = mock(World.class);
		final ManyArgumentsClass provider = creator.createInstance(sender, plugin, commandClass, world);

		// then
		assertNotNull(provider);
	}

	@Test
	void createInstanceWithException() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final IInstanceCreator creator = new InstanceCreator();
		final Class<ExceptionalClass> commandClass = ExceptionalClass.class;

		// when
		final Executable result = () -> creator.createInstance(sender, plugin, commandClass);

		// then
		assertThrows(InvocationTargetException.class, result);
	}

	@Test
	void createInstanceWithFewArguments() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final IInstanceCreator creator = new InstanceCreator();
		final Class<FewArgumentsClass> commandClass = FewArgumentsClass.class;

		// when
		final Executable result = () -> creator.createInstance(sender, plugin, commandClass);

		// then
		assertThrows(IllegalStateException.class, result);
	}

	@Test
	void createInstanceOfMultipleConstructors() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final IInstanceCreator creator = new InstanceCreator();
		final Class<ManyConstructorsClass> commandClass = ManyConstructorsClass.class;

		// when
		final Executable result = () -> creator.createInstance(sender, plugin, commandClass);

		// then
		assertThrows(UnsupportedOperationException.class, result);
	}
}
