/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.Mapper;
import eu.andret.arguments.mapper.impl.MethodInvoker;
import eu.andret.arguments.provider.MalformedClass;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MethodInvokerTest {
	@Test
	void invokeMethodWithNoArgs() throws ReflectiveOperationException {
		// given
		JavaPlugin plugin = mock(JavaPlugin.class);
		CommandSender sender = mock(CommandSender.class);
		IMethodInvoker invoker = new MethodInvoker(plugin, new HashMap<>());
		Class<? extends AnnotatedCommandExecutor> commandClass = TestMethodsProvider.class;
		Method method = spy(commandClass.getDeclaredMethod("testMethod"));

		// when
		invoker.invokeMethod(method, new String[]{"testMethod"}, sender, commandClass);

		// then
		verify(method, times(1)).invoke(eq(new TestMethodsProvider(sender, plugin)));
	}

	@Test
	void invokeMethodOneArg() throws ReflectiveOperationException {
		// given
		JavaPlugin plugin = mock(JavaPlugin.class);
		CommandSender sender = mock(CommandSender.class);
		IMethodInvoker invoker = new MethodInvoker(plugin, new HashMap<>());
		Class<? extends AnnotatedCommandExecutor> commandClass = TestMethodsProvider.class;
		Method method = spy(commandClass.getDeclaredMethod("testMethodWithArgument", String.class));

		// when
		invoker.invokeMethod(method, new String[]{"testMethodWithArgument", "test"}, sender, commandClass);

		// then
		verify(method, times(1)).invoke(eq(new TestMethodsProvider(sender, plugin)), eq("test"));
	}

	@Test
	void invokeMethodPosition() throws ReflectiveOperationException {
		// given
		JavaPlugin plugin = mock(JavaPlugin.class);
		CommandSender sender = mock(CommandSender.class);
		IMethodInvoker invoker = new MethodInvoker(plugin, new HashMap<>());
		Class<? extends AnnotatedCommandExecutor> commandClass = TestMethodsProvider.class;
		Method method = spy(commandClass.getDeclaredMethod("testMethodWithCorrectPosition", String.class));

		// when
		invoker.invokeMethod(method, new String[]{"test", "testMethodWithCorrectPosition"}, sender, commandClass);

		// then
		verify(method, times(1)).invoke(eq(new TestMethodsProvider(sender, plugin)), eq("test"));
	}

	@Test
	void throwExceptionOnMissingConstructor() throws ReflectiveOperationException {
		// given
		JavaPlugin plugin = mock(JavaPlugin.class);
		CommandSender sender = mock(CommandSender.class);
		IMethodInvoker invoker = new MethodInvoker(plugin, new HashMap<>());
		Class<? extends AnnotatedCommandExecutor> commandClass = MalformedClass.class;
		Method method = spy(commandClass.getDeclaredMethod("world"));

		// when
		Executable result = () -> invoker.invokeMethod(method, new String[]{"world"}, sender, commandClass);

		// then
		assertThrows(IllegalStateException.class, result);
		verify(method, times(0)).invoke(eq(new TestMethodsProvider(sender, plugin)), eq("test"));
	}

	@Test
	void invokeMethodTwiceWithNoArgs() throws ReflectiveOperationException {
		// given
		JavaPlugin plugin = mock(JavaPlugin.class);
		CommandSender sender = mock(CommandSender.class);
		IMethodInvoker invoker = new MethodInvoker(plugin, new HashMap<>());
		Class<? extends AnnotatedCommandExecutor> commandClass = TestMethodsProvider.class;
		Method method = spy(commandClass.getDeclaredMethod("testMethod"));

		// when
		invoker.invokeMethod(method, new String[]{"testMethod"}, sender, commandClass);
		invoker.invokeMethod(method, new String[]{"testMethod"}, sender, commandClass);

		// then
		verify(method, times(2)).invoke(eq(new TestMethodsProvider(sender, plugin)));
	}

	@Test
	void invokeMethodWithMismatchedParamNameArg() throws ReflectiveOperationException {
		// given
		JavaPlugin plugin = mock(JavaPlugin.class);
		CommandSender sender = mock(CommandSender.class);
		abstract class LocalFunction implements Function<String, World> {
		}
		Function<String, World> getWorld = mock(LocalFunction.class);
		World world = mock(World.class);
		when(getWorld.apply(eq("world"))).thenReturn(world);
		HashMap<String, Mapper<?>> mappers = new HashMap<>();
		Class<? extends AnnotatedCommandExecutor> commandClass = TestMethodsProvider.class;
		Method method = spy(commandClass.getDeclaredMethod("testMethodWithParam", World.class));

		IMethodInvoker invoker = new MethodInvoker(plugin, mappers);

		// when
		Executable ex = () -> invoker.invokeMethod(method, new String[]{"testMethodWithIntVararg", "world"}, sender, commandClass);

		// then
		assertThrows(UnsupportedOperationException.class, ex);
		verify(method, times(0)).invoke(eq(new TestMethodsProvider(sender, plugin)), eq(world));
		verify(getWorld, times(0)).apply(eq("world"));
	}

	@Test
	void invokeMethodWithMismatchedParamTypeArg() throws ReflectiveOperationException {
		// given
		JavaPlugin plugin = mock(JavaPlugin.class);
		CommandSender sender = mock(CommandSender.class);
		abstract class LocalFunction implements Function<String, Location> {
		}
		Function<String, Location> getLocation = mock(LocalFunction.class);
		Location world = mock(Location.class);
		when(getLocation.apply(eq("world"))).thenReturn(world);
		HashMap<String, Mapper<?>> mappers = new HashMap<>();
		mappers.put("testWorldMapper", new Mapper<>(Location.class, getLocation));
		Class<? extends AnnotatedCommandExecutor> commandClass = TestMethodsProvider.class;
		Method method = spy(commandClass.getDeclaredMethod("testMethodWithParam", World.class));
		IMethodInvoker invoker = new MethodInvoker(plugin, mappers);

		// when
		Executable ex = () -> invoker.invokeMethod(method, new String[]{"testMethodWithIntVararg", "world"}, sender, commandClass);

		// then
		assertThrows(UnsupportedOperationException.class, ex);
		verify(method, times(0)).invoke(eq(new TestMethodsProvider(sender, plugin)), eq(world));
		verify(getLocation, times(0)).apply(eq("world"));
	}

	@Test
	void invokeMethodWithVarArg() throws ReflectiveOperationException {
		// given
		JavaPlugin plugin = mock(JavaPlugin.class);
		CommandSender sender = mock(CommandSender.class);
		IMethodInvoker invoker = new MethodInvoker(plugin, new HashMap<>());
		Class<? extends AnnotatedCommandExecutor> commandClass = TestMethodsProvider.class;
		Method method = spy(commandClass.getDeclaredMethod("testMethodWithIntVararg", int[].class));

		// when
		invoker.invokeMethod(method, new String[]{"testMethodWithIntVararg", "1", "2"}, sender, commandClass);

		// then
		verify(method, times(1)).invoke(eq(new TestMethodsProvider(sender, plugin)), eq((Object) new int[]{1, 2}));
	}

	@Test
	void invokeMethodWithParamArg() throws ReflectiveOperationException {
		// given
		JavaPlugin plugin = mock(JavaPlugin.class);
		CommandSender sender = mock(CommandSender.class);
		abstract class LocalFunction implements Function<String, World> {
		}
		Function<String, World> getWorld = mock(LocalFunction.class);
		World world = mock(World.class);
		when(getWorld.apply(eq("world"))).thenReturn(world);
		HashMap<String, Mapper<?>> mappers = new HashMap<>();
		mappers.put("testWorldMapper", new Mapper<>(World.class, getWorld));
		Class<? extends AnnotatedCommandExecutor> commandClass = TestMethodsProvider.class;
		Method method = spy(commandClass.getDeclaredMethod("testMethodWithParam", World.class));

		IMethodInvoker invoker = new MethodInvoker(plugin, mappers);

		// when
		invoker.invokeMethod(method, new String[]{"testMethodWithIntVararg", "world"}, sender, commandClass);

		// then
		verify(method, times(1)).invoke(eq(new TestMethodsProvider(sender, plugin)), eq(world));
		verify(getWorld, times(1)).apply(eq("world"));
	}

	@Test
	void invokeMethodWithException() throws ReflectiveOperationException {
		// given
		JavaPlugin plugin = mock(JavaPlugin.class);
		CommandSender sender = mock(CommandSender.class);
		IMethodInvoker invoker = new MethodInvoker(plugin, new HashMap<>());
		Class<? extends AnnotatedCommandExecutor> commandClass = TestMethodsProvider.class;
		Method method = spy(commandClass.getDeclaredMethod("testMethodWithException"));
		Logger logger = mock(Logger.class);
		when(plugin.getLogger()).thenReturn(logger);

		// when
		invoker.invokeMethod(method, new String[]{"testMethodWithException"}, sender, commandClass);

		// then
		verify(logger, times(1)).log(eq(Level.SEVERE), any(Throwable.class), any());
		verify(method, times(1)).invoke(eq(new TestMethodsProvider(sender, plugin)));
	}
}
