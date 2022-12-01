/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.mapper.impl.MethodInvoker;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MethodInvokerTest {
	@Mock
	private Method method;
	@Mock
	private CommandSender sender;
	@Mock
	private JavaPlugin javaPlugin;
	@Mock
	private IResponseMapper responseMapper;

	@Test
	void invokeMethodCorrectly() throws InvocationTargetException, IllegalAccessException {
		// given
		final String[] data = {"test", "result"};
		final List<String> resultList = List.of(data);
		final MethodInvoker methodInvoker = new MethodInvoker(responseMapper);
		final TestMethodsProvider provider = new TestMethodsProvider(sender, javaPlugin);

		when(method.invoke(eq(provider), any())).thenReturn(resultList);
		when(responseMapper.mapResponse(method, resultList)).thenReturn(resultList);

		// when
		final List<String> list = methodInvoker.invokeMethod(method, provider, data);

		// then
		assertEquals(2, list.size());
		assertEquals("test", list.get(0));
		assertEquals("result", list.get(1));
	}

	@Test
	void invokeMethodIncorrectly() throws InvocationTargetException, IllegalAccessException {
		// given
		final String[] data = {"test", "result"};
		final MethodInvoker methodInvoker = new MethodInvoker(responseMapper);
		final TestMethodsProvider provider = new TestMethodsProvider(sender, javaPlugin);

		when(method.invoke(eq(provider), any())).thenThrow(new IllegalAccessException());

		// when
		final Executable result = () -> methodInvoker.invokeMethod(method, provider, data);

		// then
		assertThrows(IllegalAccessException.class, result);
	}
}
