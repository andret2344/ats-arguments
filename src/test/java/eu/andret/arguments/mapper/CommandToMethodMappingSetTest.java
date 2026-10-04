package eu.andret.arguments.mapper;

import eu.andret.arguments.AnnotatedCommand;
import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.filter.IArgumentsFilter;
import eu.andret.arguments.filter.IExecutorTypeFilter;
import eu.andret.arguments.filter.IMethodNameFilter;
import eu.andret.arguments.mapper.impl.CommandToMethodMapper;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@Listeners(MockitoTestNGListener.class)
public class CommandToMethodMappingSetTest {
	@Mock
	private IMethodNameFilter methodNameMapper;
	@Mock
	private IExecutorTypeFilter executorTypeMapper;
	@Mock
	private IArgumentsFilter argumentsMapper;
	@InjectMocks
	private CommandToMethodMapper mapper;

	@DataProvider(name = "getResultsData")
	static Object[][] getResultsData() {
		return new Object[][]{
				{false, false, false},
				{false, false, true},
				{false, true, false},
				{false, true, true},
				{true, false, false},
				{true, false, true},
				{true, true, false},
				{true, true, true},
		};
	}

	@Test(dataProvider = "getResultsData")
	void methodCalled(final boolean methodNameMapperResult, final boolean executorTypeMapperResult, final boolean argumentsMapperResult) throws NoSuchMethodException {
		// given
		final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> executor = TestMethodsProvider.class;
		final Method method = executor.getDeclaredMethod("testMethod");
		final CommandSender sender = mock(CommandSender.class);
		final AnnotatedCommand.Options options = new AnnotatedCommand.Options();
		lenient().when(methodNameMapper.filterMethodName(eq(method), any(String[].class), any(AnnotatedCommand.Options.class))).thenReturn(methodNameMapperResult);
		lenient().when(executorTypeMapper.filterExecutorType(eq(method), any(CommandSender.class))).thenReturn(executorTypeMapperResult);
		lenient().when(argumentsMapper.filterArguments(eq(method), any(String[].class))).thenReturn(argumentsMapperResult);

		// when
		final Optional<Method> result = mapper.mapCommandToMethod(new Method[]{method}, new String[]{}, sender, options);

		// then
		if (methodNameMapperResult && executorTypeMapperResult && argumentsMapperResult) {
			assertThat(result).contains(method);
		} else {
			assertThat(result).isEmpty();
		}
	}
}
