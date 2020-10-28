/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.entity.Mapper;
import eu.andret.arguments.mapper.impl.ArgumentsMapper;
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


class ArgumentsMapperTest {
	@Test
	void methodWithoutArguments() throws NoSuchMethodException {
		// given
		Map<String, Mapper<?>> mappers = new HashMap<>();
		IArgumentsMapper mapper = new ArgumentsMapper(mappers);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethod");

		// when
		boolean result = mapper.mapArguments(method, new String[]{"testMethod"});

		// then
		assertTrue(result);
	}

	@Test
	void methodWithArgumentsWithoutCommandArguments() throws NoSuchMethodException {
		// given
		Map<String, Mapper<?>> mappers = new HashMap<>();
		IArgumentsMapper mapper = new ArgumentsMapper(mappers);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithArgument", String.class);

		// when
		boolean result = mapper.mapArguments(method, new String[]{"testMethod"});

		// then
		assertFalse(result);
	}

	@Test
	void methodWithoutArgumentsWithCommandArguments() throws NoSuchMethodException {
		// given
		Map<String, Mapper<?>> mappers = new HashMap<>();
		IArgumentsMapper mapper = new ArgumentsMapper(mappers);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethod");

		// when
		boolean result = mapper.mapArguments(method, new String[]{"testMethod", "testArgument"});

		// then
		assertFalse(result);
	}

	@Test
	void methodWithArgumentsWithCommandArguments() throws NoSuchMethodException {
		// given
		Map<String, Mapper<?>> mappers = new HashMap<>();
		IArgumentsMapper mapper = new ArgumentsMapper(mappers);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithArgument", String.class);

		// when
		boolean result = mapper.mapArguments(method, new String[]{"testMethod", "testArgument"});

		// then
		assertTrue(result);
	}

	@Test
	void methodWithDifferentArgumentsWithMatchingCommandArguments() throws NoSuchMethodException {
		// given
		Map<String, Mapper<?>> mappers = new HashMap<>();
		IArgumentsMapper mapper = new ArgumentsMapper(mappers);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithMultipleArguments", String.class, int.class, boolean.class);

		// when
		boolean result = mapper.mapArguments(method, new String[]{"testMethod", "testArgument", "1", "false"});

		// then
		assertTrue(result);
	}

	@Test
	void methodWithDifferentArgumentsWithNonMatchingCommandArguments() throws NoSuchMethodException {
		// given
		Map<String, Mapper<?>> mappers = new HashMap<>();
		IArgumentsMapper mapper = new ArgumentsMapper(mappers);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithMultipleArguments", String.class, int.class, boolean.class);

		// when
		boolean result = mapper.mapArguments(method, new String[]{"testMethod", "testArgument", "dupa", "dupa"});

		// then
		assertFalse(result);
	}

	@Test
	void methodWithArray() throws NoSuchMethodException {
		// given
		Map<String, Mapper<?>> mappers = new HashMap<>();
		IArgumentsMapper mapper = new ArgumentsMapper(mappers);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithArray", String[].class);

		// when
		Executable result = () -> mapper.mapArguments(method, new String[]{"testMethod", "testArgument", "dupa", "dupa"});

		// then
		assertThrows(IllegalArgumentException.class, result);
	}

	@Test
	void methodWithParamArgumentsWithMatchingCommandArguments() throws NoSuchMethodException {
		// given
		Map<String, Mapper<?>> mappers = new HashMap<>();
		mappers.put("testWorldMapper", new Mapper<>(World.class, Bukkit::getWorld));
		IArgumentsMapper mapper = new ArgumentsMapper(mappers);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithParam", World.class);

		// when
		boolean result = mapper.mapArguments(method, new String[]{"testMethod", "AIR"});

		// then
		assertTrue(result);
	}

	@Test
	void methodWithVarArgsWithMatchingCommandArguments() throws NoSuchMethodException {
		// given
		Map<String, Mapper<?>> mappers = new HashMap<>();
		IArgumentsMapper mapper = new ArgumentsMapper(mappers);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithVararg", String[].class);

		// when
		boolean result = mapper.mapArguments(method, new String[]{"testMethod", "test", "test2"});

		// then
		assertTrue(result);
	}

	@Test
	void methodWithVarArgsWithNonMatchingCommandArguments() throws NoSuchMethodException {
		// given
		Map<String, Mapper<?>> mappers = new HashMap<>();
		IArgumentsMapper mapper = new ArgumentsMapper(mappers);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithIntVararg", int[].class);

		// when
		boolean result = mapper.mapArguments(method, new String[]{"testMethod", "test", "1"});

		// then
		assertFalse(result);
	}

	@Test
	void methodWithParamVarArgsWithMatchingCommandArguments() throws NoSuchMethodException {
		// given
		Map<String, Mapper<?>> mappers = new HashMap<>();
		mappers.put("testWorldMapper", new Mapper<>(World.class, Bukkit::getWorld));
		IArgumentsMapper mapper = new ArgumentsMapper(mappers);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithParamVarArg", World[].class);

		// when
		boolean result = mapper.mapArguments(method, new String[]{"testMethod", "world", "world_nether"});

		// then
		assertTrue(result);
	}

	@Test
	void methodWithPrimitiveVarArgsWithMatchingCommandArguments() throws NoSuchMethodException {
		// given
		Map<String, Mapper<?>> mappers = new HashMap<>();
		mappers.put("testWorldMapper", new Mapper<>(World.class, Bukkit::getWorld));
		IArgumentsMapper mapper = new ArgumentsMapper(mappers);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithVararg", String[].class);

		// when
		boolean result = mapper.mapArguments(method, new String[]{"testMethod", "1", "2"});

		// then
		assertFalse(result);
	}

	@Test
	void methodWithMissingParamVarArgsWithMatchingCommandArguments() throws NoSuchMethodException {
		// given
		Map<String, Mapper<?>> mappers = new HashMap<>();
		IArgumentsMapper mapper = new ArgumentsMapper(mappers);
		abstract class LocalFunction implements Function<String, Location> {
		}
		LocalFunction getLocation = mock(LocalFunction.class);
		mappers.put("testWorldMapper", new Mapper<>(Location.class, getLocation));
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithParamVarArg", World[].class);

		// when
		boolean result = mapper.mapArguments(method, new String[]{"testMethod", "world", "world_nether"});

		// then
		assertFalse(result);
	}
}
