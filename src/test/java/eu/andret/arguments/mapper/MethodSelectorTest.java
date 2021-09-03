/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.annotation.Mapper;
import eu.andret.arguments.api.annotation.TypeFallback;
import eu.andret.arguments.entity.ExecutionCall;
import eu.andret.arguments.entity.MappingConfig;
import eu.andret.arguments.entity.MappingSet;
import eu.andret.arguments.local.LocalFallbackSelector;
import eu.andret.arguments.mapper.impl.FallbackSelector;
import eu.andret.arguments.mapper.impl.MethodSelector;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MethodSelectorTest {
	@Test
	void invokeMethodWithNoArgs() throws ReflectiveOperationException {
		// given
		final FallbackSelector<JavaPlugin> fallbackSelector = mock(LocalFallbackSelector.class);
		final IMethodSelector<JavaPlugin> selector = new MethodSelector<>(fallbackSelector);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = spy(commandClass.getDeclaredMethod("testMethod"));

		// when
		final ExecutionCall executionCall = selector.selectMethod(method, new String[]{"testMethod"}, commandClass);

		// then
		validate(executionCall, List.of(method), new Object[0]);
	}

	@Test
	void invokeFallbackMethodWith() throws ReflectiveOperationException {
		// given
		abstract class LocalFunction implements Function<String, World> {
		}
		final Function<String, World> getWorld = mock(LocalFunction.class);
		final MappingConfig mappingConfig = new MappingConfig();
		mappingConfig.add("testWorldMapper", new MappingSet<>(World.class, getWorld, TypeFallback.ALWAYS));
		final FallbackSelector<JavaPlugin> fallbackSelector = mock(LocalFallbackSelector.class);
		final IMethodSelector<JavaPlugin> selector = new MethodSelector<>(fallbackSelector, mappingConfig);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> provider = TestMethodsProvider.class;
		final Method methodWorld = spy(provider.getDeclaredMethod("testMethodWithParam", World.class));
		final Mapper mapper = methodWorld.getParameters()[0].getAnnotation(Mapper.class);
		final List<Method> methods = List.of(provider.getDeclaredMethod("testMethodArgumentFallback",
				String.class));
		when(fallbackSelector.selectFallback(mapper, World.class, provider)).thenReturn(methods);

		// when
		final ExecutionCall executionCall = selector.selectMethod(methodWorld, new String[]{"testMethod", "test"}, provider);

		// then
		validate(executionCall, methods, new Object[]{"test"});
	}

	@Test
	void invokeMethodOneArg() throws ReflectiveOperationException {
		// given
		final FallbackSelector<JavaPlugin> fallbackSelector = mock(LocalFallbackSelector.class);
		final IMethodSelector<JavaPlugin> selector = new MethodSelector<>(fallbackSelector);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = spy(commandClass.getDeclaredMethod("testMethodWithArgument", String.class));

		// when
		final ExecutionCall executionCall = selector.selectMethod(method, new String[]{"testMethodWithArgument", "test"}, commandClass);

		// then
		validate(executionCall, List.of(method), new Object[]{"test"});
	}

	@Test
	void invokeMethodPosition() throws ReflectiveOperationException {
		// given
		final FallbackSelector<JavaPlugin> fallbackSelector = mock(LocalFallbackSelector.class);
		final IMethodSelector<JavaPlugin> selector = new MethodSelector<>(fallbackSelector);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = spy(commandClass.getDeclaredMethod("testMethodWithCorrectPosition", String.class, String.class));

		// when
		final ExecutionCall executionCall = selector.selectMethod(method, new String[]{"test", "testMethodWithCorrectPosition", "test2"}, commandClass);

		// then
		validate(executionCall, List.of(method), new Object[]{"test", "test2"});
	}

	@Test
	void invokeMethodWithVarArg() throws ReflectiveOperationException {
		// given
		final FallbackSelector<JavaPlugin> fallbackSelector = mock(LocalFallbackSelector.class);
		final IMethodSelector<JavaPlugin> selector = new MethodSelector<>(fallbackSelector);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = spy(commandClass.getDeclaredMethod("testMethodWithIntVararg", int[].class));

		// when
		final ExecutionCall executionCall = selector.selectMethod(method, new String[]{"testMethodWithIntVararg", "1", "2"}, commandClass);

		// then
		final Object[][] expectedData = {{1, 2}};
		validate(executionCall, List.of(method), expectedData);
	}

	@Test
	void invokeMethodWithParamArg() throws ReflectiveOperationException {
		// given
		abstract class LocalFunction implements Function<String, World> {
		}
		final Function<String, World> getWorld = mock(LocalFunction.class);
		final World world = mock(World.class);
		when(getWorld.apply("world")).thenReturn(world);
		final MappingConfig mappingConfig = new MappingConfig();
		mappingConfig.add("testWorldMapper", new MappingSet<>(World.class, getWorld, TypeFallback.NEVER));
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = spy(commandClass.getDeclaredMethod("testMethodWithParam", World.class));
		final FallbackSelector<JavaPlugin> fallbackSelector = mock(LocalFallbackSelector.class);
		final IMethodSelector<JavaPlugin> selector = new MethodSelector<>(fallbackSelector, mappingConfig);

		// when
		final ExecutionCall executionCall = selector.selectMethod(method, new String[]{"testMethodWithIntVararg", "world"}, commandClass);

		// then
		validate(executionCall, List.of(method), new Object[]{world});
		verify(getWorld, times(1)).apply("world");
	}

	private void validate(final ExecutionCall result, final List<Method> methods, final Object[] data) {
		assertEquals(methods, result.getMethods());
		assertEquals(Arrays.deepToString(data), Arrays.deepToString(result.getData()));
	}
}
