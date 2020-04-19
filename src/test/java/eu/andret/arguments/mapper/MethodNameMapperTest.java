/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.mapper.impl.MethodNameMapper;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Method;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class MethodNameMapperTest {
	private final IMethodNameMapper mapper = new MethodNameMapper();

	@Test(expected = IllegalStateException.class)
	public void staticAnnotatedMethodCalled() throws NoSuchMethodException {
		// given
		String[] command = {};
		Method method = TestMethodsProvider.class.getDeclaredMethod("testStaticMethod");

		// when
		boolean result = mapper.mapMethodName(method, command);
	}

	@Test(expected = IllegalArgumentException.class)
	public void methodWithExceededPositionCalled() throws NoSuchMethodException {
		// given
		String[] command = {};
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithExceededPosition");

		// when
		boolean result = mapper.mapMethodName(method, command);
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
