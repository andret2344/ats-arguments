/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.FallbackException;
import eu.andret.arguments.api.annotation.Mapper;
import eu.andret.arguments.entity.MappingConfig;
import eu.andret.arguments.entity.MappingSet;
import eu.andret.arguments.mapper.impl.MethodSelector;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.assertj.core.api.ThrowableAssert;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class MethodSelectorTest {
	@Test
	void invokeMethodWithNoArgs() throws ReflectiveOperationException {
		// given
		final IFallbackSelector fallbackSelector = mock(IFallbackSelector.class);
		final IMethodSelector selector = new MethodSelector(fallbackSelector);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = spy(commandClass.getDeclaredMethod("testMethod"));

		// when
		final Object[] result = selector.recalculateArguments(method, "testMethod");

		// then
		assertThat(result).isEmpty();
	}

	@Test
	void invokeFallbackMethodWith() throws ReflectiveOperationException {
		// given
		final Function<String, World> getWorld = s -> null;
		final MappingConfig mappingConfig = new MappingConfig();
		mappingConfig.addArgumentMapper("testWorldMapper", new MappingSet<>(World.class, getWorld, Objects::isNull));
		final IFallbackSelector fallbackSelector = mock(IFallbackSelector.class);
		final IMethodSelector selector = new MethodSelector(fallbackSelector, mappingConfig);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> provider = TestMethodsProvider.class;
		final Method methodWorld = spy(provider.getDeclaredMethod("testMethodWithParam", World.class));
		final Mapper mapper = methodWorld.getParameters()[0].getAnnotation(Mapper.class);
		final List<Method> methods = List.of(provider.getDeclaredMethod("testMethodArgumentFallback",
				String.class));
		when(fallbackSelector.selectFallback(mapper, World.class, provider)).thenReturn(methods);

		// when
		final ThrowableAssert.ThrowingCallable callable = () -> selector.recalculateArguments(methodWorld, "testMethod", "test");

		// then
		assertThatThrownBy(callable).isInstanceOf(FallbackException.class);
	}

	@Test
	void invokeMethodOneArg() throws ReflectiveOperationException {
		// given
		final IFallbackSelector fallbackSelector = mock(IFallbackSelector.class);
		final IMethodSelector selector = new MethodSelector(fallbackSelector);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = spy(commandClass.getDeclaredMethod("testMethodWithArgument", String.class));

		// when
		final Object[] result = selector.recalculateArguments(method, "testMethodWithArgument", "test");

		// then
		assertThat(result).containsExactly("test");
	}

	@Test
	void invokeMethodOneArgMapper() throws ReflectiveOperationException {
		// given
		final IFallbackSelector fallbackSelector = mock(IFallbackSelector.class);
		final MappingConfig mappingConfig = new MappingConfig();
		final Location location = mock(Location.class);
		mappingConfig.addTypeMapper(Location.class, new MappingSet<>(Location.class, s -> location, Objects::isNull));
		final IMethodSelector selector = new MethodSelector(fallbackSelector, mappingConfig);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = spy(commandClass.getDeclaredMethod("testMethodWithMappedArgument", Location.class));

		// when
		final Object[] result = selector.recalculateArguments(method, "testMethodWithArgument", "test");

		// then
		assertThat(result).containsExactly(location);
	}

	@Test
	void invokeMethodPosition() throws ReflectiveOperationException {
		// given
		final IFallbackSelector fallbackSelector = mock(IFallbackSelector.class);
		final IMethodSelector selector = new MethodSelector(fallbackSelector);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = spy(commandClass.getDeclaredMethod("testMethodSecondWithCorrectPosition", String.class, String.class));

		// when
		final Object[] result = selector.recalculateArguments(method, "test", "testMethodWithCorrectPosition", "test2");

		// then
		assertThat(result).containsExactly("test", "test2");
	}

	@Test
	void invokeMethodWithVarArg() throws ReflectiveOperationException {
		// given
		final IFallbackSelector fallbackSelector = mock(IFallbackSelector.class);
		final IMethodSelector selector = new MethodSelector(fallbackSelector);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = spy(commandClass.getDeclaredMethod("testMethodWithIntVararg", int[].class));

		// when
		final Object[] result = selector.recalculateArguments(method, "testMethodWithIntVararg", "1", "2");

		// then
		assertThat(result).isEqualTo(new int[][]{{1, 2}});
	}

	@Test
	void invokeMethodWithParamArg() throws ReflectiveOperationException {
		// given
		final World world = mock(World.class);
		final Function<String, World> getWorld = s -> world;
		final MappingConfig mappingConfig = new MappingConfig();
		mappingConfig.addArgumentMapper("testWorldMapper", new MappingSet<>(World.class, getWorld, Objects::isNull));
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = spy(commandClass.getDeclaredMethod("testMethodWithParam", World.class));
		final IFallbackSelector fallbackSelector = mock(IFallbackSelector.class);
		final IMethodSelector selector = new MethodSelector(fallbackSelector, mappingConfig);

		// when
		final Object[] result = selector.recalculateArguments(method, "testMethodWithIntVararg", "world");

		// then
		assertThat(result).containsExactly(world);
	}
}
