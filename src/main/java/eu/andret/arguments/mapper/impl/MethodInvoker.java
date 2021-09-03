/*
 * Copyright Andret (c) 2018-2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.mapper.IMethodInvoker;
import lombok.SneakyThrows;
import lombok.Value;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The interface to invoke the fallback method.
 *
 * @param <E> The JavaPlugin instance.
 *
 * @author Andret
 * @since Sep 03, 2021
 */
@Value
public class MethodInvoker<E extends JavaPlugin> implements IMethodInvoker<E> {
	Map<CommandSender, AnnotatedCommandExecutor<E>> executors = new HashMap<>();
	JavaPlugin plugin;

	@NotNull
	@Override
	@SneakyThrows
	public List<Object> invokeMethods(@NotNull final List<Method> methods,
									  @NotNull final Object[] data,
									  @NotNull final CommandSender sender,
									  @NotNull final Class<? extends AnnotatedCommandExecutor<E>> executorClass,
									  @NotNull final Object... parameters) {
		final AnnotatedCommandExecutor<E> commandExecutor = createInstance(sender, executorClass, parameters);
		return methods.stream()
				.map(x -> invokeMethod(x, commandExecutor, data))
				.collect(Collectors.toList());
	}

	@NotNull
	@SneakyThrows
	@SuppressWarnings("unchecked")
	private <A extends AnnotatedCommandExecutor<E>> A createInstance(@NotNull final CommandSender sender,
																	 @NotNull final Class<A> executor,
																	 @NotNull final Object... parameters) {
		if (executors.containsKey(sender)) {
			return (A) executors.get(sender);
		}
		final Constructor<A> c = findConstructor(executor)
				.orElseThrow(() -> new IllegalStateException("AnnotatedCommandExecutor subclass needs a constructor with at least 2 parameters: CommandSender and JavaPlugin as first two of them"));
		final Object[] o = new Object[parameters.length + 2];
		o[0] = sender;
		o[1] = plugin;
		System.arraycopy(parameters, 0, o, 2, parameters.length);
		final A result = c.newInstance(o);
		executors.put(sender, result);
		return result;
	}

	@NotNull
	@SuppressWarnings({"unchecked", "java:S1612"})
	private <A extends AnnotatedCommandExecutor<E>> Optional<Constructor<A>> findConstructor(@NotNull final Class<A> executor) {
		return Arrays.stream(executor.getDeclaredConstructors())
				.filter(c -> c.getParameterCount() >= 2)
				.filter(c -> c.getParameterTypes()[0].isAssignableFrom(CommandSender.class))
				.filter(c -> c.getParameterTypes()[1].isAssignableFrom(plugin.getClass()))
				.map(x -> (Constructor<A>) x)
				.findAny();
	}

	@SneakyThrows
	private Object invokeMethod(@NotNull final Method method,
								@NotNull final AnnotatedCommandExecutor<E> executor,
								@NotNull final Object[] data) {
		return method.invoke(executor, data);
	}
}
