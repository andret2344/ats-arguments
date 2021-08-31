/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.annotation.Fallback;
import eu.andret.arguments.entity.ExecutionCall;
import eu.andret.arguments.entity.MappingConfig;
import eu.andret.arguments.entity.MappingSet;
import eu.andret.arguments.mapper.impl.MethodInvoker;
import eu.andret.arguments.provider.MalformedClass;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MethodInvokerTest {
	@Test
	void invokeMethodWithNoArgs() throws ReflectiveOperationException {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final IMethodInvoker<JavaPlugin> invoker = new MethodInvoker<>(plugin);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = spy(commandClass.getDeclaredMethod("testMethod"));
		final ExecutionCall call = new ExecutionCall(method);

		// when
		invoker.invokeMethod(call, new String[]{"testMethod"}, sender, commandClass);

		// then
		verify(method, times(1)).invoke(new TestMethodsProvider(sender, plugin));
	}

	@Test
	void invokeFallbackMethodWith() throws ReflectiveOperationException {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		abstract class LocalFunction implements Function<String, World> {
		}
		final Function<String, World> getWorld = mock(LocalFunction.class);
		final MappingConfig mappingConfig = new MappingConfig();
		mappingConfig.add("testWorldMapper", new MappingSet<>(World.class, getWorld, Fallback.ALWAYS));
		final IMethodInvoker<JavaPlugin> invoker = new MethodInvoker<>(plugin, mappingConfig);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method methodWorld = spy(commandClass.getDeclaredMethod("testMethodWithParam", World.class));
		final Method methodString = spy(commandClass.getDeclaredMethod("testMethodWithParam", String.class));
		final ExecutionCall call = new ExecutionCall(methodWorld, methodString);

		// when
		invoker.invokeMethod(call, new String[]{"testMethod", "test"}, sender, commandClass);

		// then
		verify(methodWorld, times(0)).invoke(any());
		verify(methodString, times(1)).invoke(new TestMethodsProvider(sender, plugin), "test");
	}

	@Test
	void invokeMethodOneArg() throws ReflectiveOperationException {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final IMethodInvoker<JavaPlugin> invoker = new MethodInvoker<>(plugin);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = spy(commandClass.getDeclaredMethod("testMethodWithArgument", String.class));
		final ExecutionCall call = new ExecutionCall(method);

		// when
		invoker.invokeMethod(call, new String[]{"testMethodWithArgument", "test"}, sender, commandClass);

		// then
		verify(method, times(1)).invoke(new TestMethodsProvider(sender, plugin), "test");
	}

	@Test
	void invokeMethodPosition() throws ReflectiveOperationException {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final IMethodInvoker<JavaPlugin> invoker = new MethodInvoker<>(plugin);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = spy(commandClass.getDeclaredMethod("testMethodWithCorrectPosition", String.class));
		final ExecutionCall call = new ExecutionCall(method);

		// when
		invoker.invokeMethod(call, new String[]{"test", "testMethodWithCorrectPosition"}, sender, commandClass);

		// then
		verify(method, times(1)).invoke(new TestMethodsProvider(sender, plugin), "test");
	}

	@Test
	void throwExceptionOnMissingConstructor() throws ReflectiveOperationException {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final IMethodInvoker<JavaPlugin> invoker = new MethodInvoker<>(plugin);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = MalformedClass.class;
		final Method method = spy(commandClass.getDeclaredMethod("world"));
		final ExecutionCall call = new ExecutionCall(method);

		// when
		final Executable result = () -> invoker.invokeMethod(call, new String[]{"world"}, sender, commandClass);

		// then
		assertThrows(IllegalStateException.class, result);
		verify(method, times(0)).invoke(new TestMethodsProvider(sender, plugin), "test");
	}

	@Test
	void invokeMethodTwiceWithNoArgs() throws ReflectiveOperationException {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final IMethodInvoker<JavaPlugin> invoker = new MethodInvoker<>(plugin);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = spy(commandClass.getDeclaredMethod("testMethod"));
		final ExecutionCall call = new ExecutionCall(method);

		// when
		invoker.invokeMethod(call, new String[]{"testMethod"}, sender, commandClass);
		invoker.invokeMethod(call, new String[]{"testMethod"}, sender, commandClass);

		// then
		verify(method, times(2)).invoke(new TestMethodsProvider(sender, plugin));
	}

	@Test
	void invokeMethodWithMismatchedParamNameArg() throws ReflectiveOperationException {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		abstract class LocalFunction implements Function<String, World> {
		}
		final Function<String, World> getWorld = mock(LocalFunction.class);
		final World world = mock(World.class);
		when(getWorld.apply("world")).thenReturn(world);
		final MappingConfig mappingConfig = new MappingConfig();
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = spy(commandClass.getDeclaredMethod("testMethodWithParam", World.class));
		final ExecutionCall call = new ExecutionCall(method);
		final IMethodInvoker<JavaPlugin> invoker = new MethodInvoker<>(plugin, mappingConfig);

		// when
		final Executable ex = () -> invoker.invokeMethod(call, new String[]{"testMethodWithIntVararg", "world"}, sender, commandClass);

		// then
		assertThrows(UnsupportedOperationException.class, ex);
		verify(method, times(0)).invoke(new TestMethodsProvider(sender, plugin), world);
		verify(getWorld, times(0)).apply("world");
	}

	@Test
	void invokeMethodWithMismatchedParamTypeArg() throws ReflectiveOperationException {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		abstract class LocalFunction implements Function<String, Location> {
		}
		final Function<String, Location> getLocation = mock(LocalFunction.class);
		final Location world = mock(Location.class);
		when(getLocation.apply("world")).thenReturn(world);
		final MappingConfig mappingConfig = new MappingConfig();
		mappingConfig.add("testWorldMapper", new MappingSet<>(Location.class, getLocation, Fallback.NEVER));
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = spy(commandClass.getDeclaredMethod("testMethodWithParam", World.class));
		final IMethodInvoker<JavaPlugin> invoker = new MethodInvoker<>(plugin, mappingConfig);
		final ExecutionCall call = new ExecutionCall(method);

		// when
		final Executable ex = () -> invoker.invokeMethod(call, new String[]{"testMethodWithIntVararg", "world"}, sender, commandClass);

		// then
		assertThrows(UnsupportedOperationException.class, ex);
		verify(method, times(0)).invoke(new TestMethodsProvider(sender, plugin), world);
		verify(getLocation, times(0)).apply("world");
	}

	@Test
	void invokeMethodWithVarArg() throws ReflectiveOperationException {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final IMethodInvoker<JavaPlugin> invoker = new MethodInvoker<>(plugin);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = spy(commandClass.getDeclaredMethod("testMethodWithIntVararg", int[].class));
		final ExecutionCall call = new ExecutionCall(method);

		// when
		invoker.invokeMethod(call, new String[]{"testMethodWithIntVararg", "1", "2"}, sender, commandClass);

		// then
		final Object args = new int[]{1, 2};
		verify(method, times(1)).invoke(new TestMethodsProvider(sender, plugin), args);
	}

	@Test
	void invokeMethodWithParamArg() throws ReflectiveOperationException {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		abstract class LocalFunction implements Function<String, World> {
		}
		final Function<String, World> getWorld = mock(LocalFunction.class);
		final World world = mock(World.class);
		when(getWorld.apply("world")).thenReturn(world);
		final MappingConfig mappingConfig = new MappingConfig();
		mappingConfig.add("testWorldMapper", new MappingSet<>(World.class, getWorld, Fallback.NEVER));
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = spy(commandClass.getDeclaredMethod("testMethodWithParam", World.class));
		final ExecutionCall call = new ExecutionCall(method);
		final IMethodInvoker<JavaPlugin> invoker = new MethodInvoker<>(plugin, mappingConfig);

		// when
		invoker.invokeMethod(call, new String[]{"testMethodWithIntVararg", "world"}, sender, commandClass);

		// then
		verify(method, times(1)).invoke(new TestMethodsProvider(sender, plugin), world);
		verify(getWorld, times(1)).apply("world");
	}

	@Test
	void invokeMethodWithException() throws ReflectiveOperationException {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final IMethodInvoker<JavaPlugin> invoker = new MethodInvoker<>(plugin);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = spy(commandClass.getDeclaredMethod("testMethodWithException"));
		final Logger logger = mock(Logger.class);
		when(plugin.getLogger()).thenReturn(logger);
		final ExecutionCall call = new ExecutionCall(method);

		// when
		final Executable ex = () -> invoker.invokeMethod(call, new String[]{"testMethodWithException"}, sender, commandClass);

		// then
		assertThrows(InvocationTargetException.class, ex);
		verify(method, times(1)).invoke(new TestMethodsProvider(sender, plugin));
	}
}
