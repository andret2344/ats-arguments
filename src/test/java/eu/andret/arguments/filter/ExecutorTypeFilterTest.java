package eu.andret.arguments.filter;

import eu.andret.arguments.filter.impl.ExecutorTypeFilter;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ExecutorTypeFilterTest {
	@Test
	void methodWithExecutorTypePlayerCalledByPlayer() throws NoSuchMethodException {
		// given
		final CommandSender commandSender = mock(Player.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithExecutorTypePlayer");
		final IExecutorTypeFilter mapper = new ExecutorTypeFilter();

		// when
		final boolean result = mapper.filterExecutorType(method, commandSender);

		// then
		assertThat(result).isTrue();
	}

	@Test
	void methodWithExecutorTypePlayerCalledByConsole() throws NoSuchMethodException {
		// given
		final CommandSender commandSender = mock(ConsoleCommandSender.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithExecutorTypePlayer");
		final IExecutorTypeFilter mapper = new ExecutorTypeFilter();

		// when
		final boolean result = mapper.filterExecutorType(method, commandSender);

		// then
		assertThat(result).isFalse();
	}

	@Test
	void methodWithExecutorTypeConsoleCalledByPlayer() throws NoSuchMethodException {
		// given
		final CommandSender commandSender = mock(Player.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithExecutorTypeConsole");
		final IExecutorTypeFilter mapper = new ExecutorTypeFilter();

		// when
		final boolean result = mapper.filterExecutorType(method, commandSender);

		// then
		assertThat(result).isFalse();
	}

	@Test
	void methodWithExecutorTypeConsoleCalledByConsole() throws NoSuchMethodException {
		// given
		final CommandSender commandSender = mock(ConsoleCommandSender.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithExecutorTypeConsole");
		final IExecutorTypeFilter mapper = new ExecutorTypeFilter();

		// when
		final boolean result = mapper.filterExecutorType(method, commandSender);

		// then
		assertThat(result).isTrue();
	}

	@Test
	void methodWithExecutorTypeAllCalledByPlayer() throws NoSuchMethodException {
		// given
		final CommandSender commandSender = mock(Player.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithExecutorTypeAll");
		final IExecutorTypeFilter mapper = new ExecutorTypeFilter();

		// when
		final boolean result = mapper.filterExecutorType(method, commandSender);

		// then
		assertThat(result).isTrue();
	}

	@Test
	void methodWithExecutorTypeAllCalledByConsole() throws NoSuchMethodException {
		// given
		final CommandSender commandSender = mock(ConsoleCommandSender.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithExecutorTypeAll");
		final IExecutorTypeFilter mapper = new ExecutorTypeFilter();

		// when
		final boolean result = mapper.filterExecutorType(method, commandSender);

		// then
		assertThat(result).isTrue();
	}
}
