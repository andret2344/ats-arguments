/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.Mapper;
import eu.andret.arguments.mapper.impl.ArgumentsMapper;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.Material;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ArgumentsMapperTest {
	@Test
	public void methodWithoutArguments() throws NoSuchMethodException {
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
	public void methodWithArgumentsWithoutCommandArguments() throws NoSuchMethodException {
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
	public void methodWithoutArgumentsWithCommandArguments() throws NoSuchMethodException {
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
	public void methodWithArgumentsWithCommandArguments() throws NoSuchMethodException {
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
	public void methodWithDifferentArgumentsWithMatchingCommandArguments() throws NoSuchMethodException {
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
	public void methodWithDifferentArgumentsWithNonMatchingCommandArguments() throws NoSuchMethodException {
		// given
		Map<String, Mapper<?>> mappers = new HashMap<>();
		IArgumentsMapper mapper = new ArgumentsMapper(mappers);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithMultipleArguments", String.class, int.class, boolean.class);

		// when
		boolean result = mapper.mapArguments(method, new String[]{"testMethod", "testArgument", "dupa", "dupa"});

		// then
		assertFalse(result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void methodWithArray() throws NoSuchMethodException {
		// given
		Map<String, Mapper<?>> mappers = new HashMap<>();
		IArgumentsMapper mapper = new ArgumentsMapper(mappers);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithArray", String[].class);

		// when
		boolean result = mapper.mapArguments(method, new String[]{"testMethod", "testArgument", "dupa", "dupa"});
	}

	@Test
	public void methodWithParamArgumentsWithMatchingCommandArguments() throws NoSuchMethodException {
		// given
		Map<String, Mapper<?>> mappers = new HashMap<>();
		mappers.put("testMaterialMapper", new Mapper<>(Material.class, Material::getMaterial));
		IArgumentsMapper mapper = new ArgumentsMapper(mappers);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithParam", Material.class);

		// when
		boolean result = mapper.mapArguments(method, new String[]{"testMethod", "AIR"});

		// then
		assertTrue(result);
	}

	@Test
	public void methodWithVarArgsWithMatchingCommandArguments() throws NoSuchMethodException {
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
	public void methodWithVarArgsWithNonMatchingCommandArguments() throws NoSuchMethodException {
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
	public void methodWithParamVarArgsWithMatchingCommandArguments() throws NoSuchMethodException {
		// given
		Map<String, Mapper<?>> mappers = new HashMap<>();
		mappers.put("testMaterialMapper", new Mapper<>(Material.class, Material::getMaterial));
		IArgumentsMapper mapper = new ArgumentsMapper(mappers);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithParamVarArg", Material[].class);

		// when
		boolean result = mapper.mapArguments(method, new String[]{"testMethod", "AIR", "DIRT"});

		// then
		assertTrue(result);
	}
}
