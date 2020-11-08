/*
 * Copyright Andret (c) 2018-2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.mapper.impl.MethodNameMapper;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MethodNameMapperTest {
	@Test
	void staticAnnotatedMethodCalled() throws NoSuchMethodException {
		// given
		String[] command = {};
		Method method = TestMethodsProvider.class.getDeclaredMethod("testStaticMethod");
		IMethodNameMapper mapper = new MethodNameMapper();

		// when
		Executable result = () -> mapper.mapMethodName(method, command);

		// then
		assertThrows(IllegalStateException.class, result);
	}

	@Test
	void methodWithExceededPositionCalled() throws NoSuchMethodException {
		// given
		String[] command = {};
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithExceededPosition");
		IMethodNameMapper mapper = new MethodNameMapper();

		// when
		Executable result = () -> mapper.mapMethodName(method, command);

		// then
		assertThrows(IllegalArgumentException.class, result);
	}

	@Test
	void methodWithoutAnnotation() throws NoSuchMethodException {
		// given
		String[] command = {};
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithoutAnnotation");
		IMethodNameMapper mapper = new MethodNameMapper();

		// when
		boolean result = mapper.mapMethodName(method, command);

		// then
		assertFalse(result);
	}

	@Test
	void method() throws NoSuchMethodException {
		// given
		String[] command = {"testMethod"};
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethod");
		IMethodNameMapper mapper = new MethodNameMapper();

		// when
		boolean result = mapper.mapMethodName(method, command);

		// then
		assertTrue(result);
	}

	@Test
	void methodWithAliases() throws NoSuchMethodException {
		// given
		String[] command = {"testAlias1"};
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithAliases");
		IMethodNameMapper mapper = new MethodNameMapper();

		// when
		boolean result = mapper.mapMethodName(method, command);

		// then
		assertTrue(result);
	}

	@Test
	void methodWithCorrectPosition() throws NoSuchMethodException {
		// given
		String[] command = {"test", "testMethodWithCorrectPosition"};
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithCorrectPosition", String.class);
		IMethodNameMapper mapper = new MethodNameMapper();

		// when
		boolean result = mapper.mapMethodName(method, command);

		// then
		assertTrue(result);
	}
}
