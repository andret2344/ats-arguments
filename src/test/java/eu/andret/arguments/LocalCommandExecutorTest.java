/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.AnnotatedCommand.OnInsufficientPermissionsListener;
import eu.andret.arguments.AnnotatedCommand.OnMainCommandExecutionListener;
import eu.andret.arguments.AnnotatedCommand.OnUnknownSubCommandExecutionListener;
import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.api.entity.FallbackConstants;
import eu.andret.arguments.consumer.IResponseConsumer;
import eu.andret.arguments.entity.MappingSet;
import eu.andret.arguments.filter.IPermissionFilter;
import eu.andret.arguments.local.LocalFallbackSelector;
import eu.andret.arguments.local.LocalInstanceCreator;
import eu.andret.arguments.local.LocalMethodSelector;
import eu.andret.arguments.mapper.ICommandToMethodMapper;
import eu.andret.arguments.mapper.IFallbackSelector;
import eu.andret.arguments.mapper.IInstanceCreator;
import eu.andret.arguments.mapper.IMapper;
import eu.andret.arguments.mapper.IMethodSelector;
import eu.andret.arguments.mapper.IMethodToDescriptionMapper;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LocalCommandExecutorTest {
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
		verify(sender, times(27)).sendMessage("/test testString");
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
		final OnMainCommandExecutionListener onMainCommandExecutionListener = mock(OnMainCommandExecutionListener.class);
		executor.setOnMainCommandExecutionListener(onMainCommandExecutionListener);

		// when
		executor.onCommand(sender, command, "test", new String[0]);

		// then
		verify(onMainCommandExecutionListener, times(1)).mainCommandExecution(sender);
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
		final OnInsufficientPermissionsListener insufficientPermissionsListener = mock(OnInsufficientPermissionsListener.class);
		executor.setOnInsufficientPermissionsListener(insufficientPermissionsListener);

		// when
		final boolean result = executor.onCommand(sender, command, "test", args);

		// then
		assertTrue(result);
		verify(insufficientPermissionsListener, times(0)).insufficientPermissions(any(CommandSender.class));
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
		final OnUnknownSubCommandExecutionListener unknownSubCommandExecutionListener = mock(OnUnknownSubCommandExecutionListener.class);
		executor.setOnUnknownSubCommandExecutionListener(unknownSubCommandExecutionListener);
		final OnInsufficientPermissionsListener insufficientPermissionsListener = mock(OnInsufficientPermissionsListener.class);
		executor.setOnInsufficientPermissionsListener(insufficientPermissionsListener);

		// when
		final boolean result = executor.onCommand(sender, command, "test", args);

		// then
		assertTrue(result);
		verify(unknownSubCommandExecutionListener, times(1)).unknownSubCommandExecuted(sender);
		verify(insufficientPermissionsListener, times(0)).insufficientPermissions(any(CommandSender.class));
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
		final OnUnknownSubCommandExecutionListener unknownSubCommandExecutionListener = mock(OnUnknownSubCommandExecutionListener.class);
		executor.setOnUnknownSubCommandExecutionListener(unknownSubCommandExecutionListener);

		// when
		final boolean result = executor.onCommand(sender, command, "test", args);

		// then
		assertTrue(result);
		verify(unknownSubCommandExecutionListener, times(0)).unknownSubCommandExecuted(any(CommandSender.class));
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
		final OnUnknownSubCommandExecutionListener unknownSubCommandExecutionListener = mock(OnUnknownSubCommandExecutionListener.class);
		executor.setOnUnknownSubCommandExecutionListener(unknownSubCommandExecutionListener);
		final OnInsufficientPermissionsListener insufficientPermissionsListener = mock(OnInsufficientPermissionsListener.class);
		executor.setOnInsufficientPermissionsListener(insufficientPermissionsListener);

		// when
		final boolean result = executor.onCommand(sender, command, "test", args);

		// then
		assertTrue(result);
		verify(unknownSubCommandExecutionListener, times(0)).unknownSubCommandExecuted(any(CommandSender.class));
		verify(insufficientPermissionsListener, times(1)).insufficientPermissions(sender);
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
		final IFallbackSelector<JavaPlugin> fallbackSelector = mock(LocalFallbackSelector.class);
		final IMethodSelector methodSelector = mock(LocalMethodSelector.class);
		final IInstanceCreator<JavaPlugin> instanceCreator = mock(LocalInstanceCreator.class);
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
		injectMapper(executor, methodMapper, "commandToMethodMapper");
		injectMapper(executor, permissionFilter, "permissionFilter");
		injectMapper(executor, fallbackSelector, "fallbackSelector");
		injectMapper(executor, methodSelector, "methodSelector");
		injectMapper(executor, instanceCreator, "instanceCreator");
		injectMapper(executor, responseConsumer, "responseConsumer");
		final String[] args = {"testMethod"};
		final OnUnknownSubCommandExecutionListener unknownSubCommandExecutionListener = mock(OnUnknownSubCommandExecutionListener.class);
		executor.setOnUnknownSubCommandExecutionListener(unknownSubCommandExecutionListener);
		final OnInsufficientPermissionsListener insufficientPermissionsListener = mock(OnInsufficientPermissionsListener.class);
		executor.setOnInsufficientPermissionsListener(insufficientPermissionsListener);

		// when
		final boolean result = executor.onCommand(sender, command, "test", args);

		// then
		assertTrue(result);
		verify(unknownSubCommandExecutionListener, times(0)).unknownSubCommandExecuted(any());
		verify(insufficientPermissionsListener, times(0)).insufficientPermissions(any());
	}

	@Test
	void addArgumentMapper() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final PluginCommand command = mock(PluginCommand.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(annotatedCommand, TestMethodsProvider.class, plugin);

		// when
		final boolean result1 = executor.addArgumentMapper("test1", new MappingSet<>(World.class, Bukkit::getWorld, FallbackConstants.NEVER));
		final boolean result2 = executor.addArgumentMapper("test1", new MappingSet<>(World.class, Bukkit::getWorld, FallbackConstants.NEVER));
		final boolean result3 = executor.addArgumentMapper("test3", new MappingSet<>(World.class, Bukkit::getWorld, FallbackConstants.NEVER));

		// then
		assertTrue(result1);
		assertFalse(result2);
		assertTrue(result3);
	}

	@Test
	void addTypeMapper() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final PluginCommand command = mock(PluginCommand.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(annotatedCommand, TestMethodsProvider.class, plugin);

		// when
		final boolean result1 = executor.addTypeMapper(World.class, new MappingSet<>(World.class, Bukkit::getWorld, FallbackConstants.NEVER));
		final boolean result2 = executor.addTypeMapper(World.class, new MappingSet<>(World.class, Bukkit::getWorld, FallbackConstants.NEVER));
		final boolean result3 = executor.addTypeMapper(boolean.class, new MappingSet<>(boolean.class, Boolean::parseBoolean, FallbackConstants.NEVER));

		// then
		assertTrue(result1);
		assertFalse(result2);
		assertTrue(result3);
	}

	@Test
	void getNullCommandExecutor() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final PluginCommand command = mock(PluginCommand.class);
		final CommandSender sender = mock(CommandSender.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(annotatedCommand, TestMethodsProvider.class, plugin);

		final AnnotatedCommandExecutor<JavaPlugin> commandExecutor = executor.getCommandExecutor(sender);

		assertNull(commandExecutor);
	}

	@Test
	void invokeFallbackMethod() throws InvocationTargetException, IllegalAccessException {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final Method method = mock(Method.class);
		final Method fallbackMethod = mock(Method.class);
		final ICommandToMethodMapper mapper = mock(ICommandToMethodMapper.class);
		final IPermissionFilter filter = mock(IPermissionFilter.class);
		final IMethodSelector methodSelector = mock(IMethodSelector.class);
		final IFallbackSelector<JavaPlugin> fallbackSelector = mock(LocalFallbackSelector.class);
		final IInstanceCreator<JavaPlugin> instanceCreator = mock(LocalInstanceCreator.class);
		final FallbackException exception = mock(FallbackException.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(annotatedCommand, TestMethodsProvider.class, plugin);
		final TestMethodsProvider provider = new TestMethodsProvider(sender, plugin);
		injectMapper(executor, mapper, "commandToMethodMapper");
		injectMapper(executor, filter, "permissionFilter");
		injectMapper(executor, methodSelector, "methodSelector");
		injectMapper(executor, fallbackSelector, "fallbackSelector");
		injectMapper(executor, instanceCreator, "instanceCreator");
		when(mapper.mapCommandToMethod(any(), any(), eq(sender), any())).thenReturn(Optional.of(method));
		when(filter.filterPermission(method, sender)).thenReturn(true);
		when(methodSelector.recalculateArguments(any(), any())).thenThrow(exception);
		when(fallbackSelector.selectFallback(any(), any(), any())).thenReturn(List.of(fallbackMethod));
		when(instanceCreator.createInstance(sender, plugin, TestMethodsProvider.class)).thenReturn(provider);

		// when
		executor.onCommand(sender, command, "test", new String[]{"test"});

		// then
		verify(method, times(0)).invoke(any(), any());
		verify(fallbackMethod, times(1)).invoke(eq(provider), any());
	}

	@Test
	void invokeMethodTwice() throws InvocationTargetException, IllegalAccessException {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final Method method = mock(Method.class);
		final ICommandToMethodMapper mapper = mock(ICommandToMethodMapper.class);
		final IPermissionFilter filter = mock(IPermissionFilter.class);
		final IMethodSelector methodSelector = mock(IMethodSelector.class);
		final IInstanceCreator<JavaPlugin> instanceCreator = mock(LocalInstanceCreator.class);
		final IResponseConsumer consumer = mock(IResponseConsumer.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(annotatedCommand, TestMethodsProvider.class, plugin);
		final TestMethodsProvider provider = new TestMethodsProvider(sender, plugin);
		injectMapper(executor, mapper, "commandToMethodMapper");
		injectMapper(executor, filter, "permissionFilter");
		injectMapper(executor, methodSelector, "methodSelector");
		injectMapper(executor, instanceCreator, "instanceCreator");
		injectMapper(executor, consumer, "responseConsumer");
		when(mapper.mapCommandToMethod(any(), any(), eq(sender), any())).thenReturn(Optional.of(method));
		when(filter.filterPermission(method, sender)).thenReturn(true);
		when(methodSelector.recalculateArguments(any(), any())).thenReturn(new Object[]{"abc", "test"});
		when(instanceCreator.createInstance(sender, plugin, TestMethodsProvider.class)).thenReturn(provider);
		when(method.invoke(any(), any())).thenReturn("test");

		// when
		executor.onCommand(sender, command, "test", new String[]{"test"});
		executor.onCommand(sender, command, "test", new String[]{"test"});

		// then
		verify(method, times(2)).invoke(any(), any());
		verify(consumer, times(2)).consumeResponse(eq(sender), any(), any());
	}

	@Test
	void invokeMethodWithException() throws InvocationTargetException, IllegalAccessException {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final CommandSender sender = mock(CommandSender.class);
		final PluginCommand command = mock(PluginCommand.class);
		final Method method = mock(Method.class);
		final ICommandToMethodMapper mapper = mock(ICommandToMethodMapper.class);
		final IPermissionFilter filter = mock(IPermissionFilter.class);
		final IMethodSelector methodSelector = mock(IMethodSelector.class);
		final IInstanceCreator<JavaPlugin> instanceCreator = mock(LocalInstanceCreator.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(annotatedCommand, TestMethodsProvider.class, plugin);
		final TestMethodsProvider provider = new TestMethodsProvider(sender, plugin);
		injectMapper(executor, mapper, "commandToMethodMapper");
		injectMapper(executor, filter, "permissionFilter");
		injectMapper(executor, methodSelector, "methodSelector");
		injectMapper(executor, instanceCreator, "instanceCreator");
		when(mapper.mapCommandToMethod(any(), any(), eq(sender), any())).thenReturn(Optional.of(method));
		when(filter.filterPermission(method, sender)).thenReturn(true);
		when(methodSelector.recalculateArguments(any(), any())).thenReturn(new Object[0]);
		when(instanceCreator.createInstance(sender, plugin, TestMethodsProvider.class)).thenReturn(provider);
		when(method.invoke(any(), any())).thenThrow(new IllegalAccessException());

		// when
		final Executable executable = () -> executor.onCommand(sender, command, "test", new String[]{"test"});

		// then
		assertThrows(IllegalAccessException.class, executable);
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
