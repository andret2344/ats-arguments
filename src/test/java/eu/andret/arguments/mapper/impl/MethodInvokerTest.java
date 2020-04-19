/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.mapper.IMethodInvoker;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
}
