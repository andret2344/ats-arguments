/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.mapper.impl.MethodInvoker;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.assertj.core.api.ThrowableAssert;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class MethodInvokerTest {
	@Test
	void invokeMethodCorrectly() throws NoSuchMethodException {
		final CommandSender sender = mock(CommandSender.class);
		final JavaPlugin javaPlugin = mock(JavaPlugin.class);
		final IResponseMapper responseMapper = (method, result) -> List.of("test", "result");
		final IMethodInvoker methodInvoker = new MethodInvoker(responseMapper);
		final TestMethodsProvider provider = new TestMethodsProvider(sender, javaPlugin);
		final Method method = provider.getClass().getDeclaredMethod("testMethodReturningStringList");

		// when
		final List<String> list = methodInvoker.invokeMethod(method, provider, new Object[0]);

		// then
		assertThat(list)
				.hasSize(2)
				.containsExactly("test", "result");
	}

	@Test
	void invokeMethodIncorrectly() throws NoSuchMethodException {
		final CommandSender sender = mock(CommandSender.class);
		final JavaPlugin javaPlugin = mock(JavaPlugin.class);
		final IResponseMapper responseMapper = mock(IResponseMapper.class);
		final String[] data = {"test", "result"};
		final IMethodInvoker methodInvoker = new MethodInvoker(responseMapper);
		final TestMethodsProvider provider = new TestMethodsProvider(sender, javaPlugin);
		final Method method = provider.getClass().getDeclaredMethod("testMethodThrowingException", String.class, String.class);

		// when
		final ThrowableAssert.ThrowingCallable callable = () -> methodInvoker.invokeMethod(method, provider, data);

		// then
		assertThatThrownBy(callable)
				.isInstanceOf(InvocationTargetException.class)
				.hasCauseExactlyInstanceOf(IllegalAccessException.class);
	}
}
