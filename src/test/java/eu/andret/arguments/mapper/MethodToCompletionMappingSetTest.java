/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.mapper.impl.MethodToCompletionMapper;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class MethodToCompletionMappingSetTest {
	@Test
	void typeCompletionTest() throws NoSuchMethodException {
		// given
		final Map<Class<?>, Function<CommandSender, Collection<String>>> typeCompleterMap = new HashMap<>();
		typeCompleterMap.put(boolean.class, sender -> Arrays.asList("true", "false"));
		final IMethodToCompletionMapper mapper = new MethodToCompletionMapper(typeCompleterMap, new HashMap<>());
		final CommandSender sender = mock(CommandSender.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithTypeCompletion", boolean.class);
		final String[] args = {"testMethodWithTypeCompletion", ""};

		// when
		final Collection<String> collection = mapper.mapCommandToCompletion(method, args, sender);

		// then
		assertEquals(2, collection.size());
		assertTrue(collection.containsAll(Arrays.asList("false", "true")));
	}

	@Test
	void typeIgnoredCompletionTest() throws NoSuchMethodException {
		// given
		final Map<Class<?>, Function<CommandSender, Collection<String>>> typeCompleterMap = new HashMap<>();
		typeCompleterMap.put(boolean.class, sender -> Arrays.asList("true", "false"));
		final IMethodToCompletionMapper mapper = new MethodToCompletionMapper(typeCompleterMap, new HashMap<>());
		final CommandSender sender = mock(CommandSender.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithIgnoredTypeCompletion", boolean.class);
		final String[] args = {"testMethodWithIgnoredTypeCompletion", ""};

		// when
		final Collection<String> collection = mapper.mapCommandToCompletion(method, args, sender);

		// then
		assertTrue(collection.isEmpty());
	}

	@Test
	void typeMismatchingCompletionTest() throws NoSuchMethodException {
		// given
		final Map<Class<?>, Function<CommandSender, Collection<String>>> typeCompleterMap = new HashMap<>();
		typeCompleterMap.put(boolean.class, sender -> Arrays.asList("true", "false"));
		final IMethodToCompletionMapper mapper = new MethodToCompletionMapper(typeCompleterMap, new HashMap<>());
		final CommandSender sender = mock(CommandSender.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithMismatchedTypeCompletion", Player.class);
		final String[] args = {"testMethodWithMismatchedTypeCompletion", ""};

		// when
		final Collection<String> collection = mapper.mapCommandToCompletion(method, args, sender);

		// then
		assertTrue(collection.isEmpty());
	}

	@Test
	void argumentCompletionTest() throws NoSuchMethodException {
		// given
		final Map<String, Function<CommandSender, Collection<String>>> argumentCompleterMap = new HashMap<>();
		argumentCompleterMap.put("testWorldCompleter", sender -> Arrays.asList("world", "world_nether", "world_the_end"));
		final IMethodToCompletionMapper mapper = new MethodToCompletionMapper(new HashMap<>(), argumentCompleterMap);
		final CommandSender sender = mock(CommandSender.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithArgumentCompletion", World.class);
		final String[] args = {"testMethodWithArgumentCompletion", ""};

		// when
		final Collection<String> collection = mapper.mapCommandToCompletion(method, args, sender);

		// then
		assertEquals(3, collection.size());
		assertTrue(collection.containsAll(Arrays.asList("world_nether", "world_the_end", "world")));
	}

	@Test
	void argumentExtraCompletionTest() throws NoSuchMethodException {
		// given
		final Map<String, Function<CommandSender, Collection<String>>> argumentCompleterMap = new HashMap<>();
		argumentCompleterMap.put("testWorldCompleter", sender -> Arrays.asList("world", "world_nether", "world_the_end"));
		final IMethodToCompletionMapper mapper = new MethodToCompletionMapper(new HashMap<>(), argumentCompleterMap);
		final CommandSender sender = mock(CommandSender.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithArgumentCompletion", World.class);
		final String[] args = {"testMethodWithArgumentCompletion", "world", ""};

		// when
		final Collection<String> collection = mapper.mapCommandToCompletion(method, args, sender);

		// then
		assertTrue(collection.isEmpty());
	}

	@Test
	void argumentVarargCompletionTest() throws NoSuchMethodException {
		// given
		final Map<String, Function<CommandSender, Collection<String>>> argumentCompleterMap = new HashMap<>();
		argumentCompleterMap.put("testWorldCompleter", sender -> Arrays.asList("world", "world_nether", "world_the_end"));
		final IMethodToCompletionMapper mapper = new MethodToCompletionMapper(new HashMap<>(), argumentCompleterMap);
		final CommandSender sender = mock(CommandSender.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithVarArgArgumentCompletion", World[].class);
		final String[] args = {"testMethodWithVarArgArgumentCompletion", "world", ""};

		// when
		final Collection<String> collection = mapper.mapCommandToCompletion(method, args, sender);

		// then
		assertEquals(3, collection.size());
		assertTrue(collection.containsAll(Arrays.asList("world_nether", "world_the_end", "world")));
	}

	@Test
	void argumentMismatchCompletionTest() throws NoSuchMethodException {
		// given
		final Map<String, Function<CommandSender, Collection<String>>> argumentCompleterMap = new HashMap<>();
		argumentCompleterMap.put("testWorldCompleter", sender -> Arrays.asList("world", "world_nether", "world_the_end"));
		final IMethodToCompletionMapper mapper = new MethodToCompletionMapper(new HashMap<>(), argumentCompleterMap);
		final CommandSender sender = mock(CommandSender.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithMismatchedArgumentCompletion", World.class);
		final String[] args = {"testMethodWithMismatchedArgumentCompletion", ""};

		// when
		final Collection<String> collection = mapper.mapCommandToCompletion(method, args, sender);

		// then
		assertTrue(collection.isEmpty());
	}

	@Test
	void typeCompletionWithTooFewArgsTest() throws NoSuchMethodException {
		// given
		final Map<Class<?>, Function<CommandSender, Collection<String>>> typeCompleterMap = new HashMap<>();
		typeCompleterMap.put(boolean.class, sender -> Arrays.asList("true", "false"));
		final IMethodToCompletionMapper mapper = new MethodToCompletionMapper(typeCompleterMap, new HashMap<>());
		final CommandSender sender = mock(CommandSender.class);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithTypeCompletion", boolean.class);
		final String[] args = {"testMethodWithTypeCompletion"};

		// when
		final Collection<String> collection = mapper.mapCommandToCompletion(method, args, sender);

		// then
		assertTrue(collection.isEmpty());
	}
}
