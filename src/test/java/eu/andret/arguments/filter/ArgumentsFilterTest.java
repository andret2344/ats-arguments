/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.filter;

import eu.andret.arguments.api.annotation.Fallback;
import eu.andret.arguments.entity.MappingSet;
import eu.andret.arguments.filter.impl.ArgumentsFilter;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import javax.xml.stream.Location;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ArgumentsFilterTest {
	@Test
	void methodWithoutArguments() throws NoSuchMethodException {
		// given
		final Map<String, MappingSet<?>> mappers = new HashMap<>();
		final IArgumentsFilter mapper = new ArgumentsFilter(mappers);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethod");

		// when
		final boolean result = mapper.filter(method, new String[]{"testMethod"});

		// then
		assertTrue(result);
	}

	@Test
	void methodWithArgumentsWithoutCommandArguments() throws NoSuchMethodException {
		// given
		final Map<String, MappingSet<?>> mappers = new HashMap<>();
		final IArgumentsFilter mapper = new ArgumentsFilter(mappers);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithArgument", String.class);

		// when
		final boolean result = mapper.filter(method, new String[]{"testMethod"});

		// then
		assertFalse(result);
	}

	@Test
	void methodWithoutArgumentsWithCommandArguments() throws NoSuchMethodException {
		// given
		final Map<String, MappingSet<?>> mappers = new HashMap<>();
		final IArgumentsFilter mapper = new ArgumentsFilter(mappers);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethod");

		// when
		final boolean result = mapper.filter(method, new String[]{"testMethod", "testArgument"});

		// then
		assertFalse(result);
	}

	@Test
	void methodWithArgumentsWithCommandArguments() throws NoSuchMethodException {
		// given
		final Map<String, MappingSet<?>> mappers = new HashMap<>();
		final IArgumentsFilter mapper = new ArgumentsFilter(mappers);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithArgument", String.class);

		// when
		final boolean result = mapper.filter(method, new String[]{"testMethod", "testArgument"});

		// then
		assertTrue(result);
	}

	@Test
	void methodWithDifferentArgumentsWithMatchingCommandArguments() throws NoSuchMethodException {
		// given
		final Map<String, MappingSet<?>> mappers = new HashMap<>();
		final IArgumentsFilter mapper = new ArgumentsFilter(mappers);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithMultipleArguments", String.class, int.class, boolean.class);

		// when
		final boolean result = mapper.filter(method, new String[]{"testMethod", "testArgument", "1", "false"});

		// then
		assertTrue(result);
	}

	@Test
	void methodWithDifferentArgumentsWithNonMatchingCommandArguments() throws NoSuchMethodException {
		// given
		final Map<String, MappingSet<?>> mappers = new HashMap<>();
		final IArgumentsFilter mapper = new ArgumentsFilter(mappers);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithMultipleArguments", String.class, int.class, boolean.class);

		// when
		final boolean result = mapper.filter(method, new String[]{"testMethod", "testArgument", "dupa", "dupa"});

		// then
		assertFalse(result);
	}

	@Test
	void methodWithArray() throws NoSuchMethodException {
		// given
		final Map<String, MappingSet<?>> mappers = new HashMap<>();
		final IArgumentsFilter mapper = new ArgumentsFilter(mappers);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithArray", String[].class);

		// when
		final Executable result = () -> mapper.filter(method, new String[]{"testMethod", "testArgument", "dupa", "dupa"});

		// then
		assertThrows(IllegalArgumentException.class, result);
	}

	@Test
	void methodWithParamArgumentsWithMatchingCommandArguments() throws NoSuchMethodException {
		// given
		final Map<String, MappingSet<?>> mappers = new HashMap<>();
		mappers.put("testWorldMapper", new MappingSet<>(World.class, Bukkit::getWorld, Fallback.NEVER));
		final IArgumentsFilter mapper = new ArgumentsFilter(mappers);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithParam", World.class);

		// when
		final boolean result = mapper.filter(method, new String[]{"testMethod", "AIR"});

		// then
		assertTrue(result);
	}

	@Test
	void methodWithVarArgsWithMatchingCommandArguments() throws NoSuchMethodException {
		// given
		final Map<String, MappingSet<?>> mappers = new HashMap<>();
		final IArgumentsFilter mapper = new ArgumentsFilter(mappers);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithVararg", String[].class);

		// when
		final boolean result = mapper.filter(method, new String[]{"testMethod", "test", "test2"});

		// then
		assertTrue(result);
	}

	@Test
	void methodWithVarArgsWithNonMatchingCommandArguments() throws NoSuchMethodException {
		// given
		final Map<String, MappingSet<?>> mappers = new HashMap<>();
		final IArgumentsFilter mapper = new ArgumentsFilter(mappers);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithIntVararg", int[].class);

		// when
		final boolean result = mapper.filter(method, new String[]{"testMethod", "test", "1"});

		// then
		assertFalse(result);
	}

	@Test
	void methodWithParamVarArgsWithMatchingCommandArguments() throws NoSuchMethodException {
		// given
		final Map<String, MappingSet<?>> mappers = new HashMap<>();
		mappers.put("testWorldMapper", new MappingSet<>(World.class, Bukkit::getWorld, Fallback.NEVER));
		final IArgumentsFilter mapper = new ArgumentsFilter(mappers);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithParamVarArg", World[].class);

		// when
		final boolean result = mapper.filter(method, new String[]{"testMethod", "world", "world_nether"});

		// then
		assertTrue(result);
	}

	@Test
	void methodWithPrimitiveVarArgsWithMatchingCommandArguments() throws NoSuchMethodException {
		// given
		final Map<String, MappingSet<?>> mappers = new HashMap<>();
		mappers.put("testWorldMapper", new MappingSet<>(World.class, Bukkit::getWorld, Fallback.NEVER));
		final IArgumentsFilter mapper = new ArgumentsFilter(mappers);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithVararg", String[].class);

		// when
		final boolean result = mapper.filter(method, new String[]{"testMethod", "1", "2"});

		// then
		assertFalse(result);
	}

	@Test
	void methodWithMissingParamVarArgsWithMatchingCommandArguments() throws NoSuchMethodException {
		// given
		final Map<String, MappingSet<?>> mappers = new HashMap<>();
		final IArgumentsFilter mapper = new ArgumentsFilter(mappers);
		abstract class LocalFunction implements Function<String, Location> {
		}
		final LocalFunction getLocation = mock(LocalFunction.class);
		mappers.put("testWorldMapper", new MappingSet<>(Location.class, getLocation, Fallback.NEVER));
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithParamVarArg", World[].class);

		// when
		final boolean result = mapper.filter(method, new String[]{"testMethod", "world", "world_nether"});

		// then
		assertFalse(result);
	}

	@Test
	void methodWithMoreParamsThanCommand() throws NoSuchMethodException {
		// given
		final IArgumentsFilter mapper = new ArgumentsFilter(new HashMap<>());
		abstract class LocalFunction implements Function<String, Location> {
		}
		final LocalFunction getLocation = mock(LocalFunction.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithMultipleArguments", String.class, int.class, boolean.class);

		// when
		final boolean result = mapper.filter(method, new String[]{"testMethod", "world", "1"});

		// then
		assertFalse(result);
	}

	@Test
	void methodWithFewerParamsThanCommand() throws NoSuchMethodException {
		// given
		final IArgumentsFilter mapper = new ArgumentsFilter(new HashMap<>());
		abstract class LocalFunction implements Function<String, Location> {
		}
		final LocalFunction getLocation = mock(LocalFunction.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithMultipleArguments", String.class, int.class, boolean.class);

		// when
		final boolean result = mapper.filter(method, new String[]{"testMethod", "world", "1", "false", "test"});

		// then
		assertFalse(result);
	}
}
