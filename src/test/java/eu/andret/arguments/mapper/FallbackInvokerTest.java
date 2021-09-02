/*
 * Copyright Andret (c) 2018-2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.api.annotation.Mapper;
import eu.andret.arguments.mapper.impl.FallbackInvoker;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class FallbackInvokerTest {
	@Test
	void invokeMismatch() {
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final FallbackInvoker<JavaPlugin> invoker = new FallbackInvoker<>();
		final TestMethodsProvider testMethodsProvider = new TestMethodsProvider(sender, plugin);

		final Object result = invoker.invokeFallback(null, "test", World.class, testMethodsProvider);

		assertEquals(new ArrayList<>(), result);
	}

	@Test
	void dupa() throws ReflectiveOperationException {
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final IFallbackInvoker<JavaPlugin> invoker = new FallbackInvoker<>();
		final Class<? extends TestMethodsProvider> providerClass = TestMethodsProvider.class;
		final Method fallbackMethod = spy(providerClass.getDeclaredMethod("testMethodWithParamArgumentFallback",
				String.class));
		final TestMethodsProvider testMethodsProvider = (TestMethodsProvider) providerClass.getConstructors()[0]
				.newInstance(sender, plugin);
		final Method method = providerClass.getDeclaredMethod("testMethodWithParam", World.class);
		final Mapper mapper = method.getParameters()[0].getAnnotation(Mapper.class);

		final Object result = invoker.invokeFallback(mapper, "test", World.class, testMethodsProvider);

		assertEquals(new ArrayList<>(), result);
		verify(fallbackMethod, times(1)).invoke(any(), any());
	}
}
