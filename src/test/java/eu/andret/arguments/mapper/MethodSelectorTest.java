package eu.andret.arguments.mapper;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.annotation.Mapper;
import eu.andret.arguments.entity.ExecutionCall;
import eu.andret.arguments.entity.MappingConfig;
import eu.andret.arguments.entity.MappingSet;
import eu.andret.arguments.mapper.impl.MethodSelector;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MethodSelectorTest {
	@Test
	void invokeMethodWithNoArgs() throws ReflectiveOperationException {
		// given
		final IFallbackSelector fallbackSelector = mock(IFallbackSelector.class);
		final IMethodSelector selector = new MethodSelector(fallbackSelector);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = commandClass.getDeclaredMethod("testMethod");

		// when
		final ExecutionCall result = selector.selectMethod(method, new String[]{"testMethod"}, commandClass);

		// then
		assertThat(result.methods()).containsExactly(method);
		assertThat(result.data()).isEmpty();
	}

	@Test
	void invokeFallbackMethodWith() throws ReflectiveOperationException {
		// given
		final Function<String, World> getWorld = _ -> null;
		final MappingConfig mappingConfig = new MappingConfig();
		mappingConfig.addArgumentMapper("testWorldMapper", new MappingSet<>(World.class, getWorld, Objects::isNull));
		final IFallbackSelector fallbackSelector = mock(IFallbackSelector.class);
		final IMethodSelector selector = new MethodSelector(fallbackSelector, mappingConfig);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> provider = TestMethodsProvider.class;
		final Method methodWorld = provider.getDeclaredMethod("testMethodWithParam", World.class);
		final Mapper mapper = methodWorld.getParameters()[0].getAnnotation(Mapper.class);
		final List<Method> methods = List.of(provider.getDeclaredMethod("testMethodArgumentFallback",
				String.class));
		when(fallbackSelector.selectFallback(mapper, World.class, provider)).thenReturn(methods);

		// when
		final ExecutionCall result = selector.selectMethod(methodWorld, new String[]{"testMethod", "test"}, provider);

		// then
		assertThat(result.methods()).isEqualTo(methods);
		assertThat(result.data()).containsExactly("test");
	}

	@Test
	void invokeMethodOneArg() throws ReflectiveOperationException {
		// given
		final IFallbackSelector fallbackSelector = mock(IFallbackSelector.class);
		final IMethodSelector selector = new MethodSelector(fallbackSelector);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = commandClass.getDeclaredMethod("testMethodWithArgument", String.class);

		// when
		final ExecutionCall result = selector.selectMethod(method, new String[]{"testMethodWithArgument", "test"}, commandClass);

		// then
		assertThat(result.data()).containsExactly("test");
	}

	@Test
	void invokeMethodOneArgMapper() throws ReflectiveOperationException {
		// given
		final IFallbackSelector fallbackSelector = mock(IFallbackSelector.class);
		final MappingConfig mappingConfig = new MappingConfig();
		final Location location = mock(Location.class);
		mappingConfig.addTypeMapper(Location.class, new MappingSet<>(Location.class, _ -> location, Objects::isNull));
		final IMethodSelector selector = new MethodSelector(fallbackSelector, mappingConfig);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = commandClass.getDeclaredMethod("testMethodWithMappedArgument", Location.class);

		// when
		final ExecutionCall result = selector.selectMethod(method, new String[]{"testMethodWithArgument", "test"}, commandClass);

		// then
		assertThat(result.data()).containsExactly(location);
	}

	@Test
	void invokeMethodPosition() throws ReflectiveOperationException {
		// given
		final IFallbackSelector fallbackSelector = mock(IFallbackSelector.class);
		final IMethodSelector selector = new MethodSelector(fallbackSelector);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = commandClass.getDeclaredMethod("testMethodSecondWithCorrectPosition", String.class, String.class);

		// when
		final ExecutionCall result = selector.selectMethod(method, new String[]{"test", "testMethodWithCorrectPosition", "test2"}, commandClass);

		// then
		assertThat(result.data()).containsExactly("test", "test2");
	}

	@Test
	void invokeMethodWithVarArg() throws ReflectiveOperationException {
		// given
		final IFallbackSelector fallbackSelector = mock(IFallbackSelector.class);
		final IMethodSelector selector = new MethodSelector(fallbackSelector);
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = commandClass.getDeclaredMethod("testMethodWithIntVararg", int[].class);

		// when
		final ExecutionCall result = selector.selectMethod(method, new String[]{"testMethodWithIntVararg", "1", "2"}, commandClass);

		// then
		assertThat(result.data()).isEqualTo(new int[][]{{1, 2}});
	}

	@Test
	void invokeMethodWithParamArg() throws ReflectiveOperationException {
		// given
		final World world = mock(World.class);
		final Function<String, World> getWorld = _ -> world;
		final MappingConfig mappingConfig = new MappingConfig();
		mappingConfig.addArgumentMapper("testWorldMapper", new MappingSet<>(World.class, getWorld, Objects::isNull));
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass = TestMethodsProvider.class;
		final Method method = commandClass.getDeclaredMethod("testMethodWithParam", World.class);
		final IFallbackSelector fallbackSelector = mock(IFallbackSelector.class);
		final IMethodSelector selector = new MethodSelector(fallbackSelector, mappingConfig);

		// when
		final ExecutionCall result = selector.selectMethod(method, new String[]{"testMethodWithIntVararg", "world"}, commandClass);

		// then
		assertThat(result.data()).containsExactly(world);
	}
}
