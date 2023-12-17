/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.filter.IPermissionFilter;
import eu.andret.arguments.mapper.ICommandToMethodMapper;
import eu.andret.arguments.mapper.IExceptionHandler;
import eu.andret.arguments.mapper.IFallbackSelector;
import eu.andret.arguments.mapper.IInstanceCreator;
import eu.andret.arguments.mapper.IMethodSelector;
import eu.andret.arguments.mapper.IMethodToDescriptionMapper;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LocalCommandExecutorTest {
	@FunctionalInterface
	private interface CommandSenderConsumer extends Consumer<CommandSender> {
	}

	@Test
	void noCommandArguments() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final IMethodToDescriptionMapper methodToDescriptionMapper = mock(IMethodToDescriptionMapper.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(annotatedCommand, TestMethodsProvider.class, plugin);
		injectMapper(executor, methodToDescriptionMapper, "methodToDescriptionMapper");
		when(methodToDescriptionMapper.mapMethodToDescription(any(Method.class), anyString())).thenReturn("/test testString");
		when(command.getName()).thenReturn("test");

		// when
		executor.onCommand(sender, command, "test", new String[0]);

		// then
		verify(sender, times(38)).sendMessage("/test testString");
	}

	@Test
	void noCommandArgumentsWithListener() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final IMethodToDescriptionMapper methodToDescriptionMapper = mock(IMethodToDescriptionMapper.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(annotatedCommand, TestMethodsProvider.class, plugin);
		injectMapper(executor, methodToDescriptionMapper, "methodToDescriptionMapper");
		when(methodToDescriptionMapper.mapMethodToDescription(any(Method.class), anyString())).thenReturn("/test testString");
		when(command.getName()).thenReturn("test");
		final Consumer<CommandSender> onMainCommandExecutionListener = mock(CommandSenderConsumer.class);
		executor.setOnMainCommandExecutionListener(onMainCommandExecutionListener);
		annotatedCommand.getOptions().setAutoTranslateColors(true);

		// when
		executor.onCommand(sender, command, "test", new String[0]);

		// then
		verify(onMainCommandExecutionListener).accept(any(CommandSender.class));
	}

	@Test
	void incorrectArgumentsWithoutListener() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final ICommandToMethodMapper commandToMethodMapper = mock(ICommandToMethodMapper.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(annotatedCommand, TestMethodsProvider.class, plugin);
		when(commandToMethodMapper.mapCommandToMethod(any(), any(), any(), any())).thenReturn(Optional.empty());
		injectMapper(executor, commandToMethodMapper, "commandToMethodMapper");
		when(command.getName()).thenReturn("test");
		final String[] args = {"testMethod"};
		final Consumer<CommandSender> insufficientPermissionsListener = mock(CommandSenderConsumer.class);
		executor.setOnInsufficientPermissionsListener(insufficientPermissionsListener);

		// when
		final boolean result = executor.onCommand(sender, command, "test", args);

		// then
		assertThat(result).isTrue();
		verify(insufficientPermissionsListener, never()).accept(any(CommandSender.class));
	}

	@Test
	void incorrectArgumentsWithListener() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final ICommandToMethodMapper commandToMethodMapper = mock(ICommandToMethodMapper.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(annotatedCommand, TestMethodsProvider.class, plugin);
		when(commandToMethodMapper.mapCommandToMethod(any(), any(), any(), any())).thenReturn(Optional.empty());
		injectMapper(executor, commandToMethodMapper, "commandToMethodMapper");
		when(command.getName()).thenReturn("test");
		final String[] args = {"testMethod"};
		final Consumer<CommandSender> unknownSubCommandExecutionListener = mock(CommandSenderConsumer.class);
		executor.setOnUnknownSubCommandExecutionListener(unknownSubCommandExecutionListener);
		final Consumer<CommandSender> insufficientPermissionsListener = mock(CommandSenderConsumer.class);
		executor.setOnInsufficientPermissionsListener(insufficientPermissionsListener);

		// when
		final boolean result = executor.onCommand(sender, command, "test", args);

		// then
		assertThat(result).isTrue();
		verify(unknownSubCommandExecutionListener).accept(sender);
		verify(insufficientPermissionsListener, never()).accept(any(CommandSender.class));
	}

	@Test
	void correctArgumentsWithoutPermissionWithoutListener() throws NoSuchMethodException {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final ICommandToMethodMapper commandToMethodMapper = mock(ICommandToMethodMapper.class);
		final IPermissionFilter permissionFilter = mock(IPermissionFilter.class);
		final Class<TestMethodsProvider> testMethodsProviderClass = TestMethodsProvider.class;
		final Method method = testMethodsProviderClass.getDeclaredMethod("testMethod");
		when(permissionFilter.filterPermission(method, sender)).thenReturn(false);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(annotatedCommand, testMethodsProviderClass, plugin);
		when(commandToMethodMapper.mapCommandToMethod(any(), any(), any(), any())).thenReturn(Optional.of(method));
		injectMapper(executor, commandToMethodMapper, "commandToMethodMapper");
		injectMapper(executor, permissionFilter, "permissionFilter");
		when(command.getName()).thenReturn("test");
		final String[] args = {"testMethod"};
		final Consumer<CommandSender> unknownSubCommandExecutionListener = mock(CommandSenderConsumer.class);
		executor.setOnUnknownSubCommandExecutionListener(unknownSubCommandExecutionListener);

		// when
		final boolean result = executor.onCommand(sender, command, "test", args);

		// then
		assertThat(result).isTrue();
		verify(unknownSubCommandExecutionListener, never()).accept(any(CommandSender.class));
	}

	@Test
	void correctArgumentsWithoutPermissionWithListener() throws NoSuchMethodException {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final ICommandToMethodMapper commandToMethodMapper = mock(ICommandToMethodMapper.class);
		final IPermissionFilter permissionFilter = mock(IPermissionFilter.class);
		final Class<TestMethodsProvider> testMethodsProviderClass = TestMethodsProvider.class;
		final Method method = testMethodsProviderClass.getDeclaredMethod("testMethod");
		when(permissionFilter.filterPermission(method, sender)).thenReturn(false);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(annotatedCommand, testMethodsProviderClass, plugin);
		when(commandToMethodMapper.mapCommandToMethod(any(), any(), any(), any())).thenReturn(Optional.of(method));
		injectMapper(executor, commandToMethodMapper, "commandToMethodMapper");
		injectMapper(executor, permissionFilter, "permissionFilter");
		when(command.getName()).thenReturn("test");
		final String[] args = {"testMethod"};
		final Consumer<CommandSender> unknownSubCommandExecutionListener = mock(CommandSenderConsumer.class);
		executor.setOnUnknownSubCommandExecutionListener(unknownSubCommandExecutionListener);
		final Consumer<CommandSender> insufficientPermissionsListener = mock(CommandSenderConsumer.class);
		executor.setOnInsufficientPermissionsListener(insufficientPermissionsListener);

		// when
		final boolean result = executor.onCommand(sender, command, "test", args);

		// then
		assertThat(result).isTrue();
		verify(unknownSubCommandExecutionListener, never()).accept(any(CommandSender.class));
		verify(insufficientPermissionsListener).accept(sender);
	}

	@Test
	void correctArgumentsWithPermissionWithListener() throws NoSuchMethodException {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final ICommandToMethodMapper commandToMethodMapper = mock(ICommandToMethodMapper.class);
		final IPermissionFilter permissionFilter = mock(IPermissionFilter.class);
		final IFallbackSelector fallbackSelector = mock(IFallbackSelector.class);
		final IMethodSelector methodSelector = mock(IMethodSelector.class);
		final IInstanceCreator instanceCreator = mock(IInstanceCreator.class);
		final IExceptionHandler exceptionHandler = mock(IExceptionHandler.class);
		final Class<TestMethodsProvider> providerClass = TestMethodsProvider.class;
		final Method method = providerClass.getDeclaredMethod("testMethod");
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final TestMethodsProvider provider = new TestMethodsProvider(sender, plugin);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(annotatedCommand, providerClass, plugin);
		final Object[] objects = {};

		// and
		when(permissionFilter.filterPermission(method, sender)).thenReturn(true);
		when(methodSelector.recalculateArguments(eq(method), any(String[].class))).thenReturn(objects);
		when(commandToMethodMapper.mapCommandToMethod(any(), any(), any(), any())).thenReturn(Optional.of(method));
		when(instanceCreator.createInstance(sender, plugin, providerClass)).thenReturn(provider);
		when(command.getName()).thenReturn("test");
		when(exceptionHandler.handleException(eq(method), any(), any(), eq(TestMethodsProvider.class.getDeclaredMethods())))
				.thenReturn(List.of("result", "value"));
		injectMapper(executor, commandToMethodMapper, "commandToMethodMapper");
		injectMapper(executor, permissionFilter, "permissionFilter");
		injectMapper(executor, fallbackSelector, "fallbackSelector");
		injectMapper(executor, methodSelector, "methodSelector");
		injectMapper(executor, instanceCreator, "instanceCreator");
		injectMapper(executor, exceptionHandler, "exceptionHandler");
		final String[] args = {"testMethod"};
		final Consumer<CommandSender> unknownSubCommandExecutionListener = mock(CommandSenderConsumer.class);
		executor.setOnUnknownSubCommandExecutionListener(unknownSubCommandExecutionListener);
		final Consumer<CommandSender> insufficientPermissionsListener = mock(CommandSenderConsumer.class);
		executor.setOnInsufficientPermissionsListener(insufficientPermissionsListener);

		// when
		final boolean result = executor.onCommand(sender, command, "test", args);

		// then
		assertThat(result).isTrue();
		verify(unknownSubCommandExecutionListener, never()).accept(any());
		verify(insufficientPermissionsListener, never()).accept(any());
	}

	@Test
	void getNullCommandExecutor() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final PluginCommand command = mock(PluginCommand.class);
		final CommandSender sender = mock(CommandSender.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(annotatedCommand, TestMethodsProvider.class, plugin);

		// when
		final AnnotatedCommandExecutor<JavaPlugin> commandExecutor = executor.getCommandExecutor(sender);

		// then
		assertThat(commandExecutor).isNull();
	}

	@Test
	void invokeFallbackMethod() throws NoSuchMethodException {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final Class<TestMethodsProvider> testMethodsProviderClass = TestMethodsProvider.class;
		final Method method = testMethodsProviderClass.getDeclaredMethod("testMethodWithParam", World.class);
		final Method fallbackMethod = testMethodsProviderClass.getDeclaredMethod("testMethodArgumentFallback", String.class);
		final ICommandToMethodMapper commandToMethodMapper = mock(ICommandToMethodMapper.class);
		final IPermissionFilter filter = mock(IPermissionFilter.class);
		final IMethodSelector methodSelector = mock(IMethodSelector.class);
		final IFallbackSelector fallbackSelector = mock(IFallbackSelector.class);
		final IInstanceCreator instanceCreator = mock(IInstanceCreator.class);
		final IExceptionHandler exceptionHandler = mock(IExceptionHandler.class);
		final FallbackException exception = mock(FallbackException.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(annotatedCommand, testMethodsProviderClass, plugin);
		final TestMethodsProvider provider = new TestMethodsProvider(sender, plugin);
		injectMapper(executor, commandToMethodMapper, "commandToMethodMapper");
		injectMapper(executor, filter, "permissionFilter");
		injectMapper(executor, methodSelector, "methodSelector");
		injectMapper(executor, fallbackSelector, "fallbackSelector");
		injectMapper(executor, instanceCreator, "instanceCreator");
		injectMapper(executor, exceptionHandler, "exceptionHandler");

		// and
		when(commandToMethodMapper.mapCommandToMethod(any(), any(), eq(sender), any())).thenReturn(Optional.of(method));
		when(filter.filterPermission(method, sender)).thenReturn(true);
		when(methodSelector.recalculateArguments(any(), any())).thenThrow(exception);
		when(fallbackSelector.selectFallback(any(), any(), any())).thenReturn(List.of(fallbackMethod));
		when(instanceCreator.createInstance(sender, plugin, testMethodsProviderClass)).thenReturn(provider);
		when(exceptionHandler.handleException(eq(fallbackMethod), eq(provider), any(), eq(TestMethodsProvider.class.getDeclaredMethods())))
				.thenReturn(List.of("test", "result"));

		// when
		executor.onCommand(sender, command, "test", new String[]{"test"});

		// then
		verify(sender).sendMessage("test");
		verify(sender).sendMessage("result");
	}

	@Test
	void invokeMethodTwice() throws NoSuchMethodException {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final Class<TestMethodsProvider> testMethodsProviderClass = TestMethodsProvider.class;
		final Method method = testMethodsProviderClass.getDeclaredMethod("testMethod");
		final ICommandToMethodMapper commandToMethodMapper = mock(ICommandToMethodMapper.class);
		final IPermissionFilter permissionFilter = mock(IPermissionFilter.class);
		final IMethodSelector methodSelector = mock(IMethodSelector.class);
		final IInstanceCreator instanceCreator = mock(IInstanceCreator.class);
		final IExceptionHandler exceptionHandler = mock(IExceptionHandler.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(annotatedCommand, testMethodsProviderClass, plugin);
		final TestMethodsProvider provider = new TestMethodsProvider(sender, plugin);
		final Object[] args = {"abc", "test"};
		injectMapper(executor, commandToMethodMapper, "commandToMethodMapper");
		injectMapper(executor, permissionFilter, "permissionFilter");
		injectMapper(executor, methodSelector, "methodSelector");
		injectMapper(executor, instanceCreator, "instanceCreator");
		injectMapper(executor, exceptionHandler, "exceptionHandler");

		// and
		when(commandToMethodMapper.mapCommandToMethod(any(), any(), eq(sender), any())).thenReturn(Optional.of(method));
		when(permissionFilter.filterPermission(method, sender)).thenReturn(true);
		when(methodSelector.recalculateArguments(any(), any())).thenReturn(args);
		when(instanceCreator.createInstance(sender, plugin, testMethodsProviderClass)).thenReturn(provider);
		when(exceptionHandler.handleException(eq(method), eq(provider), any(), eq(TestMethodsProvider.class.getDeclaredMethods())))
				.thenReturn(List.of("result", "value"));

		// when
		executor.onCommand(sender, command, "test", new String[]{"abc", "test"});
		executor.onCommand(sender, command, "test", new String[]{"abc", "test"});

		// then
		verify(sender, times(2)).sendMessage("result");
		verify(sender, times(2)).sendMessage("value");
	}

	@Test
	void invokeMethodWithException() throws NoSuchMethodException {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final Class<TestMethodsProvider> testMethodsProviderClass = TestMethodsProvider.class;
		final Method method = testMethodsProviderClass.getDeclaredMethod("testMethod");
		final ICommandToMethodMapper commandToMethodMapper = mock(ICommandToMethodMapper.class);
		final IPermissionFilter permissionFilter = mock(IPermissionFilter.class);
		final IMethodSelector methodSelector = mock(IMethodSelector.class);
		final IInstanceCreator instanceCreator = mock(IInstanceCreator.class);
		final IExceptionHandler exceptionHandler = mock(IExceptionHandler.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(annotatedCommand, testMethodsProviderClass, plugin);
		final TestMethodsProvider provider = new TestMethodsProvider(sender, plugin);
		injectMapper(executor, commandToMethodMapper, "commandToMethodMapper");
		injectMapper(executor, permissionFilter, "permissionFilter");
		injectMapper(executor, methodSelector, "methodSelector");
		injectMapper(executor, instanceCreator, "instanceCreator");
		injectMapper(executor, exceptionHandler, "exceptionHandler");
		final String[] result = {"one", "two"};

		// and
		when(commandToMethodMapper.mapCommandToMethod(any(), any(), eq(sender), any())).thenReturn(Optional.of(method));
		when(permissionFilter.filterPermission(method, sender)).thenReturn(true);
		when(methodSelector.recalculateArguments(any(), any())).thenReturn(result);
		when(instanceCreator.createInstance(sender, plugin, testMethodsProviderClass)).thenReturn(provider);

		// when
		executor.onCommand(sender, command, "test", new String[]{"test"});

		// then
		verify(exceptionHandler).handleException(method, provider, result, TestMethodsProvider.class.getDeclaredMethods());
	}

	private void injectMapper(final LocalCommandExecutor<JavaPlugin> executor, final IMapper mapper, final String mapperName) {
		try {
			final Field field = executor.getClass().getDeclaredField(mapperName);
			field.setAccessible(true);
			field.set(executor, mapper);
		} catch (final ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}
}
