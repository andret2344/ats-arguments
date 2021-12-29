/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.filter;

import eu.andret.arguments.AnnotatedCommand;
import eu.andret.arguments.filter.impl.MethodNameFilter;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MethodNameFilterTest {
	static Iterable<Object[]> data() {
		final Object[][] objects = {
				{false, "testMethod", true},
				{false, "TestMethod", true},
				{true, "testMethod", true},
				{true, "TestMethod", false},
		};
		return Arrays.asList(objects);
	}

	@Test
	void staticAnnotatedMethodCalled() throws NoSuchMethodException {
		// given
		final String[] command = {};
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testStaticMethod");
		final IMethodNameFilter mapper = new MethodNameFilter();
		final AnnotatedCommand.Options options = new AnnotatedCommand.Options();

		// when
		final Executable result = () -> mapper.filterMethodName(method, command, options);

		// then
		assertThrows(IllegalStateException.class, result);
	}

	@Test
	void methodWithExceededPositionCalled() throws NoSuchMethodException {
		// given
		final String[] command = {};
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithExceededPosition");
		final IMethodNameFilter mapper = new MethodNameFilter();
		final AnnotatedCommand.Options options = new AnnotatedCommand.Options();

		// when
		final boolean result = mapper.filterMethodName(method, command, options);

		// then
		assertFalse(result);
	}

	@Test
	void methodWithoutAnnotation() throws NoSuchMethodException {
		// given
		final String[] command = {};
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithoutAnnotation");
		final IMethodNameFilter mapper = new MethodNameFilter();
		final AnnotatedCommand.Options options = new AnnotatedCommand.Options();

		// when
		final boolean result = mapper.filterMethodName(method, command, options);

		// then
		assertFalse(result);
	}

	@Test
	void method() throws NoSuchMethodException {
		// given
		final String[] command = {"testMethod"};
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethod");
		final IMethodNameFilter mapper = new MethodNameFilter();
		final AnnotatedCommand.Options options = new AnnotatedCommand.Options();

		// when
		final boolean result = mapper.filterMethodName(method, command, options);

		// then
		assertTrue(result);
	}

	@Test
	void methodWithAliases() throws NoSuchMethodException {
		// given
		final String[] command = {"testAlias1"};
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithAliases");
		final IMethodNameFilter mapper = new MethodNameFilter();
		final AnnotatedCommand.Options options = new AnnotatedCommand.Options();

		// when
		final boolean result = mapper.filterMethodName(method, command, options);

		// then
		assertTrue(result);
	}

	@Test
	void methodWithCorrectPosition() throws NoSuchMethodException {
		// given
		final String[] command = {"test", "testMethodSecondWithCorrectPosition"};
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodSecondWithCorrectPosition", String.class, String.class);
		final IMethodNameFilter mapper = new MethodNameFilter();
		final AnnotatedCommand.Options options = new AnnotatedCommand.Options();

		// when
		final boolean result = mapper.filterMethodName(method, command, options);

		// then
		assertTrue(result);
	}

	@ParameterizedTest
	@MethodSource("data")
	void methodCaseSensitiveWithIncorrectCase(final boolean caseSensitive, final String command, final boolean expected) throws NoSuchMethodException {
		// given
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethod");
		final IMethodNameFilter mapper = new MethodNameFilter();
		final AnnotatedCommand.Options options = new AnnotatedCommand.Options();
		options.setCaseSensitive(caseSensitive);

		// when
		final boolean result = mapper.filterMethodName(method, new String[]{command}, options);

		// then
		assertEquals(expected, result);
	}
}
