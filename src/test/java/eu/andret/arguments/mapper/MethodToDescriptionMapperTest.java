/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.mapper.impl.MethodToDescriptionMapper;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MethodToDescriptionMapperTest {
	private final IMethodToDescriptionMapper mapper = new MethodToDescriptionMapper();

	@Test
	void testMappingPureMethod() throws NoSuchMethodException {
		// given
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethod");

		// when
		String result = mapper.mapMethodToDescription(method, "test");

		// then
		assertEquals("/test testMethod", result);
	}

	@Test
	void testMappingMethodWithAlias() throws NoSuchMethodException {
		// given
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithAliases");

		// when
		String result = mapper.mapMethodToDescription(method, "test");

		// then
		assertEquals("/test <testMethodWithAliases|testAlias1>", result);
	}

	@Test
	void testMappingMethodWithArgument() throws NoSuchMethodException {
		// given
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithArgument", String.class);

		// when
		String result = mapper.mapMethodToDescription(method, "test");

		// then
		assertEquals("/test testMethodWithArgument <text>", result);
	}

	@Test
	void testMappingMethodWithArgumentAndPosition() throws NoSuchMethodException {
		// given
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithCorrectPosition", String.class);

		// when
		String result = mapper.mapMethodToDescription(method, "test");

		// then
		assertEquals("/test <text> testMethodWithCorrectPosition", result);
	}

	@Test
	void testMappingMethodWithVararg() throws NoSuchMethodException {
		// given
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithVararg", String[].class);

		// when
		String result = mapper.mapMethodToDescription(method, "test");

		// then
		assertEquals("/test testMethodWithVararg <text...>", result);
	}

	@Test
	void testMappingMethodWithDescription() throws NoSuchMethodException {
		// given
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithDescription");

		// when
		String result = mapper.mapMethodToDescription(method, "test");

		// then
		assertEquals("/test testMethodWithDescription - test description", result);
	}
}
