/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.Mapper;
import eu.andret.arguments.mapper.impl.CommandToMethodMapper;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.command.CommandSender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(Parameterized.class)
public class CommandToMethodMapperTest {
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

	private final boolean methodNameMapperResult;
	private final boolean executorTypeMapperResult;
	private final boolean argumentsMapperResult;

	public CommandToMethodMapperTest(boolean methodNameMapperResult, boolean executorTypeMapperResult, boolean argumentsMapperResult) {
		this.methodNameMapperResult = methodNameMapperResult;
		this.executorTypeMapperResult = executorTypeMapperResult;
		this.argumentsMapperResult = argumentsMapperResult;
	}

	@Before
	public void setup() {
		initMocks(this);
	}

	@Parameterized.Parameters(name = "{0} && {1} && {2}")
	public static Iterable<Object[]> data() {
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

	@Test
	public void methodCalled() throws NoSuchMethodException {
		// given
		Class<? extends AnnotatedCommandExecutor> executor = TestMethodsProvider.class;
		Method method = executor.getDeclaredMethod("testMethod");
		when(methodNameMapper.mapMethodName(eq(method), any(String[].class))).thenReturn(methodNameMapperResult);
		when(executorTypeMapper.mapExecutorType(eq(method), any(CommandSender.class))).thenReturn(executorTypeMapperResult);
		when(argumentsMapper.mapArguments(eq(method), any(String[].class))).thenReturn(argumentsMapperResult);

		// when
		Optional<Method> result = mapper.mapCommandToMethod(new Method[]{method}, new String[]{"test"}, null);

		// then
		if (methodNameMapperResult && executorTypeMapperResult && argumentsMapperResult) {
			assertEquals(Optional.of(method), result);
		} else {
			assertEquals(Optional.empty(), result);
		}
	}
}
