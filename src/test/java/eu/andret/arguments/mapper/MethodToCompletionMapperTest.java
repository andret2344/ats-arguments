/*
 * Copyright Andret (c) 2018-2020. Copying and modifying allowed only keeping git link reference.
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

class MethodToCompletionMapperTest {
	@Test
	void typeCompletionTest() throws NoSuchMethodException {
		// given
		Map<Class<?>, Function<CommandSender, Collection<String>>> typeCompleterMap = new HashMap<>();
		typeCompleterMap.put(boolean.class, sender -> Arrays.asList("true", "false"));
		IMethodToCompletionMapper mapper = new MethodToCompletionMapper(typeCompleterMap, new HashMap<>());
		CommandSender sender = mock(CommandSender.class);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithTypeCompletion", boolean.class);
		String[] args = {"testMethodWithTypeCompletion", ""};

		// when
		Collection<String> collection = mapper.mapCommandToCompletion(method, args, sender);

		// then
		assertEquals(2, collection.size());
		assertTrue(collection.containsAll(Arrays.asList("false", "true")));
	}

	@Test
	void typeIgnoredCompletionTest() throws NoSuchMethodException {
		// given
		Map<Class<?>, Function<CommandSender, Collection<String>>> typeCompleterMap = new HashMap<>();
		typeCompleterMap.put(boolean.class, sender -> Arrays.asList("true", "false"));
		IMethodToCompletionMapper mapper = new MethodToCompletionMapper(typeCompleterMap, new HashMap<>());
		CommandSender sender = mock(CommandSender.class);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithIgnoredTypeCompletion", boolean.class);
		String[] args = {"testMethodWithIgnoredTypeCompletion", ""};

		// when
		Collection<String> collection = mapper.mapCommandToCompletion(method, args, sender);

		// then
		assertTrue(collection.isEmpty());
	}

	@Test
	void typeMismatchingCompletionTest() throws NoSuchMethodException {
		// given
		Map<Class<?>, Function<CommandSender, Collection<String>>> typeCompleterMap = new HashMap<>();
		typeCompleterMap.put(boolean.class, sender -> Arrays.asList("true", "false"));
		IMethodToCompletionMapper mapper = new MethodToCompletionMapper(typeCompleterMap, new HashMap<>());
		CommandSender sender = mock(CommandSender.class);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithMismatchedTypeCompletion", Player.class);
		String[] args = {"testMethodWithMismatchedTypeCompletion", ""};

		// when
		Collection<String> collection = mapper.mapCommandToCompletion(method, args, sender);

		// then
		assertTrue(collection.isEmpty());
	}

	@Test
	void argumentCompletionTest() throws NoSuchMethodException {
		// given
		Map<String, Function<CommandSender, Collection<String>>> argumentCompleterMap = new HashMap<>();
		argumentCompleterMap.put("testWorldCompleter", sender -> Arrays.asList("world", "world_nether", "world_the_end"));
		IMethodToCompletionMapper mapper = new MethodToCompletionMapper(new HashMap<>(), argumentCompleterMap);
		CommandSender sender = mock(CommandSender.class);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithArgumentCompletion", World.class);
		String[] args = {"testMethodWithArgumentCompletion", ""};

		// when
		Collection<String> collection = mapper.mapCommandToCompletion(method, args, sender);

		// then
		assertEquals(3, collection.size());
		assertTrue(collection.containsAll(Arrays.asList("world_nether", "world_the_end", "world")));
	}

	@Test
	void argumentExtraCompletionTest() throws NoSuchMethodException {
		// given
		Map<String, Function<CommandSender, Collection<String>>> argumentCompleterMap = new HashMap<>();
		argumentCompleterMap.put("testWorldCompleter", sender -> Arrays.asList("world", "world_nether", "world_the_end"));
		IMethodToCompletionMapper mapper = new MethodToCompletionMapper(new HashMap<>(), argumentCompleterMap);
		CommandSender sender = mock(CommandSender.class);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithArgumentCompletion", World.class);
		String[] args = {"testMethodWithArgumentCompletion", "world", ""};

		// when
		Collection<String> collection = mapper.mapCommandToCompletion(method, args, sender);

		// then
		assertTrue(collection.isEmpty());
	}

	@Test
	void argumentVarargCompletionTest() throws NoSuchMethodException {
		// given
		Map<String, Function<CommandSender, Collection<String>>> argumentCompleterMap = new HashMap<>();
		argumentCompleterMap.put("testWorldCompleter", sender -> Arrays.asList("world", "world_nether", "world_the_end"));
		IMethodToCompletionMapper mapper = new MethodToCompletionMapper(new HashMap<>(), argumentCompleterMap);
		CommandSender sender = mock(CommandSender.class);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithVarArgArgumentCompletion", World[].class);
		String[] args = {"testMethodWithVarArgArgumentCompletion", "world", ""};

		// when
		Collection<String> collection = mapper.mapCommandToCompletion(method, args, sender);

		// then
		assertEquals(3, collection.size());
		assertTrue(collection.containsAll(Arrays.asList("world_nether", "world_the_end", "world")));
	}

	@Test
	void argumentMismatchCompletionTest() throws NoSuchMethodException {
		// given
		Map<String, Function<CommandSender, Collection<String>>> argumentCompleterMap = new HashMap<>();
		argumentCompleterMap.put("testWorldCompleter", sender -> Arrays.asList("world", "world_nether", "world_the_end"));
		IMethodToCompletionMapper mapper = new MethodToCompletionMapper(new HashMap<>(), argumentCompleterMap);
		CommandSender sender = mock(CommandSender.class);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithMismatchedArgumentCompletion", World.class);
		String[] args = {"testMethodWithMismatchedArgumentCompletion", ""};

		// when
		Collection<String> collection = mapper.mapCommandToCompletion(method, args, sender);

		// then
		assertTrue(collection.isEmpty());
	}

	@Test
	void typeCompletionWithTooFewArgsTest() throws NoSuchMethodException {
		// given
		Map<Class<?>, Function<CommandSender, Collection<String>>> typeCompleterMap = new HashMap<>();
		typeCompleterMap.put(boolean.class, sender -> Arrays.asList("true", "false"));
		IMethodToCompletionMapper mapper = new MethodToCompletionMapper(typeCompleterMap, new HashMap<>());
		CommandSender sender = mock(CommandSender.class);
		Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithTypeCompletion", boolean.class);
		String[] args = {"testMethodWithTypeCompletion"};

		// when
		Collection<String> collection = mapper.mapCommandToCompletion(method, args, sender);

		// then
		assertTrue(collection.isEmpty());
	}
}
