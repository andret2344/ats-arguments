/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.consumer.IResponseConsumer;
import eu.andret.arguments.filter.IPermissionFilter;
import eu.andret.arguments.mapper.ICommandToMethodMapper;
import eu.andret.arguments.mapper.IFallbackSelector;
import eu.andret.arguments.mapper.IInstanceCreator;
import eu.andret.arguments.mapper.IMethodInvoker;
import eu.andret.arguments.mapper.IMethodSelector;
import eu.andret.arguments.mapper.IMethodToDescriptionMapper;
import eu.andret.arguments.provider.TestMethodsProvider;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LocalCommandExecutorTest {
	private interface CommandSenderConsumer extends Consumer<CommandSender> {
	}

	@Test
	void noCommandArguments() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final IMethodToDescriptionMapper mapper = mock(IMethodToDescriptionMapper.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(annotatedCommand, TestMethodsProvider.class, plugin);
		injectMapper(executor, mapper, "methodToDescriptionMapper");
		when(mapper.mapMethodToDescription(any(Method.class), anyString())).thenReturn("/test testString");
		when(command.getName()).thenReturn("test");

		// when
		executor.onCommand(sender, command, "test", new String[0]);

		// then
		verify(sender, times(36)).sendMessage("/test testString");
	}

	@Test
	void noCommandArgumentsWithListener() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final IMethodToDescriptionMapper mapper = mock(IMethodToDescriptionMapper.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(annotatedCommand, TestMethodsProvider.class, plugin);
		injectMapper(executor, mapper, "methodToDescriptionMapper");
		when(mapper.mapMethodToDescription(any(Method.class), anyString())).thenReturn("/test testString");
		when(command.getName()).thenReturn("test");
		final Consumer<CommandSender> onMainCommandExecutionListener = mock(CommandSenderConsumer.class);
		executor.setOnMainCommandExecutionListener(onMainCommandExecutionListener);

		// when
		executor.onCommand(sender, command, "test", new String[0]);

		// then
		verify(onMainCommandExecutionListener, times(1)).accept(sender);
	}

	@Test
	void incorrectArgumentsWithoutListener() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final ICommandToMethodMapper mapper = mock(ICommandToMethodMapper.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(annotatedCommand, TestMethodsProvider.class, plugin);
		when(mapper.mapCommandToMethod(any(), any(), any(), any())).thenReturn(Optional.empty());
		injectMapper(executor, mapper, "commandToMethodMapper");
		when(command.getName()).thenReturn("test");
		final String[] args = {"testMethod"};
		final Consumer<CommandSender> insufficientPermissionsListener = mock(CommandSenderConsumer.class);
		executor.setOnInsufficientPermissionsListener(insufficientPermissionsListener);

		// when
		final boolean result = executor.onCommand(sender, command, "test", args);

		// then
		assertThat(result).isTrue();
		verify(insufficientPermissionsListener, times(0)).accept(any(CommandSender.class));
	}

	@Test
	void incorrectArgumentsWithListener() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final ICommandToMethodMapper mapper = mock(ICommandToMethodMapper.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(annotatedCommand, TestMethodsProvider.class, plugin);
		when(mapper.mapCommandToMethod(any(), any(), any(), any())).thenReturn(Optional.empty());
		injectMapper(executor, mapper, "commandToMethodMapper");
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
		verify(unknownSubCommandExecutionListener, times(1)).accept(sender);
		verify(insufficientPermissionsListener, times(0)).accept(any(CommandSender.class));
	}

	@Test
	void correctArgumentsWithoutPermissionWithoutListener() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final ICommandToMethodMapper methodMapper = mock(ICommandToMethodMapper.class);
		final IPermissionFilter permissionFilter = mock(IPermissionFilter.class);
		final Method method = mock(Method.class);
		when(permissionFilter.filterPermission(method, sender)).thenReturn(false);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(annotatedCommand, TestMethodsProvider.class, plugin);
		when(methodMapper.mapCommandToMethod(any(), any(), any(), any())).thenReturn(Optional.of(method));
		injectMapper(executor, methodMapper, "commandToMethodMapper");
		injectMapper(executor, permissionFilter, "permissionFilter");
		when(command.getName()).thenReturn("test");
		final String[] args = {"testMethod"};
		final Consumer<CommandSender> unknownSubCommandExecutionListener = mock(CommandSenderConsumer.class);
		executor.setOnUnknownSubCommandExecutionListener(unknownSubCommandExecutionListener);

		// when
		final boolean result = executor.onCommand(sender, command, "test", args);

		// then
		assertThat(result).isTrue();
		verify(unknownSubCommandExecutionListener, times(0)).accept(any(CommandSender.class));
	}

	@Test
	void correctArgumentsWithoutPermissionWithListener() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final ICommandToMethodMapper methodMapper = mock(ICommandToMethodMapper.class);
		final IPermissionFilter permissionFilter = mock(IPermissionFilter.class);
		final Method method = mock(Method.class);
		when(permissionFilter.filterPermission(method, sender)).thenReturn(false);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(annotatedCommand, TestMethodsProvider.class, plugin);
		when(methodMapper.mapCommandToMethod(any(), any(), any(), any())).thenReturn(Optional.of(method));
		injectMapper(executor, methodMapper, "commandToMethodMapper");
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
		verify(unknownSubCommandExecutionListener, times(0)).accept(any(CommandSender.class));
		verify(insufficientPermissionsListener, times(1)).accept(sender);
	}

	@Test
	void correctArgumentsWithPermissionWithListener() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final ICommandToMethodMapper methodMapper = mock(ICommandToMethodMapper.class);
		final IPermissionFilter permissionFilter = mock(IPermissionFilter.class);
		final IResponseConsumer responseConsumer = mock(IResponseConsumer.class);
		final IFallbackSelector fallbackSelector = mock(IFallbackSelector.class);
		final IMethodSelector methodSelector = mock(IMethodSelector.class);
		final IInstanceCreator instanceCreator = mock(IInstanceCreator.class);
		final IMethodInvoker methodInvoker = mock(IMethodInvoker.class);
		final Method method = mock(Method.class);
		final Argument argument = mock(Argument.class);
		when(method.getAnnotation(Argument.class)).thenReturn(argument);
		when(permissionFilter.filterPermission(method, sender)).thenReturn(true);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final Class<TestMethodsProvider> providerClass = TestMethodsProvider.class;
		final TestMethodsProvider provider = new TestMethodsProvider(sender, plugin);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(annotatedCommand, providerClass, plugin);
		final Object[] objects = {};
		when(methodSelector.recalculateArguments(eq(method), any(String[].class))).thenReturn(objects);
		when(methodMapper.mapCommandToMethod(any(), any(), any(), any())).thenReturn(Optional.of(method));
		when(instanceCreator.createInstance(sender, plugin, providerClass)).thenReturn(provider);
		when(command.getName()).thenReturn("test");
		when(methodInvoker.invokeMethod(eq(method), any(), any())).thenReturn(List.of("result", "value"));
		injectMapper(executor, methodMapper, "commandToMethodMapper");
		injectMapper(executor, permissionFilter, "permissionFilter");
		injectMapper(executor, fallbackSelector, "fallbackSelector");
		injectMapper(executor, methodSelector, "methodSelector");
		injectMapper(executor, instanceCreator, "instanceCreator");
		injectMapper(executor, responseConsumer, "responseConsumer");
		injectMapper(executor, methodInvoker, "methodInvoker");
		final String[] args = {"testMethod"};
		final Consumer<CommandSender> unknownSubCommandExecutionListener = mock(CommandSenderConsumer.class);
		executor.setOnUnknownSubCommandExecutionListener(unknownSubCommandExecutionListener);
		final Consumer<CommandSender> insufficientPermissionsListener = mock(CommandSenderConsumer.class);
		executor.setOnInsufficientPermissionsListener(insufficientPermissionsListener);

		// when
		final boolean result = executor.onCommand(sender, command, "test", args);

		// then
		assertThat(result).isTrue();
		verify(unknownSubCommandExecutionListener, times(0)).accept(any());
		verify(insufficientPermissionsListener, times(0)).accept(any());
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
	void invokeFallbackMethod() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final Method method = mock(Method.class);
		final Method fallbackMethod = mock(Method.class);
		final ICommandToMethodMapper mapper = mock(ICommandToMethodMapper.class);
		final IPermissionFilter filter = mock(IPermissionFilter.class);
		final IMethodSelector methodSelector = mock(IMethodSelector.class);
		final IFallbackSelector fallbackSelector = mock(IFallbackSelector.class);
		final IInstanceCreator instanceCreator = mock(IInstanceCreator.class);
		final IMethodInvoker methodInvoker = mock(IMethodInvoker.class);
		final IResponseConsumer responseConsumer = mock(IResponseConsumer.class);
		final FallbackException exception = mock(FallbackException.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(annotatedCommand, TestMethodsProvider.class, plugin);
		final TestMethodsProvider provider = new TestMethodsProvider(sender, plugin);
		injectMapper(executor, mapper, "commandToMethodMapper");
		injectMapper(executor, filter, "permissionFilter");
		injectMapper(executor, methodSelector, "methodSelector");
		injectMapper(executor, fallbackSelector, "fallbackSelector");
		injectMapper(executor, instanceCreator, "instanceCreator");
		injectMapper(executor, methodInvoker, "methodInvoker");
		injectMapper(executor, responseConsumer, "responseConsumer");
		when(mapper.mapCommandToMethod(any(), any(), eq(sender), any())).thenReturn(Optional.of(method));
		when(filter.filterPermission(method, sender)).thenReturn(true);
		when(methodSelector.recalculateArguments(any(), any())).thenThrow(exception);
		when(fallbackSelector.selectFallback(any(), any(), any())).thenReturn(List.of(fallbackMethod));
		when(instanceCreator.createInstance(sender, plugin, TestMethodsProvider.class)).thenReturn(provider);
		when(methodInvoker.invokeMethod(eq(fallbackMethod), eq(provider), any())).thenReturn(List.of("test", "result"));

		// when
		executor.onCommand(sender, command, "test", new String[]{"test"});

		// then
		verify(responseConsumer, times(1)).consumeResponse(eq(sender), eq("test"), any());
		verify(responseConsumer, times(1)).consumeResponse(eq(sender), eq("result"), any());
	}

	@Test
	void invokeMethodTwice() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final Method method = mock(Method.class);
		final ICommandToMethodMapper mapper = mock(ICommandToMethodMapper.class);
		final IPermissionFilter filter = mock(IPermissionFilter.class);
		final IMethodSelector methodSelector = mock(IMethodSelector.class);
		final IInstanceCreator instanceCreator = mock(IInstanceCreator.class);
		final IResponseConsumer consumer = mock(IResponseConsumer.class);
		final IMethodInvoker invoker = mock(IMethodInvoker.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(annotatedCommand, TestMethodsProvider.class, plugin);
		final TestMethodsProvider provider = new TestMethodsProvider(sender, plugin);
		final Object[] args = {"abc", "test"};
		injectMapper(executor, mapper, "commandToMethodMapper");
		injectMapper(executor, filter, "permissionFilter");
		injectMapper(executor, methodSelector, "methodSelector");
		injectMapper(executor, instanceCreator, "instanceCreator");
		injectMapper(executor, consumer, "responseConsumer");
		injectMapper(executor, invoker, "methodInvoker");
		when(mapper.mapCommandToMethod(any(), any(), eq(sender), any())).thenReturn(Optional.of(method));
		when(filter.filterPermission(method, sender)).thenReturn(true);
		when(methodSelector.recalculateArguments(any(), any())).thenReturn(args);
		when(instanceCreator.createInstance(sender, plugin, TestMethodsProvider.class)).thenReturn(provider);
		when(invoker.invokeMethod(eq(method), any(), eq(args))).thenReturn(List.of("result", "value"));

		// when
		executor.onCommand(sender, command, "test", new String[]{"abc", "test"});
		executor.onCommand(sender, command, "test", new String[]{"abc", "test"});

		// then
		verify(consumer, times(2)).consumeResponse(eq(sender), eq("result"), any());
		verify(consumer, times(2)).consumeResponse(eq(sender), eq("value"), any());
	}

	@Test
	void invokeMethodWithException() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final Method method = mock(Method.class);
		final ICommandToMethodMapper mapper = mock(ICommandToMethodMapper.class);
		final IPermissionFilter filter = mock(IPermissionFilter.class);
		final IMethodSelector methodSelector = mock(IMethodSelector.class);
		final IInstanceCreator instanceCreator = mock(IInstanceCreator.class);
		final IMethodInvoker methodInvoker = mock(IMethodInvoker.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(annotatedCommand, TestMethodsProvider.class, plugin);
		final TestMethodsProvider provider = new TestMethodsProvider(sender, plugin);
		injectMapper(executor, mapper, "commandToMethodMapper");
		injectMapper(executor, filter, "permissionFilter");
		injectMapper(executor, methodSelector, "methodSelector");
		injectMapper(executor, instanceCreator, "instanceCreator");
		injectMapper(executor, methodInvoker, "methodInvoker");
		when(mapper.mapCommandToMethod(any(), any(), eq(sender), any())).thenReturn(Optional.of(method));
		when(filter.filterPermission(method, sender)).thenReturn(true);
		final String[] result = {"one", "two"};
		when(methodSelector.recalculateArguments(any(), any())).thenReturn(result);
		when(instanceCreator.createInstance(sender, plugin, TestMethodsProvider.class)).thenReturn(provider);

		// when
		executor.onCommand(sender, command, "test", new String[]{"test"});

		// then
		verify(methodInvoker).invokeMethod(eq(method), any(), eq(result));
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
