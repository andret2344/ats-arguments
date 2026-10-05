package eu.andret.arguments;

import eu.andret.arguments.entity.MappingConfig;
import eu.andret.arguments.entity.MappingSet;
import eu.andret.arguments.entity.ResponseMappingSet;
import eu.andret.arguments.provider.TestEnum;
import org.assertj.core.api.ThrowableAssert;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AnnotatedCommandTest {
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
		assertThat(result).isSameAs(command);
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
		verify(executor).setOnUnknownSubCommandExecutionListener(any());
		verify(executor).setOnInsufficientPermissionsListener(any());
		verify(executor).setOnMainCommandExecutionListener(any());
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
		assertThat(result).isSameAs(executor);
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
		assertThat(result).isSameAs(completer);
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
		final MappingSet<?> result = executor.getMappingConfig().getArgumentMapper("test");
		assertThat(result).isNotNull();
		assertThat(result.clazz()).isSameAs(int.class);
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
		final ThrowableAssert.ThrowingCallable callable = () -> annotatedCommand.addArgumentMapper("test", int.class, Integer::parseInt);

		// then
		assertThatThrownBy(callable)
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Mapper with id \"test\" is already registered!");
		final MappingSet<?> result = executor.getMappingConfig().getArgumentMapper("test");
		assertThat(result).isNotNull();
		assertThat(result.clazz()).isSameAs(double.class);
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
		final MappingSet<?> result = executor.getMappingConfig().getTypeMapper(boolean.class);
		assertThat(result).isNotNull();
		assertThat(result.clazz()).isSameAs(boolean.class);
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
		final ThrowableAssert.ThrowingCallable callable = () -> annotatedCommand.addTypeMapper(boolean.class, Boolean::parseBoolean);

		// then
		assertThatThrownBy(callable)
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Mapper with class boolean is already registered!");
		final MappingSet<?> result = executor.getMappingConfig().getTypeMapper(boolean.class);
		assertThat(result).isNotNull();
		assertThat(result.clazz()).isSameAs(boolean.class);
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
		final MappingSet<?> mappingSet = executor.getMappingConfig().getTypeMapper(TestEnum.class);
		assertThat(mappingSet).isNotNull();
		assertThat(mappingSet.clazz()).isSameAs(TestEnum.class);
		assertThat(mappingSet.function().apply("TEST_ONE")).isSameAs(TestEnum.TEST_ONE);
		assertThat(mappingSet.function().apply("UNKNOWN")).isNull();
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
		final ThrowableAssert.ThrowingCallable callable = () -> annotatedCommand.addEnumMapper(TestEnum.class);

		// then
		assertThatThrownBy(callable)
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Mapper with enum eu.andret.arguments.provider.TestEnum is already registered!");
		final MappingSet<?> result = executor.getMappingConfig().getTypeMapper(TestEnum.class);
		assertThat(result).isNotNull();
		assertThat(result.clazz()).isSameAs(TestEnum.class);
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
		verify(completer)
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
		annotatedCommand.addTypeCompleter(World.class, sender -> list);

		// then
		verify(completer)
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
		final ThrowableAssert.ThrowingCallable callable = () -> annotatedCommand.addTypeCompleter(World.class, new ArrayList<>());

		// then
		assertThatThrownBy(callable)
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Completer with class org.bukkit.World is already registered!");
		verify(completer).addTypeCompleter(eq(World.class), any());
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
		verify(completer)
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
		annotatedCommand.addArgumentCompleter("testPlayerMapper", sender -> list);

		// then
		verify(completer)
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
		final ThrowableAssert.ThrowingCallable callable = () -> annotatedCommand.addArgumentCompleter("testPlayerMapper", new ArrayList<>());

		// then
		assertThatThrownBy(callable)
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Completer with id \"testPlayerMapper\" is already registered!");
		verify(completer).addArgumentCompleter(eq("testPlayerMapper"), any());
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
		verify(completer).addTypeCompleter(eq(TestEnum.class),
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
		final ThrowableAssert.ThrowingCallable callable = () -> annotatedCommand.addEnumCompleter(TestEnum.class);

		// then
		assertThatThrownBy(callable)
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Completer with enum eu.andret.arguments.provider.TestEnum is already registered!");
		verify(completer).addTypeCompleter(eq(TestEnum.class), any());
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
		final ResponseMappingSet<?> result = executor.getMappingConfig().getTypeResponseMapper(boolean.class);
		assertThat(result).isNotNull();
		assertThat(result.clazz()).isSameAs(boolean.class);
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
		final ThrowableAssert.ThrowingCallable callable = () -> annotatedCommand.addTypeResponseMapper(boolean.class, String::valueOf);

		// then
		assertThatThrownBy(callable)
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Response mapper with class boolean is already registered!");
		final ResponseMappingSet<?> result = executor.getMappingConfig().getTypeResponseMapper(boolean.class);
		assertThat(result).isNotNull();
		assertThat(result.clazz()).isSameAs(boolean.class);
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
		final ResponseMappingSet<?> result = executor.getMappingConfig().getArgumentResponseMapper("test");
		assertThat(result).isNotNull();
		assertThat(result.clazz()).isSameAs(int.class);
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
		final ThrowableAssert.ThrowingCallable callable = () -> annotatedCommand.addArgumentResponseMapper("test", int.class, String::valueOf);

		// then
		assertThatThrownBy(callable)
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Response mapper with id \"test\" is already registered!");
		final ResponseMappingSet<?> result = executor.getMappingConfig().getArgumentResponseMapper("test");
		assertThat(result).isNotNull();
		assertThat(result.clazz()).isSameAs(double.class);
	}
}
