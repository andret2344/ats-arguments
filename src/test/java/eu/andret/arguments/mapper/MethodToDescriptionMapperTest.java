/*
 * Copyright Andret (c) 2018-2020. Copying and modifying allowed only keeping git link reference.
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
		Object[][] objects = {
				{"testMethod", "testMethod", null},
				{"testMethodWithAliases", "<testMethodWithAliases|testAlias1>", null},
				{"testMethodWithDescription", "testMethodWithDescription - test description", null},
				{"testMethodWithArgument", "testMethodWithArgument <text>", String.class},
				{"testMethodWithCorrectPosition", "<text> testMethodWithCorrectPosition", String.class},
				{"testMethodWithVararg", "testMethodWithVararg <text...>", String[].class}
		};
		return Arrays.asList(objects);
	}

	@ParameterizedTest
	@MethodSource("getMappingData")
	void testMappingMethod(String input, String output, Class<?> clazz) throws NoSuchMethodException {
		// given
		Class<?>[] args = clazz == null ? new Class<?>[0] : new Class<?>[]{clazz};
		Method method = TestMethodsProvider.class.getDeclaredMethod(input, args);
		IMethodToDescriptionMapper mapper = new MethodToDescriptionMapper();

		// when
		String result = mapper.mapMethodToDescription(method, "test");

		// then
		assertEquals("/test " + output, result);
	}
}
