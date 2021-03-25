/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.AnnotatedCommand.OnInsufficientPermissionsListener;
import eu.andret.arguments.AnnotatedCommand.OnUnknownSubCommandExecutionListener;
import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.api.annotation.Fallback;
import eu.andret.arguments.api.entity.ResponseType;
import eu.andret.arguments.consumer.IResponseConsumer;
import eu.andret.arguments.entity.ExecutionCall;
import eu.andret.arguments.entity.Mapper;
import eu.andret.arguments.filter.IPermissionFilter;
import eu.andret.arguments.mapper.ICommandToMethodMapper;
import eu.andret.arguments.mapper.IMapper;
import eu.andret.arguments.mapper.IMethodInvoker;
import eu.andret.arguments.mapper.IMethodToDescriptionMapper;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
		final CommandSender sender = mock(CommandSender.class);
		final Command command = mock(Command.class);
		final IMethodToDescriptionMapper mapper = mock(IMethodToDescriptionMapper.class);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(TestMethodsProvider.class, null);
		injectMapper(executor, mapper, "methodToDescriptionMapper");
		when(mapper.mapMethodToDescription(any(Method.class), anyString())).thenReturn("/test testString");
		when(command.getName()).thenReturn("test");

		// when
		executor.onCommand(sender, command, "test", new String[0]);

		// then
		verify(sender, times(25)).sendMessage("/test testString");
	}

	@Test
	void incorrectArgumentsWithoutListener() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final Command command = mock(Command.class);
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final ICommandToMethodMapper mapper = mock(ICommandToMethodMapper.class);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(TestMethodsProvider.class, plugin);
		when(mapper.mapCommandToMethod(any(), any(), any())).thenReturn(Optional.empty());
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
		final Command command = mock(Command.class);
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final ICommandToMethodMapper mapper = mock(ICommandToMethodMapper.class);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(TestMethodsProvider.class, plugin);
		when(mapper.mapCommandToMethod(any(), any(), any())).thenReturn(Optional.empty());
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
		final Command command = mock(Command.class);
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final ICommandToMethodMapper methodMapper = mock(ICommandToMethodMapper.class);
		final IPermissionFilter permissionFilter = mock(IPermissionFilter.class);
		final Method method = mock(Method.class);
		final ExecutionCall call = new ExecutionCall(method);
		when(permissionFilter.filterPermission(method, sender)).thenReturn(false);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(TestMethodsProvider.class, plugin);
		when(methodMapper.mapCommandToMethod(any(), any(), any())).thenReturn(Optional.of(call));
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
		final Command command = mock(Command.class);
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final ICommandToMethodMapper methodMapper = mock(ICommandToMethodMapper.class);
		final IPermissionFilter permissionFilter = mock(IPermissionFilter.class);
		final Method method = mock(Method.class);
		final ExecutionCall call = new ExecutionCall(method);
		when(permissionFilter.filterPermission(method, sender)).thenReturn(false);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(TestMethodsProvider.class, plugin);
		when(methodMapper.mapCommandToMethod(any(), any(), any())).thenReturn(Optional.of(call));
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
		final Command command = mock(Command.class);
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final ICommandToMethodMapper methodMapper = mock(ICommandToMethodMapper.class);
		final IPermissionFilter permissionFilter = mock(IPermissionFilter.class);
		abstract class LocalMethodInvoker implements IMethodInvoker<JavaPlugin> {
		}
		final LocalMethodInvoker methodInvoker = mock(LocalMethodInvoker.class);
		final IResponseConsumer responseConsumer = mock(IResponseConsumer.class);
		final Method method = mock(Method.class);
		final ExecutionCall call = new ExecutionCall(method);
		final Argument argument = mock(Argument.class);
		when(method.getAnnotation(Argument.class)).thenReturn(argument);
		when(argument.responseType()).thenReturn(ResponseType.SENDER);
		when(permissionFilter.filterPermission(method, sender)).thenReturn(true);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(TestMethodsProvider.class, plugin);
		final Object value = mock(Object.class);
		when(methodInvoker.invokeMethod(eq(call), any(String[].class), eq(sender), eq(TestMethodsProvider.class))).thenReturn(value);
		when(methodMapper.mapCommandToMethod(any(), any(), any())).thenReturn(Optional.of(call));
		injectMapper(executor, methodMapper, "commandToMethodMapper");
		injectMapper(executor, permissionFilter, "permissionFilter");
		injectMapper(executor, methodInvoker, "methodInvoker");
		injectMapper(executor, responseConsumer, "responseConsumer");
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
		verify(insufficientPermissionsListener, times(0)).insufficientPermissions(sender);
		verify(responseConsumer, times(1)).consumeResponse(eq(sender), eq(value), eq(ResponseType.SENDER), any(AnnotatedCommand.Options.class));
	}

	@Test
	void addMapper() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(TestMethodsProvider.class, plugin);

		// when
		final boolean result1 = executor.addMapper("test1", new Mapper<>(World.class, Bukkit::getWorld, Fallback.NEVER));
		final boolean result2 = executor.addMapper("test1", new Mapper<>(World.class, Bukkit::getWorld, Fallback.NEVER));
		final boolean result3 = executor.addMapper("test3", new Mapper<>(World.class, Bukkit::getWorld, Fallback.NEVER));

		// then
		assertTrue(result1);
		assertFalse(result2);
		assertTrue(result3);
	}

	@Test
	void setOptions() {
		// given
		final JavaPlugin plugin = mock(JavaPlugin.class);
		final LocalCommandExecutor<JavaPlugin> executor = new LocalCommandExecutor<>(TestMethodsProvider.class, plugin);

		// when
		final AnnotatedCommand.Options executorOptions = executor.getOptions();

		// then
		assertNotNull(executorOptions);
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
