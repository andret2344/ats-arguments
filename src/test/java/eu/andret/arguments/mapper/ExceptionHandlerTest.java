package eu.andret.arguments.mapper;

import eu.andret.arguments.mapper.impl.ExceptionHandler;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.assertj.core.api.ThrowableAssert;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExceptionHandlerTest {
	@Test
	public void testNotThrowingException() throws NoSuchMethodException {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final IMethodInvoker methodInvoker = mock(IMethodInvoker.class);
		final IExceptionHandler exceptionHandler = new ExceptionHandler(methodInvoker);
		final List<String> strings = List.of("one", "two");
		final TestMethodsProvider provider = new TestMethodsProvider(sender, plugin);
		final Class<TestMethodsProvider> testMethodsProviderClass = TestMethodsProvider.class;
		final Method testMethod = testMethodsProviderClass.getDeclaredMethod("testMethod");
		final Object[] data = new Object[0];
		when(methodInvoker.invokeMethod(testMethod, provider, data)).thenReturn(strings);

		// when
		final List<String> result = exceptionHandler.handleException(testMethod, provider, data, testMethodsProviderClass.getMethods());

		// then
		assertThat(result).containsExactlyElementsOf(strings);
	}

	@Test
	public void testThrowingNotCaughtException() throws NoSuchMethodException {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final IMethodInvoker methodInvoker = mock(IMethodInvoker.class);
		final IExceptionHandler exceptionHandler = new ExceptionHandler(methodInvoker);
		final TestMethodsProvider provider = new TestMethodsProvider(sender, plugin);
		final Class<TestMethodsProvider> testMethodsProviderClass = TestMethodsProvider.class;
		final Method testMethod = testMethodsProviderClass.getDeclaredMethod("testMethodThrowingException", String.class, String.class);
		final Object[] data = {"one", "two"};
		when(methodInvoker.invokeMethod(testMethod, provider, data))
				.thenThrow(new RuntimeException(new IllegalAccessException("on purpose")));

		// when
		final ThrowableAssert.ThrowingCallable callable = () -> exceptionHandler.handleException(testMethod, provider, data, testMethodsProviderClass.getMethods());

		// then
		assertThatThrownBy(callable)
				.isInstanceOf(RuntimeException.class)
				.cause()
				.isInstanceOf(IllegalAccessException.class)
				.hasMessage("on purpose");
	}

	@Test
	public void testThrowingCaughtException() throws NoSuchMethodException {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final IMethodInvoker methodInvoker = mock(IMethodInvoker.class);
		final IExceptionHandler exceptionHandler = new ExceptionHandler(methodInvoker);
		final TestMethodsProvider provider = new TestMethodsProvider(sender, plugin);
		final Class<TestMethodsProvider> testMethodsProviderClass = TestMethodsProvider.class;
		final Method testMethod = testMethodsProviderClass.getDeclaredMethod("testMethodThrowingUncheckedException");
		final Object[] data = {"one", "two"};
		final IllegalArgumentException exception = new IllegalArgumentException("on purpose");
		when(methodInvoker.invokeMethod(testMethod, provider, data))
				.thenThrow(new RuntimeException(exception));
		when(methodInvoker.invokeMethod(any(), eq(provider), eq(new Object[0])))
				.thenReturn(List.of("no parameter"));
		when(methodInvoker.invokeMethod(any(), eq(provider), eq(new Object[]{exception})))
				.thenReturn(List.of("on purpose"));

		// when
		final List<String> result = exceptionHandler.handleException(testMethod, provider, data, testMethodsProviderClass.getMethods());

		// then
		assertThat(result)
				.containsExactlyInAnyOrder("no parameter", "on purpose");
	}
}
