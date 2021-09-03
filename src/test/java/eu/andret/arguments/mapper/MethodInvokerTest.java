/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.entity.ExecutionCall;
import eu.andret.arguments.mapper.impl.MethodInvoker;
import eu.andret.arguments.provider.MalformedClass;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
		final ExecutionCall call = new ExecutionCall(List.of(method), new String[0]);

		// when
		final List<Object> objects = invoker.invokeMethods(call, sender, commandClass);

		// then
		assertEquals(Collections.emptyList(), objects);
		verify(method, times(1)).invoke(new TestMethodsProvider(sender, plugin));
	}

	@Test
	void invokeMethodOneArg() throws ReflectiveOperationException {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final IMethodInvoker<JavaPlugin> invoker = new MethodInvoker<>(plugin);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = spy(commandClass.getDeclaredMethod("testMethodWithArgument", String.class));
		final ExecutionCall call = new ExecutionCall(List.of(method), new String[]{"test"});

		// when
		final List<Object> objects = invoker.invokeMethods(call, sender, commandClass);
		assertEquals(Collections.emptyList(), objects);

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
		final ExecutionCall call = new ExecutionCall(List.of(method), new String[]{"world"});

		// when
		final Executable result = () -> invoker.invokeMethods(call, sender, commandClass);

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
		final ExecutionCall call = new ExecutionCall(List.of(method), new String[0]);

		// when
		invoker.invokeMethods(call, sender, commandClass);
		invoker.invokeMethods(call, sender, commandClass);

		// then
		verify(method, times(2)).invoke(new TestMethodsProvider(sender, plugin));
	}

	@Test
	void invokeMethodWithVarArg() throws ReflectiveOperationException {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final IMethodInvoker<JavaPlugin> invoker = new MethodInvoker<>(plugin);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = spy(commandClass.getDeclaredMethod("testMethodWithIntVararg", int[].class));
		final ExecutionCall call = new ExecutionCall(List.of(method), new int[][]{{1, 2}});

		// when
		final List<Object> objects = invoker.invokeMethods(call, sender, commandClass);
		assertEquals(Collections.emptyList(), objects);

		// then
		final Object args = new int[]{1, 2};
		verify(method, times(1)).invoke(new TestMethodsProvider(sender, plugin), args);
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
		final ExecutionCall call = new ExecutionCall(List.of(method), new String[0]);

		// when
		final Executable ex = () -> invoker.invokeMethods(call, sender, commandClass);

		// then
		assertThrows(InvocationTargetException.class, ex);
		verify(method, times(1)).invoke(new TestMethodsProvider(sender, plugin));
	}
}
