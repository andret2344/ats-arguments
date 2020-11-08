/*
 * Copyright Andret (c) 2018-2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.AnnotatedCommand.OnInsufficientPermissionsListener;
import eu.andret.arguments.AnnotatedCommand.OnUnknownSubCommandExecutionListener;
import eu.andret.arguments.annotation.Argument;
import eu.andret.arguments.annotation.Fallback;
import eu.andret.arguments.entity.ExecutionCall;
import eu.andret.arguments.entity.Mapper;
import eu.andret.arguments.entity.ResponseType;
import eu.andret.arguments.mapper.ICommandToMethodMapper;
import eu.andret.arguments.mapper.IMapper;
import eu.andret.arguments.mapper.IMethodInvoker;
import eu.andret.arguments.mapper.IMethodToDescriptionMapper;
import eu.andret.arguments.mapper.IPermissionMapper;
import eu.andret.arguments.mapper.IResponseMapper;
import eu.andret.arguments.mapper.impl.MethodToDescriptionMapper;
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
		CommandSender sender = mock(CommandSender.class);
		Command command = mock(Command.class);
		IMethodToDescriptionMapper mapper = mock(MethodToDescriptionMapper.class);
		LocalCommandExecutor executor = new LocalCommandExecutor(TestMethodsProvider.class, null);
		injectMapper(executor, mapper, "methodToDescriptionMapper");
		when(mapper.mapMethodToDescription(any(Method.class), anyString())).thenReturn("/test testString");
		when(command.getName()).thenReturn("test");

		// when
		executor.onCommand(sender, command, "test", new String[0]);

		// then
		verify(sender, times(19)).sendMessage(eq("/test testString"));
	}

	@Test
	void incorrectArgumentsWithoutListener() {
		// given
		CommandSender sender = mock(CommandSender.class);
		Command command = mock(Command.class);
		JavaPlugin plugin = mock(JavaPlugin.class);
		ICommandToMethodMapper mapper = mock(ICommandToMethodMapper.class);
		LocalCommandExecutor executor = new LocalCommandExecutor(TestMethodsProvider.class, plugin);
		when(mapper.mapCommandToMethod(any(), any(), any())).thenReturn(Optional.empty());
		injectMapper(executor, mapper, "commandToMethodMapper");
		when(command.getName()).thenReturn("test");
		String[] args = {"testMethod"};
		OnInsufficientPermissionsListener insufficientPermissionsListener = mock(OnInsufficientPermissionsListener.class);
		executor.setOnInsufficientPermissionsListener(insufficientPermissionsListener);

		// when
		boolean result = executor.onCommand(sender, command, "test", args);

		// then
		assertTrue(result);
		verify(insufficientPermissionsListener, times(0)).insufficientPermissions(any(CommandSender.class));
	}

	@Test
	void incorrectArgumentsWithListener() {
		// given
		CommandSender sender = mock(CommandSender.class);
		Command command = mock(Command.class);
		JavaPlugin plugin = mock(JavaPlugin.class);
		ICommandToMethodMapper mapper = mock(ICommandToMethodMapper.class);
		LocalCommandExecutor executor = new LocalCommandExecutor(TestMethodsProvider.class, plugin);
		when(mapper.mapCommandToMethod(any(), any(), any())).thenReturn(Optional.empty());
		injectMapper(executor, mapper, "commandToMethodMapper");
		when(command.getName()).thenReturn("test");
		String[] args = {"testMethod"};
		OnUnknownSubCommandExecutionListener unknownSubCommandExecutionListener = mock(OnUnknownSubCommandExecutionListener.class);
		executor.setOnUnknownSubCommandExecutionListener(unknownSubCommandExecutionListener);
		OnInsufficientPermissionsListener insufficientPermissionsListener = mock(OnInsufficientPermissionsListener.class);
		executor.setOnInsufficientPermissionsListener(insufficientPermissionsListener);

		// when
		boolean result = executor.onCommand(sender, command, "test", args);

		// then
		assertTrue(result);
		verify(unknownSubCommandExecutionListener, times(1)).unknownSubCommandExecuted(eq(sender));
		verify(insufficientPermissionsListener, times(0)).insufficientPermissions(any(CommandSender.class));
	}

	@Test
	void correctArgumentsWithoutPermissionWithoutListener() {
		// given
		CommandSender sender = mock(CommandSender.class);
		Command command = mock(Command.class);
		JavaPlugin plugin = mock(JavaPlugin.class);
		ICommandToMethodMapper methodMapper = mock(ICommandToMethodMapper.class);
		IPermissionMapper permissionMapper = mock(IPermissionMapper.class);
		Method method = mock(Method.class);
		ExecutionCall call = new ExecutionCall(method);
		when(permissionMapper.mapPermission(eq(method), eq(sender))).thenReturn(false);
		LocalCommandExecutor executor = new LocalCommandExecutor(TestMethodsProvider.class, plugin);
		when(methodMapper.mapCommandToMethod(any(), any(), any())).thenReturn(Optional.of(call));
		injectMapper(executor, methodMapper, "commandToMethodMapper");
		injectMapper(executor, permissionMapper, "permissionMapper");
		when(command.getName()).thenReturn("test");
		String[] args = {"testMethod"};
		OnUnknownSubCommandExecutionListener unknownSubCommandExecutionListener = mock(OnUnknownSubCommandExecutionListener.class);
		executor.setOnUnknownSubCommandExecutionListener(unknownSubCommandExecutionListener);

		// when
		boolean result = executor.onCommand(sender, command, "test", args);

		// then
		assertTrue(result);
		verify(unknownSubCommandExecutionListener, times(0)).unknownSubCommandExecuted(any(CommandSender.class));
	}

	@Test
	void correctArgumentsWithoutPermissionWithListener() {
		// given
		CommandSender sender = mock(CommandSender.class);
		Command command = mock(Command.class);
		JavaPlugin plugin = mock(JavaPlugin.class);
		ICommandToMethodMapper methodMapper = mock(ICommandToMethodMapper.class);
		IPermissionMapper permissionMapper = mock(IPermissionMapper.class);
		Method method = mock(Method.class);
		ExecutionCall call = new ExecutionCall(method);
		when(permissionMapper.mapPermission(eq(method), eq(sender))).thenReturn(false);
		LocalCommandExecutor executor = new LocalCommandExecutor(TestMethodsProvider.class, plugin);
		when(methodMapper.mapCommandToMethod(any(), any(), any())).thenReturn(Optional.of(call));
		injectMapper(executor, methodMapper, "commandToMethodMapper");
		injectMapper(executor, permissionMapper, "permissionMapper");
		when(command.getName()).thenReturn("test");
		String[] args = {"testMethod"};
		OnUnknownSubCommandExecutionListener unknownSubCommandExecutionListener = mock(OnUnknownSubCommandExecutionListener.class);
		executor.setOnUnknownSubCommandExecutionListener(unknownSubCommandExecutionListener);
		OnInsufficientPermissionsListener insufficientPermissionsListener = mock(OnInsufficientPermissionsListener.class);
		executor.setOnInsufficientPermissionsListener(insufficientPermissionsListener);

		// when
		boolean result = executor.onCommand(sender, command, "test", args);

		// then
		assertTrue(result);
		verify(unknownSubCommandExecutionListener, times(0)).unknownSubCommandExecuted(any(CommandSender.class));
		verify(insufficientPermissionsListener, times(1)).insufficientPermissions(eq(sender));
	}

	@Test
	void correctArgumentsWithPermissionWithListener() {
		// given
		CommandSender sender = mock(CommandSender.class);
		Command command = mock(Command.class);
		JavaPlugin plugin = mock(JavaPlugin.class);
		ICommandToMethodMapper methodMapper = mock(ICommandToMethodMapper.class);
		IPermissionMapper permissionMapper = mock(IPermissionMapper.class);
		IMethodInvoker methodInvoker = mock(IMethodInvoker.class);
		IResponseMapper responseMapper = mock(IResponseMapper.class);
		Method method = mock(Method.class);
		ExecutionCall call = new ExecutionCall(method);
		Argument argument = mock(Argument.class);
		when(method.getAnnotation(eq(Argument.class))).thenReturn(argument);
		when(argument.responseType()).thenReturn(ResponseType.SENDER);
		when(permissionMapper.mapPermission(eq(method), eq(sender))).thenReturn(true);
		LocalCommandExecutor executor = new LocalCommandExecutor(TestMethodsProvider.class, plugin);
		Object value = mock(Object.class);
		when(methodInvoker.invokeMethod(eq(call), any(String[].class), eq(sender), eq(TestMethodsProvider.class))).thenReturn(value);
		when(methodMapper.mapCommandToMethod(any(), any(), any())).thenReturn(Optional.of(call));
		injectMapper(executor, methodMapper, "commandToMethodMapper");
		injectMapper(executor, permissionMapper, "permissionMapper");
		injectMapper(executor, methodInvoker, "methodInvoker");
		injectMapper(executor, responseMapper, "responseMapper");
		when(command.getName()).thenReturn("test");
		String[] args = {"testMethod"};
		OnUnknownSubCommandExecutionListener unknownSubCommandExecutionListener = mock(OnUnknownSubCommandExecutionListener.class);
		executor.setOnUnknownSubCommandExecutionListener(unknownSubCommandExecutionListener);
		OnInsufficientPermissionsListener insufficientPermissionsListener = mock(OnInsufficientPermissionsListener.class);
		executor.setOnInsufficientPermissionsListener(insufficientPermissionsListener);

		// when
		boolean result = executor.onCommand(sender, command, "test", args);

		// then
		assertTrue(result);
		verify(unknownSubCommandExecutionListener, times(0)).unknownSubCommandExecuted(any(CommandSender.class));
		verify(insufficientPermissionsListener, times(0)).insufficientPermissions(eq(sender));
		verify(responseMapper, times(1)).mapResponse(eq(sender), eq(value), eq(ResponseType.SENDER), any(AnnotatedCommand.Options.class));
	}

	@Test
	void addMapper() {
		// given
		JavaPlugin plugin = mock(JavaPlugin.class);
		LocalCommandExecutor executor = new LocalCommandExecutor(TestMethodsProvider.class, plugin);

		// when
		boolean result1 = executor.addMapper("test1", new Mapper<>(World.class, Bukkit::getWorld, Fallback.NEVER));
		boolean result2 = executor.addMapper("test1", new Mapper<>(World.class, Bukkit::getWorld, Fallback.NEVER));
		boolean result3 = executor.addMapper("test3", new Mapper<>(World.class, Bukkit::getWorld, Fallback.NEVER));

		// then
		assertTrue(result1);
		assertFalse(result2);
		assertTrue(result3);
	}

	@Test
	void setOptions() {
		// given
		JavaPlugin plugin = mock(JavaPlugin.class);
		LocalCommandExecutor executor = new LocalCommandExecutor(TestMethodsProvider.class, plugin);

		// when
		AnnotatedCommand.Options executorOptions = executor.getOptions();

		// then
		assertNotNull(executorOptions);
	}

	private void injectMapper(LocalCommandExecutor executor, IMapper mapper, String mapperName) {
		try {
			Field field = executor.getClass().getDeclaredField(mapperName);
			field.setAccessible(true);
			field.set(executor, mapper);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}
}
