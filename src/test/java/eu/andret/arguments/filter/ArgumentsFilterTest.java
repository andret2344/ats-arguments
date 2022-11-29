/*
 * Copyright Andret (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.filter;

import eu.andret.arguments.entity.MappingConfig;
import eu.andret.arguments.entity.MappingSet;
import eu.andret.arguments.filter.impl.ArgumentsFilter;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.assertj.core.api.ThrowableAssert;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.testng.annotations.Test;

import javax.xml.stream.Location;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class ArgumentsFilterTest {
	@Test
	void methodWithoutArguments() throws NoSuchMethodException {
		// given
		final IArgumentsFilter mapper = new ArgumentsFilter(new MappingConfig());
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethod");

		// when
		final boolean result = mapper.filterArguments(method, new String[]{"testMethod"});

		// then
		assertThat(result).isTrue();
	}

	@Test
	void methodWithArgumentsWithoutCommandArguments() throws NoSuchMethodException {
		// given
		final IArgumentsFilter mapper = new ArgumentsFilter(new MappingConfig());
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithArgument", String.class);

		// when
		final boolean result = mapper.filterArguments(method, new String[]{"testMethod"});

		// then
		assertThat(result).isFalse();
	}

	@Test
	void methodWithoutArgumentsWithCommandArguments() throws NoSuchMethodException {
		// given
		final IArgumentsFilter mapper = new ArgumentsFilter(new MappingConfig());
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethod");

		// when
		final boolean result = mapper.filterArguments(method, new String[]{"testMethod", "testArgument"});

		// then
		assertThat(result).isFalse();
	}

	@Test
	void methodWithArgumentsWithCommandArguments() throws NoSuchMethodException {
		// given
		final IArgumentsFilter mapper = new ArgumentsFilter(new MappingConfig());
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithArgument", String.class);

		// when
		final boolean result = mapper.filterArguments(method, new String[]{"testMethod", "testArgument"});

		// then
		assertThat(result).isTrue();
	}

	@Test
	void methodWithDifferentArgumentsWithMatchingCommandArguments() throws NoSuchMethodException {
		// given
		final IArgumentsFilter mapper = new ArgumentsFilter(new MappingConfig());
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithMultipleArguments", String.class, int.class, boolean.class);

		// when
		final boolean result = mapper.filterArguments(method, new String[]{"testMethod", "testArgument", "1", "false"});

		// then
		assertThat(result).isTrue();
	}

	@Test
	void methodWithDifferentArgumentsWithNonMatchingCommandArguments() throws NoSuchMethodException {
		// given
		final IArgumentsFilter mapper = new ArgumentsFilter(new MappingConfig());
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithMultipleArguments", String.class, int.class, boolean.class);

		// when
		final boolean result = mapper.filterArguments(method, new String[]{"testMethod", "testArgument", "dupa", "dupa"});

		// then
		assertThat(result).isFalse();
	}

	@Test
	void methodWithArray() throws NoSuchMethodException {
		// given
		final IArgumentsFilter mapper = new ArgumentsFilter(new MappingConfig());
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithArray", String[].class);

		// when
		final ThrowableAssert.ThrowingCallable callable = () -> mapper.filterArguments(method, new String[]{"testMethod", "testArgument", "dupa", "dupa"});

		// then
		assertThatThrownBy(callable)
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Cannot be the array! Use VarArg instead. Method: eu.andret.arguments.provider.TestMethodsProvider#testMethodWithArray");
	}

	@Test
	void methodWithParamArgumentsWithMatchingCommandArguments() throws NoSuchMethodException {
		// given
		final MappingConfig mappingConfig = new MappingConfig();
		mappingConfig.addArgumentMapper("testWorldMapper", new MappingSet<>(World.class, Bukkit::getWorld, Objects::isNull));
		final IArgumentsFilter mapper = new ArgumentsFilter(mappingConfig);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithParam", World.class);

		// when
		final boolean result = mapper.filterArguments(method, new String[]{"testMethod", "AIR"});

		// then
		assertThat(result).isTrue();
	}

	@Test
	void methodWithVarArgsWithMatchingCommandArguments() throws NoSuchMethodException {
		// given
		final IArgumentsFilter mapper = new ArgumentsFilter(new MappingConfig());
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithVararg", String[].class);

		// when
		final boolean result = mapper.filterArguments(method, new String[]{"testMethod", "test", "test2"});

		// then
		assertThat(result).isTrue();
	}

	@Test
	void methodWithVarArgsWithNonMatchingCommandArguments() throws NoSuchMethodException {
		// given
		final IArgumentsFilter mapper = new ArgumentsFilter(new MappingConfig());
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithIntVararg", int[].class);

		// when
		final boolean result = mapper.filterArguments(method, new String[]{"testMethod", "test", "1"});

		// then
		assertThat(result).isFalse();
	}

	@Test
	void methodWithParamVarArgsWithMatchingCommandArguments() throws NoSuchMethodException {
		// given
		final MappingConfig mappingConfig = new MappingConfig();
		mappingConfig.addArgumentMapper("testWorldMapper", new MappingSet<>(World.class, Bukkit::getWorld, Objects::isNull));
		final IArgumentsFilter mapper = new ArgumentsFilter(mappingConfig);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithParamVarArg", World[].class);

		// when
		final boolean result = mapper.filterArguments(method, new String[]{"testMethod", "world", "world_nether"});

		// then
		assertThat(result).isTrue();
	}

	@Test
	void methodWithPrimitiveVarArgsWithMatchingCommandArguments() throws NoSuchMethodException {
		// given
		final MappingConfig mappingConfig = new MappingConfig();
		mappingConfig.addArgumentMapper("testWorldMapper", new MappingSet<>(World.class, Bukkit::getWorld, Objects::isNull));
		final IArgumentsFilter mapper = new ArgumentsFilter(mappingConfig);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithVararg", String[].class);

		// when
		final boolean result = mapper.filterArguments(method, new String[]{"testMethod", "1", "2"});

		// then
		assertThat(result).isFalse();
	}

	@Test
	void methodWithMissingParamVarArgsWithMatchingCommandArguments() throws NoSuchMethodException {
		// given
		final MappingConfig mappingConfig = new MappingConfig();
		final IArgumentsFilter mapper = new ArgumentsFilter(mappingConfig);
		abstract class LocalFunction implements Function<String, Location> {
		}
		final LocalFunction getLocation = mock(LocalFunction.class);
		mappingConfig.addArgumentMapper("testWorldMapper", new MappingSet<>(Location.class, getLocation, Objects::isNull));
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithParamVarArg", World[].class);

		// when
		final boolean result = mapper.filterArguments(method, new String[]{"testMethod", "world", "world_nether"});

		// then
		assertThat(result).isFalse();
	}

	@Test
	void methodWithMoreParamsThanCommand() throws NoSuchMethodException {
		// given
		final IArgumentsFilter mapper = new ArgumentsFilter(new MappingConfig());
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithMultipleArguments", String.class, int.class, boolean.class);

		// when
		final boolean result = mapper.filterArguments(method, new String[]{"testMethod", "world", "1"});

		// then
		assertThat(result).isFalse();
	}

	@Test
	void methodWithFewerParamsThanCommand() throws NoSuchMethodException {
		// given
		final IArgumentsFilter mapper = new ArgumentsFilter(new MappingConfig());
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithMultipleArguments", String.class, int.class, boolean.class);

		// when
		final boolean result = mapper.filterArguments(method, new String[]{"testMethod", "world", "1", "false", "test"});

		// then
		assertThat(result).isFalse();
	}

	@Test
	void methodWithTypeMapperWithMatchingCommandArguments() throws NoSuchMethodException {
		// given
		final MappingConfig mappingConfig = new MappingConfig();
		mappingConfig.addTypeMapper(boolean.class, new MappingSet<>(boolean.class, Boolean::parseBoolean, Objects::isNull));
		final IArgumentsFilter mapper = new ArgumentsFilter(mappingConfig);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithTypeCompletion", boolean.class);

		// when
		final boolean result = mapper.filterArguments(method, new String[]{"testMethod", "true"});

		// then
		assertThat(result).isTrue();
	}
}
