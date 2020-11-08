/*
 * Copyright Andret (c) 2018-2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.entity.ExecutionCall;
import eu.andret.arguments.entity.Mapper;
import eu.andret.arguments.mapper.impl.CommandToMethodMapper;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

class CommandToMethodMapperTest {
	@Mock
	private IExecutionCallMapper executionCallMapper;
	@Mock
	private IMethodNameMapper methodNameMapper;
	@Mock
	private IExecutorTypeMapper executorTypeMapper;
	@Mock
	private IArgumentsMapper argumentsMapper;
	@Mock
	private Map<String, Mapper<?>> mappers;
	@InjectMocks
	private CommandToMethodMapper mapper;

	@BeforeEach
	void setup() {
		Mockito.mockitoSession()
				.initMocks(this)
				.startMocking()
				.finishMocking();
	}

	static Iterable<Object[]> data() {
		Object[][] objects = {
				{false, false, false},
				{false, false, true},
				{false, true, false},
				{false, true, true},
				{true, false, false},
				{true, false, true},
				{true, true, false},
				{true, true, true},
		};
		return Arrays.asList(objects);
	}

	@ParameterizedTest
	@MethodSource("data")
	void methodCalled(boolean methodNameMapperResult, boolean executorTypeMapperResult, boolean argumentsMapperResult) throws NoSuchMethodException {
		// given
		Class<? extends AnnotatedCommandExecutor<JavaPlugin>> executor = TestMethodsProvider.class;
		Method method = executor.getDeclaredMethod("testMethod");
		CommandSender sender = mock(CommandSender.class);
		lenient().when(executionCallMapper.mapExecutionCall(eq(method), any(Method[].class))).thenReturn(new ExecutionCall(method));
		lenient().when(methodNameMapper.mapMethodName(eq(method), any(String[].class))).thenReturn(methodNameMapperResult);
		lenient().when(executorTypeMapper.mapExecutorType(eq(method), any(CommandSender.class))).thenReturn(executorTypeMapperResult);
		lenient().when(argumentsMapper.mapArguments(eq(method), any(String[].class))).thenReturn(argumentsMapperResult);

		// when
		Optional<ExecutionCall> result = mapper.mapCommandToMethod(new Method[]{method}, new String[]{}, sender);

		// then
		if (methodNameMapperResult && executorTypeMapperResult && argumentsMapperResult) {
			assertEquals(Optional.of(method), result.map(ExecutionCall::getMethod));
		} else {
			assertEquals(Optional.empty(), result);
		}
	}
}
