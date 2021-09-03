/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.api.annotation.Mapper;
import eu.andret.arguments.mapper.impl.FallbackSelector;
import eu.andret.arguments.provider.TestMethodsProvider;
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
		final FallbackSelector<JavaPlugin> invoker = new FallbackSelector<>();
		final Class<? extends TestMethodsProvider> providerClass = TestMethodsProvider.class;

		final Object result = invoker.selectFallback(null, World.class, providerClass);

		assertEquals(new ArrayList<>(), result);
	}

	@Test
	void dupa() throws ReflectiveOperationException {
		final Class<? extends TestMethodsProvider> providerClass = TestMethodsProvider.class;
		final Method fallbackMethod = providerClass.getDeclaredMethod("testMethodWithParamArgumentFallback",
				String.class);
		final Method method = providerClass.getDeclaredMethod("testMethodWithParam", World.class);
		final Mapper mapper = method.getParameters()[0].getAnnotation(Mapper.class);
		final IFallbackSelector<JavaPlugin> invoker = new FallbackSelector<>();

		final Object result = invoker.selectFallback(mapper, World.class, providerClass);

		assertEquals(List.of(fallbackMethod), result);
	}
}
