package eu.andret.arguments.mapper;

import eu.andret.arguments.entity.ExecutionCall;
import eu.andret.arguments.mapper.impl.ExecutionCallMapper;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.World;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ExecutionCallMapperTest {
	@Test
	void methodWithFallback() throws NoSuchMethodException {
		// given
		IExecutionCallMapper mapper = new ExecutionCallMapper();
		Class<TestMethodsProvider> provider = TestMethodsProvider.class;
		Method methodWorld = provider.getDeclaredMethod("testMethodWithParam", World.class);
		Method methodString = provider.getDeclaredMethod("testMethodWithParam", String.class);

		// when
		ExecutionCall executionCall = mapper.mapExecutionCall(methodWorld, provider.getDeclaredMethods());

		// then
		assertEquals(methodWorld, executionCall.getMethod());
		assertEquals(methodString, executionCall.getFallbackMethod());
	}

	@Test
	void methodWithNoFallback() throws NoSuchMethodException {
		// given
		IExecutionCallMapper mapper = new ExecutionCallMapper();
		Method methodWorld = TestMethodsProvider.class.getDeclaredMethod("testMethod");

		// when
		ExecutionCall executionCall = mapper.mapExecutionCall(methodWorld, TestMethodsProvider.class.getDeclaredMethods());

		// then
		assertEquals(methodWorld, executionCall.getMethod());
		assertNull(executionCall.getFallbackMethod());
	}
}
