package eu.andret.arguments.mapper;

import eu.andret.arguments.mapper.impl.MethodToCompletionMapper;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class MethodToCompletionMappingSetTest {
	@Test
	void typeCompletionTest() throws NoSuchMethodException {
		// given
		final Map<Class<?>, BiFunction<List<String>, CommandSender, Collection<String>>> typeCompleterMap = new HashMap<>();
		typeCompleterMap.put(boolean.class, (sender, collection) -> Arrays.asList("true", "false"));
		final IMethodToCompletionMapper mapper = new MethodToCompletionMapper(typeCompleterMap, new HashMap<>());
		final CommandSender sender = mock(CommandSender.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithTypeCompletion", boolean.class);
		final String[] args = {"testMethodWithTypeCompletion", ""};

		// when
		final Collection<String> collection = mapper.mapCommandToCompletion(method, args, sender);

		// then
		assertThat(collection).containsExactlyInAnyOrder("false", "true");
	}

	@Test
	void typeIgnoredCompletionTest() throws NoSuchMethodException {
		// given
		final Map<Class<?>, BiFunction<List<String>, CommandSender, Collection<String>>> typeCompleterMap = new HashMap<>();
		typeCompleterMap.put(boolean.class, (sender, collection) -> Arrays.asList("true", "false"));
		final IMethodToCompletionMapper mapper = new MethodToCompletionMapper(typeCompleterMap, new HashMap<>());
		final CommandSender sender = mock(CommandSender.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithIgnoredTypeCompletion", boolean.class);
		final String[] args = {"testMethodWithIgnoredTypeCompletion", ""};

		// when
		final Collection<String> collection = mapper.mapCommandToCompletion(method, args, sender);

		// then
		assertThat(collection).isEmpty();
	}

	@Test
	void typeMismatchingCompletionTest() throws NoSuchMethodException {
		// given
		final Map<Class<?>, BiFunction<List<String>, CommandSender, Collection<String>>> typeCompleterMap = new HashMap<>();
		typeCompleterMap.put(boolean.class, (sender, collection) -> Arrays.asList("true", "false"));
		final IMethodToCompletionMapper mapper = new MethodToCompletionMapper(typeCompleterMap, new HashMap<>());
		final CommandSender sender = mock(CommandSender.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithMismatchedTypeCompletion", Player.class);
		final String[] args = {"testMethodWithMismatchedTypeCompletion", ""};

		// when
		final Collection<String> collection = mapper.mapCommandToCompletion(method, args, sender);

		// then
		assertThat(collection).isEmpty();
	}

	@Test
	void argumentCompletionTest() throws NoSuchMethodException {
		// given
		final Map<String, BiFunction<List<String>, CommandSender, Collection<String>>> argumentCompleterMap = new HashMap<>();
		argumentCompleterMap.put("testWorldCompleter", (sender, collection) -> Arrays.asList("world", "world_nether", "world_the_end"));
		final IMethodToCompletionMapper mapper = new MethodToCompletionMapper(new HashMap<>(), argumentCompleterMap);
		final CommandSender sender = mock(CommandSender.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithArgumentCompletion", World.class);
		final String[] args = {"testMethodWithArgumentCompletion", ""};

		// when
		final Collection<String> collection = mapper.mapCommandToCompletion(method, args, sender);

		// then
		assertThat(collection).containsExactlyInAnyOrder("world_nether", "world_the_end", "world");
	}

	@Test
	void argumentExtraCompletionTest() throws NoSuchMethodException {
		// given
		final Map<String, BiFunction<List<String>, CommandSender, Collection<String>>> argumentCompleterMap = new HashMap<>();
		argumentCompleterMap.put("testWorldCompleter", (sender, collection) -> Arrays.asList("world", "world_nether", "world_the_end"));
		final IMethodToCompletionMapper mapper = new MethodToCompletionMapper(new HashMap<>(), argumentCompleterMap);
		final CommandSender sender = mock(CommandSender.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithArgumentCompletion", World.class);
		final String[] args = {"testMethodWithArgumentCompletion", "world", ""};

		// when
		final Collection<String> collection = mapper.mapCommandToCompletion(method, args, sender);

		// then
		assertThat(collection).isEmpty();
	}

	@Test
	void argumentVarargCompletionTest() throws NoSuchMethodException {
		// given
		final Map<String, BiFunction<List<String>, CommandSender, Collection<String>>> argumentCompleterMap = new HashMap<>();
		argumentCompleterMap.put("testWorldCompleter", (sender, collection) -> Arrays.asList("world", "world_nether", "world_the_end"));
		final IMethodToCompletionMapper mapper = new MethodToCompletionMapper(new HashMap<>(), argumentCompleterMap);
		final CommandSender sender = mock(CommandSender.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithVarArgArgumentCompletion", World[].class);
		final String[] args = {"testMethodWithVarArgArgumentCompletion", "world", ""};

		// when
		final Collection<String> collection = mapper.mapCommandToCompletion(method, args, sender);

		// then
		assertThat(collection).containsExactlyInAnyOrder("world_nether", "world_the_end", "world");
	}

	@Test
	void argumentMismatchCompletionTest() throws NoSuchMethodException {
		// given
		final Map<String, BiFunction<List<String>, CommandSender, Collection<String>>> argumentCompleterMap = new HashMap<>();
		argumentCompleterMap.put("testWorldCompleter", (sender, collection) -> Arrays.asList("world", "world_nether", "world_the_end"));
		final IMethodToCompletionMapper mapper = new MethodToCompletionMapper(new HashMap<>(), argumentCompleterMap);
		final CommandSender sender = mock(CommandSender.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithMismatchedArgumentCompletion", World.class);
		final String[] args = {"testMethodWithMismatchedArgumentCompletion", ""};

		// when
		final Collection<String> collection = mapper.mapCommandToCompletion(method, args, sender);

		// then
		assertThat(collection).isEmpty();
	}

	@Test
	void typeCompletionWithTooFewArgsTest() throws NoSuchMethodException {
		// given
		final Map<Class<?>, BiFunction<List<String>, CommandSender, Collection<String>>> typeCompleterMap = new HashMap<>();
		typeCompleterMap.put(boolean.class, (sender, collection) -> Arrays.asList("true", "false"));
		final IMethodToCompletionMapper mapper = new MethodToCompletionMapper(typeCompleterMap, new HashMap<>());
		final CommandSender sender = mock(CommandSender.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithTypeCompletion", boolean.class);
		final String[] args = {"testMethodWithTypeCompletion"};

		// when
		final Collection<String> collection = mapper.mapCommandToCompletion(method, args, sender);

		// then
		assertThat(collection).isEmpty();
	}
}
