/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
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

public class MethodNameMapperTest {
	private final IMethodNameMapper mapper = new MethodNameMapper();

	@Test
	public void staticAnnotatedMethodCalled() throws NoSuchMethodException {
		// given
		String[] command = {};
		Method method = TestMethodsProvider.class.getDeclaredMethod("testStaticMethod");

		// when
		Executable result = () -> mapper.mapMethodName(method, command);

		// then
		assertThrows(IllegalStateException.class, result);
	}

	@Test
	public void methodWithExceededPositionCalled() throws NoSuchMethodException {
		// given
		String[] command = {};
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithExceededPosition");

		// when
		Executable result = () -> mapper.mapMethodName(method, command);

		// then
		assertThrows(IllegalArgumentException.class, result);
	}

	@Test
	public void methodWithoutAnnotation() throws NoSuchMethodException {
		// given
		String[] command = {};
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithoutAnnotation");

		// when
		boolean result = mapper.mapMethodName(method, command);

		// then
		assertFalse(result);
	}

	@Test
	public void method() throws NoSuchMethodException {
		// given
		String[] command = {"testMethod"};
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethod");

		// when
		boolean result = mapper.mapMethodName(method, command);

		// then
		assertTrue(result);
	}

	@Test
	public void methodWithAliases() throws NoSuchMethodException {
		// given
		String[] command = {"testAlias1"};
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithAliases");

		// when
		boolean result = mapper.mapMethodName(method, command);

		// then
		assertTrue(result);
	}

	@Test
	public void methodWithCorrectPosition() throws NoSuchMethodException {
		// given
		String[] command = {"test", "testMethodWithCorrectPosition"};
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithCorrectPosition", String.class);

		// when
		boolean result = mapper.mapMethodName(method, command);

		// then
		assertTrue(result);
	}
}
