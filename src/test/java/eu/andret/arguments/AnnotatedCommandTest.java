/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.entity.MappingConfig;
import eu.andret.arguments.entity.MappingSet;
import eu.andret.arguments.entity.ResponseMappingSet;
import eu.andret.arguments.provider.TestEnum;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AnnotatedCommandTest {
	private static class TestCommandExecutor extends LocalCommandExecutor<JavaPlugin> {
		TestCommandExecutor(final AnnotatedCommand<JavaPlugin> annotatedCommand, final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass, final JavaPlugin plugin, final Object... parameters) {
			super(annotatedCommand, commandClass, plugin, parameters);
		}
	}

	private static class TestTabCompleter extends LocalTabCompleter<JavaPlugin> {
		TestTabCompleter(final AnnotatedCommand<JavaPlugin> annotatedCommand, final Class<? extends AnnotatedCommandExecutor<JavaPlugin>> commandClass) {
			super(annotatedCommand, commandClass);
		}
	}

	@Test
	void correctCommandReturned() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestCommandExecutor executor = mock(TestCommandExecutor.class);
		when(command.getExecutor()).thenReturn(executor);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);

		// when
		final PluginCommand result = annotatedCommand.getCommand();

		// then
		assertSame(command, result);
	}

	@Test
	void correctListenersSetup() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestCommandExecutor executor = mock(TestCommandExecutor.class);
		when(command.getExecutor()).thenReturn(executor);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);

		// when
		annotatedCommand.setOnUnknownSubCommandExecutionListener(sender -> {
		});
		annotatedCommand.setOnInsufficientPermissionsListener(sender -> {
		});
		annotatedCommand.setOnMainCommandExecutionListener(sender -> {
		});

		// then
		verify(executor, times(1)).setOnUnknownSubCommandExecutionListener(any());
		verify(executor, times(1)).setOnInsufficientPermissionsListener(any());
		verify(executor, times(1)).setOnMainCommandExecutionListener(any());
	}

	@Test
	void correctExecutorReturned() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestCommandExecutor executor = mock(TestCommandExecutor.class);
		when(command.getExecutor()).thenReturn(executor);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);

		// when
		final CommandExecutor result = annotatedCommand.getCommand().getExecutor();

		// then
		assertSame(executor, result);
	}

	@Test
	void correctCompleterReturned() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		final TestTabCompleter completer = mock(TestTabCompleter.class);
		when(command.getTabCompleter()).thenReturn(completer);

		// when
		final TabCompleter result = annotatedCommand.getCommand().getTabCompleter();

		// then
		assertSame(completer, result);
	}

	@Test
	void correctAddArgumentMapper() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestCommandExecutor executor = mock(TestCommandExecutor.class);
		when(command.getExecutor()).thenReturn(executor);
		when(executor.getMappingConfig()).thenReturn(new MappingConfig());
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);

		// when
		annotatedCommand.addArgumentMapper("test", int.class, Integer::parseInt);

		// then
		final MappingSet<?> test = executor.getMappingConfig().getArgumentMapper("test");
		assertNotNull(test);
		assertEquals(int.class, test.getClazz());
	}

	@Test
	void incorrectAddArgumentMapper() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestCommandExecutor executor = mock(TestCommandExecutor.class);
		when(command.getExecutor()).thenReturn(executor);
		when(executor.getMappingConfig()).thenReturn(new MappingConfig());
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		annotatedCommand.addArgumentMapper("test", double.class, Double::parseDouble);

		// when
		final Executable result = () -> annotatedCommand.addArgumentMapper("test", int.class, Integer::parseInt);

		// then
		assertThrows(IllegalArgumentException.class, result);
		final MappingSet<?> test = executor.getMappingConfig().getArgumentMapper("test");
		assertNotNull(test);
		assertEquals(double.class, test.getClazz());
	}

	@Test
	void correctAddTypeMapper() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestCommandExecutor executor = mock(TestCommandExecutor.class);
		when(command.getExecutor()).thenReturn(executor);
		when(executor.getMappingConfig()).thenReturn(new MappingConfig());
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);

		// when
		annotatedCommand.addTypeMapper(boolean.class, Boolean::parseBoolean);

		// then
		final MappingSet<?> test = executor.getMappingConfig().getTypeMapper(boolean.class);
		assertNotNull(test);
		assertEquals(boolean.class, test.getClazz());
	}

	@Test
	void incorrectAddTypeMapper() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestCommandExecutor executor = mock(TestCommandExecutor.class);
		when(command.getExecutor()).thenReturn(executor);
		when(executor.getMappingConfig()).thenReturn(new MappingConfig());
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		annotatedCommand.addTypeMapper(boolean.class, Boolean::parseBoolean);

		// when
		final Executable result = () -> annotatedCommand.addTypeMapper(boolean.class, Boolean::parseBoolean);

		// then
		assertThrows(IllegalArgumentException.class, result);
		final MappingSet<?> test = executor.getMappingConfig().getTypeMapper(boolean.class);
		assertNotNull(test);
		assertEquals(boolean.class, test.getClazz());
	}

	@Test
	void correctAddEnumMapper() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestCommandExecutor executor = mock(TestCommandExecutor.class);
		when(command.getExecutor()).thenReturn(executor);
		when(executor.getMappingConfig()).thenReturn(new MappingConfig());
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);

		// when
		annotatedCommand.addEnumMapper(TestEnum.class);

		// then
		final MappingSet<?> test = executor.getMappingConfig().getTypeMapper(TestEnum.class);
		assertNotNull(test);
		assertEquals(TestEnum.class, test.getClazz());
		assertEquals(TestEnum.TEST_ONE, test.getFunction().apply("TEST_ONE"));
	}

	@Test
	void incorrectAddEnumMapper() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestCommandExecutor executor = mock(TestCommandExecutor.class);
		when(command.getExecutor()).thenReturn(executor);
		when(executor.getMappingConfig()).thenReturn(new MappingConfig());
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		annotatedCommand.addEnumMapper(TestEnum.class);

		// when
		final Executable result = () -> annotatedCommand.addEnumMapper(TestEnum.class);

		// then
		assertThrows(IllegalArgumentException.class, result);
		final MappingSet<?> test = executor.getMappingConfig().getTypeMapper(TestEnum.class);
		assertNotNull(test);
		assertEquals(TestEnum.class, test.getClazz());
	}

	@Test
	void correctAddTypeCompleterWithCollection() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestTabCompleter completer = mock(TestTabCompleter.class);
		when(command.getTabCompleter()).thenReturn(completer);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		when(completer.addTypeCompleter(any(Class.class), any())).thenReturn(true);
		final Collection<String> list = new ArrayList<>();
		list.add("one");
		list.add("two");

		// when
		annotatedCommand.addTypeCompleter(World.class, list);

		// then
		verify(completer, times(1))
				.addTypeCompleter(eq(World.class), argThat(function -> function.apply(null, null).equals(list)));
	}

	@Test
	void correctAddTypeCompleterWithFunction() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestTabCompleter completer = mock(TestTabCompleter.class);
		when(command.getTabCompleter()).thenReturn(completer);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		when(completer.addTypeCompleter(any(Class.class), any())).thenReturn(true);
		final Collection<String> list = new ArrayList<>();
		list.add("one");
		list.add("two");

		// when
		annotatedCommand.addTypeCompleter(World.class, (sender) -> list);

		// then
		verify(completer, times(1))
				.addTypeCompleter(eq(World.class), argThat(function -> function.apply(null, null).equals(list)));
	}

	@Test
	void incorrectAddTypeCompleter() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestTabCompleter completer = mock(TestTabCompleter.class);
		when(command.getTabCompleter()).thenReturn(completer);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		when(completer.addTypeCompleter(any(Class.class), any())).thenReturn(false);

		// when
		final Executable result = () -> annotatedCommand.addTypeCompleter(World.class, new ArrayList<>());

		// then
		assertThrows(IllegalArgumentException.class, result);
		verify(completer, times(1)).addTypeCompleter(eq(World.class), any());
	}

	@Test
	void correctAddArgumentCompleterWithCollection() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestTabCompleter completer = mock(TestTabCompleter.class);
		when(command.getTabCompleter()).thenReturn(completer);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		when(completer.addArgumentCompleter(any(String.class), any())).thenReturn(true);
		final Collection<String> list = new ArrayList<>();
		list.add("one");
		list.add("two");

		// when
		annotatedCommand.addArgumentCompleter("testPlayerMapper", list);

		// then
		verify(completer, times(1))
				.addArgumentCompleter(eq("testPlayerMapper"), argThat(function -> function.apply(null, null).equals(list)));
	}

	@Test
	void correctAddArgumentCompleterWithFunction() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestTabCompleter completer = mock(TestTabCompleter.class);
		when(command.getTabCompleter()).thenReturn(completer);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		when(completer.addArgumentCompleter(any(String.class), any())).thenReturn(true);
		final Collection<String> list = new ArrayList<>();
		list.add("one");
		list.add("two");

		// when
		annotatedCommand.addArgumentCompleter("testPlayerMapper", (sender) -> list);

		// then
		verify(completer, times(1))
				.addArgumentCompleter(eq("testPlayerMapper"), argThat(function -> function.apply(null, null).equals(list)));
	}

	@Test
	void incorrectAddArgumentCompleter() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestTabCompleter completer = mock(TestTabCompleter.class);
		when(command.getTabCompleter()).thenReturn(completer);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		when(completer.addArgumentCompleter(any(String.class), any())).thenReturn(false);

		// when
		final Executable result = () -> annotatedCommand.addArgumentCompleter("testPlayerMapper", new ArrayList<>());

		// then
		assertThrows(IllegalArgumentException.class, result);
		verify(completer, times(1)).addArgumentCompleter(eq("testPlayerMapper"), any());
	}

	@Test
	void correctAddEnumCompleter() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestTabCompleter completer = mock(TestTabCompleter.class);
		when(command.getTabCompleter()).thenReturn(completer);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		when(completer.addTypeCompleter(eq(TestEnum.class), any())).thenReturn(true);
		final Collection<String> values = Arrays.stream(TestEnum.values())
				.map(Enum::toString)
				.collect(Collectors.toList());

		// when
		annotatedCommand.addEnumCompleter(TestEnum.class);

		// then
		verify(completer, times(1)).addTypeCompleter(eq(TestEnum.class),
				argThat(function -> function.apply(null, null).equals(values)));
	}

	@Test
	void incorrectAddEnumCompleter() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestTabCompleter completer = mock(TestTabCompleter.class);
		when(command.getTabCompleter()).thenReturn(completer);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		when(completer.addTypeCompleter(eq(TestEnum.class), any())).thenReturn(false);

		// when
		final Executable result = () -> annotatedCommand.addEnumCompleter(TestEnum.class);

		// then
		assertThrows(IllegalArgumentException.class, result);
		verify(completer, times(1)).addTypeCompleter(eq(TestEnum.class), any());
	}

	@Test
	void correctAddTypeResponseMapper() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestCommandExecutor executor = mock(TestCommandExecutor.class);
		when(command.getExecutor()).thenReturn(executor);
		when(executor.getMappingConfig()).thenReturn(new MappingConfig());
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);

		// when
		annotatedCommand.addTypeResponseMapper(boolean.class, String::valueOf);

		// then
		final ResponseMappingSet<?> test = executor.getMappingConfig().getTypeResponseMapper(boolean.class);
		assertNotNull(test);
		assertEquals(boolean.class, test.getClazz());
	}

	@Test
	void incorrectAddTypeResponseMapper() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestCommandExecutor executor = mock(TestCommandExecutor.class);
		when(command.getExecutor()).thenReturn(executor);
		when(executor.getMappingConfig()).thenReturn(new MappingConfig());
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		annotatedCommand.addTypeResponseMapper(boolean.class, String::valueOf);

		// when
		final Executable result = () -> annotatedCommand.addTypeResponseMapper(boolean.class, String::valueOf);

		// then
		assertThrows(IllegalArgumentException.class, result);
		final ResponseMappingSet<?> test = executor.getMappingConfig().getTypeResponseMapper(boolean.class);
		assertNotNull(test);
		assertEquals(boolean.class, test.getClazz());
	}

	@Test
	void correctAddArgumentResponseMapper() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestCommandExecutor executor = mock(TestCommandExecutor.class);
		when(command.getExecutor()).thenReturn(executor);
		when(executor.getMappingConfig()).thenReturn(new MappingConfig());
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);

		// when
		annotatedCommand.addArgumentResponseMapper("test", int.class, String::valueOf);

		// then
		final ResponseMappingSet<?> test = executor.getMappingConfig().getArgumentResponseMapper("test");
		assertNotNull(test);
		assertEquals(int.class, test.getClazz());
	}

	@Test
	void incorrectAddArgumentResponseMapper() {
		// given
		final PluginCommand command = mock(PluginCommand.class);
		final TestCommandExecutor executor = mock(TestCommandExecutor.class);
		when(command.getExecutor()).thenReturn(executor);
		when(executor.getMappingConfig()).thenReturn(new MappingConfig());
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);
		annotatedCommand.addArgumentResponseMapper("test", double.class, String::valueOf);

		// when
		final Executable result = () -> annotatedCommand.addArgumentResponseMapper("test", int.class, String::valueOf);

		// then
		assertThrows(IllegalArgumentException.class, result);
		final ResponseMappingSet<?> test = executor.getMappingConfig().getArgumentResponseMapper("test");
		assertNotNull(test);
		assertEquals(double.class, test.getClazz());
	}

	@Test
	void getCommandExecutorTest() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final JavaPlugin javaPlugin = mock(JavaPlugin.class);
		final TestMethodsProvider testMethodsProvider = new TestMethodsProvider(sender, javaPlugin);
		final PluginCommand command = mock(PluginCommand.class);
		final TestCommandExecutor executor = mock(TestCommandExecutor.class);
		when(command.getExecutor()).thenReturn(executor);
		when(executor.getCommandExecutor(sender)).thenReturn(testMethodsProvider);
		final AnnotatedCommand<JavaPlugin> annotatedCommand = new AnnotatedCommand<>(command);

		// when
		final AnnotatedCommandExecutor<JavaPlugin> commandExecutor = annotatedCommand.getCommandExecutor(sender);

		// then
		assertEquals(testMethodsProvider, commandExecutor);
	}
}
