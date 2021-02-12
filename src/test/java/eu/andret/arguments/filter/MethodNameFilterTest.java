/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.filter;

import eu.andret.arguments.filter.impl.MethodNameFilter;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MethodNameFilterTest {
	@Test
	void staticAnnotatedMethodCalled() throws NoSuchMethodException {
		// given
		final String[] command = {};
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testStaticMethod");
		final IMethodNameFilter mapper = new MethodNameFilter();

		// when
		final Executable result = () -> mapper.filterMethodName(method, command);

		// then
		assertThrows(IllegalStateException.class, result);
	}

	@Test
	void methodWithExceededPositionCalled() throws NoSuchMethodException {
		// given
		final String[] command = {};
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithExceededPosition");
		final IMethodNameFilter mapper = new MethodNameFilter();

		// when
		final Executable result = () -> mapper.filterMethodName(method, command);

		// then
		assertThrows(IllegalArgumentException.class, result);
	}

	@Test
	void methodWithoutAnnotation() throws NoSuchMethodException {
		// given
		final String[] command = {};
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithoutAnnotation");
		final IMethodNameFilter mapper = new MethodNameFilter();

		// when
		final boolean result = mapper.filterMethodName(method, command);

		// then
		assertFalse(result);
	}

	@Test
	void method() throws NoSuchMethodException {
		// given
		final String[] command = {"testMethod"};
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethod");
		final IMethodNameFilter mapper = new MethodNameFilter();

		// when
		final boolean result = mapper.filterMethodName(method, command);

		// then
		assertTrue(result);
	}

	@Test
	void methodWithAliases() throws NoSuchMethodException {
		// given
		final String[] command = {"testAlias1"};
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithAliases");
		final IMethodNameFilter mapper = new MethodNameFilter();

		// when
		final boolean result = mapper.filterMethodName(method, command);

		// then
		assertTrue(result);
	}

	@Test
	void methodWithCorrectPosition() throws NoSuchMethodException {
		// given
		final String[] command = {"test", "testMethodWithCorrectPosition"};
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithCorrectPosition", String.class);
		final IMethodNameFilter mapper = new MethodNameFilter();

		// when
		final boolean result = mapper.filterMethodName(method, command);

		// then
		assertTrue(result);
	}
}
