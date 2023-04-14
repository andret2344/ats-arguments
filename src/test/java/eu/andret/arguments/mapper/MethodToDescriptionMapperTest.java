/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.mapper.impl.MethodToDescriptionMapper;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

// TODO
public class MethodToDescriptionMapperTest {
	@DataProvider(name = "getMappingData")
	static Object[][] getMappingData() {
		return new Object[][]{
				{"testMethod", "testMethod", new Class<?>[0]},
				{"testMethodWithAliases", "<testMethodWithAliases|testAlias1>", new Class<?>[0]},
				{"testMethodWithDescription", "testMethodWithDescription - test description", new Class<?>[0]},
				{"testMethodWithArgument", "testMethodWithArgument <text>", new Class<?>[]{String.class}},
				{"testMethodWithCorrectPosition", "<text> testMethodWithCorrectPosition", new Class<?>[]{String.class}},
				{"testMethodSecondWithCorrectPosition", "<text> testMethodSecondWithCorrectPosition <text2>", new Class<?>[]{String.class, String.class}},
				{"testMethodWithVararg", "testMethodWithVararg <text...>", new Class<?>[]{String[].class}}
		};
	}

	@Test(dataProvider = "getMappingData")
	void testMappingMethod(final String input, final String output, final Class<?>[] args) throws NoSuchMethodException {
		// given
		final Method method = TestMethodsProvider.class.getDeclaredMethod(input, args);
		final IMethodToDescriptionMapper mapper = new MethodToDescriptionMapper();

		// when
		final String result = mapper.mapMethodToDescription(method, "test");

		// then
		assertThat(result).isEqualTo(String.format("/test %s", output));
	}
}
