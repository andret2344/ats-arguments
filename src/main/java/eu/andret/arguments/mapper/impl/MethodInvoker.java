/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.FallbackException;
import eu.andret.arguments.Util;
import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.api.annotation.Fallback;
import eu.andret.arguments.api.annotation.Param;
import eu.andret.arguments.entity.ExecutionCall;
import eu.andret.arguments.entity.Mapper;
import eu.andret.arguments.mapper.IMethodInvoker;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.Value;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * An implementation of {@link IMethodInvoker}.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
@Value
@Getter(AccessLevel.NONE)
public class MethodInvoker implements IMethodInvoker {
	private static final Map<CommandSender, AnnotatedCommandExecutor<JavaPlugin>> EXECUTORS = new HashMap<>();
	JavaPlugin plugin;
	Map<String, Mapper<?>> mappers;

	@Override
	public Object invokeMethod(final ExecutionCall call, final String[] command, final CommandSender sender, final Class<? extends AnnotatedCommandExecutor<? extends JavaPlugin>> executor) {
		try {
			final Object[] data = recalculateArguments(call.getMethod(), command);
			return invoke(call.getMethod(), sender, executor, data);
		} catch (final FallbackException ex) {
			final Object[] data = recalculateArguments(call.getFallbackMethod(), command);
			return invoke(call.getFallbackMethod(), sender, executor, data);
		}
	}

	private Object[] recalculateArguments(final Method method, final String... args) {
		final Argument argument = method.getAnnotation(Argument.class);
		final Fallback fallback = method.getAnnotation(Fallback.class);
		final Object[] data = new Object[method.getParameterCount()];
		int skip = 0;
		for (int i = 0; i < method.getParameterCount(); i++) {
			if (fallback != null && i == 0 || i == argument.position()) {
				skip++;
			}
			final Param param = method.getParameters()[i].getAnnotation(Param.class);
			if (method.getParameters()[i].isVarArgs()) {
				final Class<?> type = method.getParameters()[i].getType().getComponentType();
				final int length = args.length - i + skip - 2;
				final Object array = Array.newInstance(type, length);
				for (int j = 0; j < length; j++) {
					Array.set(array, j, convert(param, type, args[j + i + skip]));
				}
				data[i] = array;
				break;
			} else {
				data[i] = convert(param, method.getParameters()[i].getType(), args[i + skip]);
			}
		}
		return data;
	}

	private Object convert(final Param param, final Class<?> c, final String value) {
		final Optional<? extends Mapper<?>> mapper = Optional.ofNullable(param)
				.map(Param::value)
				.filter(mappers::containsKey)
				.map(mappers::get)
				.filter(m -> m.getClazz().equals(c));
		if (mapper.isEmpty()) {
			return Util.convert(c, value);
		}
		final Object result = mapper.get().getFunction().apply(value);
		if (mapper.get().getFallbackCondition().test(result)) {
			throw new FallbackException("Fallback Condition failed");
		}
		return c.cast(result);
	}

	@SuppressWarnings("unchecked")
	@SneakyThrows
	private <E extends AnnotatedCommandExecutor<? extends JavaPlugin>> Object invoke(final Method method, final CommandSender sender, final Class<E> executor, final Object... data) {
		if (!EXECUTORS.containsKey(sender)) {
			final Constructor<?> c = findMatchingConstructor(executor).orElseThrow(() -> new IllegalStateException("AnnotatedCommandExecutor subclass has to contain a constructor that takes 2 parameters: CommandSender and JavaPlugin"));
			EXECUTORS.put(sender, (AnnotatedCommandExecutor<JavaPlugin>) c.newInstance(sender, plugin));
		}
		return method.invoke(EXECUTORS.get(sender), data);
	}

	private Optional<Constructor<?>> findMatchingConstructor(final Class<?> executor) {
		return Arrays.stream(executor.getDeclaredConstructors())
				.filter(c -> c.getParameterCount() == 2)
				.filter(c -> c.getParameterTypes()[0].isAssignableFrom(CommandSender.class))
				.filter(c -> c.getParameterTypes()[1].isAssignableFrom(plugin.getClass()))
				.findAny();
	}
}
