/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.FallbackException;
import eu.andret.arguments.api.annotation.Mapper;
import eu.andret.arguments.api.entity.FallbackConstants;
import eu.andret.arguments.entity.MappingConfig;
import eu.andret.arguments.entity.MappingSet;
import eu.andret.arguments.local.LocalFallbackSelector;
import eu.andret.arguments.mapper.impl.FallbackSelector;
import eu.andret.arguments.mapper.impl.MethodSelector;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class MethodSelectorTest {
	@Test
	void invokeMethodWithNoArgs() throws ReflectiveOperationException {
		// given
		final FallbackSelector<JavaPlugin> fallbackSelector = mock(LocalFallbackSelector.class);
		final IMethodSelector selector = new MethodSelector<>(fallbackSelector);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = spy(commandClass.getDeclaredMethod("testMethod"));

		// when
		final Object[] result = selector.recalculateArguments(method, "testMethod");

		// then
		assertArrayEquals(new Object[0], result);
	}

	@Test
	void invokeFallbackMethodWith() throws ReflectiveOperationException {
		// given
		abstract class LocalFunction implements Function<String, World> {
		}
		final Function<String, World> getWorld = mock(LocalFunction.class);
		final MappingConfig mappingConfig = new MappingConfig();
		mappingConfig.add("testWorldMapper", new MappingSet<>(World.class, getWorld, FallbackConstants.ALWAYS));
		final FallbackSelector<JavaPlugin> fallbackSelector = mock(LocalFallbackSelector.class);
		final IMethodSelector selector = new MethodSelector<>(fallbackSelector, mappingConfig);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> provider = TestMethodsProvider.class;
		final Method methodWorld = spy(provider.getDeclaredMethod("testMethodWithParam", World.class));
		final Mapper mapper = methodWorld.getParameters()[0].getAnnotation(Mapper.class);
		final List<Method> methods = List.of(provider.getDeclaredMethod("testMethodArgumentFallback",
				String.class));
		when(fallbackSelector.selectFallback(mapper, World.class, provider)).thenReturn(methods);

		// when
		final Executable result = () -> selector.recalculateArguments(methodWorld, "testMethod", "test");

		// then
		assertThrows(FallbackException.class, result);
	}

	@Test
	void invokeMethodOneArg() throws ReflectiveOperationException {
		// given
		final FallbackSelector<JavaPlugin> fallbackSelector = mock(LocalFallbackSelector.class);
		final IMethodSelector selector = new MethodSelector<>(fallbackSelector);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = spy(commandClass.getDeclaredMethod("testMethodWithArgument", String.class));

		// when
		final Object[] result = selector.recalculateArguments(method, "testMethodWithArgument", "test");

		// then
		assertArrayEquals(new Object[]{"test"}, result);
	}

	@Test
	void invokeMethodPosition() throws ReflectiveOperationException {
		// given
		final FallbackSelector<JavaPlugin> fallbackSelector = mock(LocalFallbackSelector.class);
		final IMethodSelector selector = new MethodSelector<>(fallbackSelector);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = spy(commandClass.getDeclaredMethod("testMethodSecondWithCorrectPosition", String.class, String.class));

		// when
		final Object[] result = selector.recalculateArguments(method, "test", "testMethodWithCorrectPosition", "test2");

		// then
		assertArrayEquals(new Object[]{"test", "test2"}, result);
	}

	@Test
	void invokeMethodWithVarArg() throws ReflectiveOperationException {
		// given
		final FallbackSelector<JavaPlugin> fallbackSelector = mock(LocalFallbackSelector.class);
		final IMethodSelector selector = new MethodSelector<>(fallbackSelector);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = spy(commandClass.getDeclaredMethod("testMethodWithIntVararg", int[].class));

		// when
		final Object[] result = selector.recalculateArguments(method, "testMethodWithIntVararg", "1", "2");

		// then
		assertArrayEquals(new int[][]{{1, 2}}, result);
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
		mappingConfig.add("testWorldMapper", new MappingSet<>(World.class, getWorld, FallbackConstants.NEVER));
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = spy(commandClass.getDeclaredMethod("testMethodWithParam", World.class));
		final FallbackSelector<JavaPlugin> fallbackSelector = mock(LocalFallbackSelector.class);
		final IMethodSelector selector = new MethodSelector<>(fallbackSelector, mappingConfig);

		// when
		final Object[] result = selector.recalculateArguments(method, "testMethodWithIntVararg", "world");

		// then
		assertArrayEquals(new Object[]{world}, result);
	}
}
