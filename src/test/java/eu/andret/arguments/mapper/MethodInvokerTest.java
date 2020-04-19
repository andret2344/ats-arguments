/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.Mapper;
import eu.andret.arguments.mapper.impl.MethodInvoker;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.function.Function;
import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MethodInvokerTest {
	@Test
	public void invokeMethodWithNoArgs() throws ReflectiveOperationException {
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
	public void invokeMethodOneArg() throws ReflectiveOperationException {
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
	public void invokeMethodWithVarArg() throws ReflectiveOperationException {
		// given
		JavaPlugin plugin = mock(JavaPlugin.class);
		CommandSender sender = mock(CommandSender.class);
		IMethodInvoker invoker = new MethodInvoker(plugin, new HashMap<>());
		Class<? extends AnnotatedCommandExecutor> commandClass = TestMethodsProvider.class;
		Method method = spy(commandClass.getDeclaredMethod("testMethodWithIntVararg", int[].class));

		// when
		invoker.invokeMethod(method, new String[]{"testMethodWithIntVararg", "1", "2"}, sender, commandClass);

		// then
		verify(method, times(1))
				.invoke(eq(new TestMethodsProvider(sender, plugin)), eq((Object) new int[]{1, 2}));
	}

	@Test
	public void invokeMethodWithParamArg() throws ReflectiveOperationException {
		// given
		JavaPlugin plugin = mock(JavaPlugin.class);
		CommandSender sender = mock(CommandSender.class);
		Function<String, World> getWorld = mock(Function.class);
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
		verify(method, times(1))
				.invoke(eq(new TestMethodsProvider(sender, plugin)), eq(world));
		verify(getWorld, times(1)).apply(eq("world"));
	}

	@Test
	public void invokeMethodWithException() throws ReflectiveOperationException {
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
		verify(logger, times(1)).throwing(eq("eu.andret.arguments.mapper.impl.MethodInvoker"), eq("invoke"), any());
		verify(method, times(1)).invoke(eq(new TestMethodsProvider(sender, plugin)));
	}
}
