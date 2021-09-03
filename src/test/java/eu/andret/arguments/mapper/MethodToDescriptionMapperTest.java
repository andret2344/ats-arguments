/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.mapper.impl.MethodToDescriptionMapper;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MethodToDescriptionMapperTest {
	static Collection<Object[]> getMappingData() {
		final Object[][] objects = {
				{"testMethod", "testMethod", new Class<?>[0]},
				{"testMethodWithAliases", "<testMethodWithAliases|testAlias1>", new Class<?>[0]},
				{"testMethodWithDescription", "testMethodWithDescription - test description", new Class<?>[0]},
				{"testMethodWithArgument", "testMethodWithArgument <text>", new Class<?>[]{String.class}},
				{"testMethodWithCorrectPosition", "<text> testMethodWithCorrectPosition <text2>", new Class<?>[]{String.class, String.class}},
				{"testMethodWithVararg", "testMethodWithVararg <text...>", new Class<?>[]{String[].class}}
		};
		return Arrays.asList(objects);
	}

	@ParameterizedTest
	@MethodSource("getMappingData")
	void testMappingMethod(final String input, final String output, final Class<?>[] args) throws NoSuchMethodException {
		// given
		final Method method = TestMethodsProvider.class.getDeclaredMethod(input, args);
		final IMethodToDescriptionMapper mapper = new MethodToDescriptionMapper();

		// when
		final String result = mapper.mapMethodToDescription(method, "test");

		// then
		assertEquals("/test " + output, result);
	}
}
