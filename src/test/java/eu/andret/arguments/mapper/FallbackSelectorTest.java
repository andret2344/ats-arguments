/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.api.annotation.Mapper;
import eu.andret.arguments.mapper.impl.FallbackSelector;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FallbackSelectorTest {
	@Test
	void invokeMismatch() {
		// given
		final FallbackSelector<JavaPlugin> invoker = new FallbackSelector<>();
		final Class<? extends TestMethodsProvider> providerClass = TestMethodsProvider.class;

		// when
		final List<Method> result = invoker.selectFallback(null, World.class, providerClass);

		// then
		assertEquals(new ArrayList<>(), result);
	}

	@Test
	void selectArgumentFallbackMethods() throws ReflectiveOperationException {
		// given
		final Class<? extends TestMethodsProvider> providerClass = TestMethodsProvider.class;
		final Method fallbackMethod = providerClass.getDeclaredMethod("testMethodArgumentFallback",
				String.class);
		final Method fallbackSecondMethod = providerClass.getDeclaredMethod("testMethodArgumentSecondFallback",
				String.class);
		final Method method = providerClass.getDeclaredMethod("testMethodWithParam", World.class);
		final Mapper mapper = method.getParameters()[0].getAnnotation(Mapper.class);
		final IFallbackSelector<JavaPlugin> fallbackSelector = new FallbackSelector<>();

		// when
		final List<Method> result = fallbackSelector.selectFallback(mapper, World.class, providerClass);

		// then
		assertEquals(List.of(fallbackSecondMethod, fallbackMethod), result);
	}

	@Test
	void selectTypeFallbackMethods() throws ReflectiveOperationException {
		// given
		final Class<? extends TestMethodsProvider> providerClass = TestMethodsProvider.class;
		final Method highest = providerClass.getDeclaredMethod("testMethodWithMappedArgumentTypeFallbackHighest",
				String.class);
		final Method lowest = providerClass.getDeclaredMethod("testMethodWithMappedArgumentTypeFallbackLowest",
				String.class);
		final Method method = providerClass.getDeclaredMethod("testMethodWithMappedArgument", Location.class);
		final Mapper mapper = method.getParameters()[0].getAnnotation(Mapper.class);
		final IFallbackSelector<JavaPlugin> fallbackSelector = new FallbackSelector<>();

		// when
		final List<Method> result = fallbackSelector.selectFallback(mapper, Location.class, providerClass);

		// then
		assertEquals(List.of(highest, lowest), result);
	}
}
