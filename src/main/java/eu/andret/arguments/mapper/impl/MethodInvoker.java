/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.mapper.IMethodInvoker;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.util.Optional;

/**
 * The interface to invoke the fallback method.
 *
 * @param <E> The JavaPlugin instance.
 *
 * @author Andret
 * @since Sep 03, 2021
 */
@Value
@NonFinal
public class MethodInvoker<E extends JavaPlugin> implements IMethodInvoker<E> {
	@Override
	@NotNull
	@SneakyThrows
	public <A extends AnnotatedCommandExecutor<E>> A createInstance(@NotNull final CommandSender sender,
																	@NotNull final JavaPlugin plugin,
																	@NotNull final Class<A> executor,
																	@NotNull final Object... parameters) {
		final Constructor<A> c = findConstructor(executor, plugin)
				.orElseThrow(() -> new IllegalStateException("AnnotatedCommandExecutor subclass needs a constructor with at least 2 parameters: CommandSender and JavaPlugin as first two of them"));
		final Object[] o = new Object[parameters.length + 2];
		o[0] = sender;
		o[1] = plugin;
		System.arraycopy(parameters, 0, o, 2, parameters.length);
		return c.newInstance(o);
	}

	@NotNull
	@SuppressWarnings({"unchecked", "java:S1612"})
	private <A extends AnnotatedCommandExecutor<E>> Optional<Constructor<A>> findConstructor(
			@NotNull final Class<A> executor,
			@NotNull final JavaPlugin plugin) {
		final Constructor<A>[] constructors = (Constructor<A>[]) executor.getDeclaredConstructors();
		if (constructors.length != 1) {
			throw new UnsupportedOperationException("The class " + executor.getName()
					+ " has to have exactly one declared constructor");
		}
		return Optional.of(constructors[0])
				.filter(c -> c.getParameterCount() >= 2)
				.filter(c -> c.getParameterTypes()[0].isAssignableFrom(CommandSender.class))
				.filter(c -> c.getParameterTypes()[1].isAssignableFrom(plugin.getClass()));
	}
}
