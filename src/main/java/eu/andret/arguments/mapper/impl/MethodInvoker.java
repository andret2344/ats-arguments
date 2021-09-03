/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.FallbackException;
import eu.andret.arguments.Util;
import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.api.annotation.Mapper;
import eu.andret.arguments.api.annotation.TypeFallback;
import eu.andret.arguments.entity.MappingConfig;
import eu.andret.arguments.entity.MappingSet;
import eu.andret.arguments.mapper.IFallbackInvoker;
import eu.andret.arguments.mapper.IMethodInvoker;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.Value;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * An implementation of {@link IMethodInvoker}.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
@Value
@AllArgsConstructor
@Getter(AccessLevel.NONE)
public class MethodInvoker<E extends JavaPlugin> implements IMethodInvoker<E> {
	Map<CommandSender, AnnotatedCommandExecutor<E>> executors = new HashMap<>();
	JavaPlugin plugin;
	MappingConfig mappingConfig;
	IFallbackInvoker<E> fallbackInvoker;
	RealMethodInvoker<E> realMethodInvoker;

	/**
	 * Smallest acceptable constructor.
	 *
	 * @param plugin The plugin.
	 */
	public MethodInvoker(final JavaPlugin plugin) {
		this(plugin, new MappingConfig(), new FallbackInvoker<>(plugin), new RealMethodInvoker<>(plugin));
	}

	/**
	 * Medium acceptable constructor.
	 *
	 * @param plugin The plugin.
	 */
	public MethodInvoker(final JavaPlugin plugin, final MappingConfig mappingConfig) {
		this(plugin, mappingConfig, new FallbackInvoker<>(plugin), new RealMethodInvoker<>(plugin));
	}

	@Override
	@Nullable
	@SneakyThrows
	public Object invokeMethod(final Method method, final String[] command, final CommandSender sender,
							   final Class<? extends AnnotatedCommandExecutor<E>> executor, final Object... parameters) {
		try {
			final Object[] data = recalculateArguments(method, command);
			return realMethodInvoker.realCallMethod(List.of(method), data, sender, executor, parameters);
		} catch (final FallbackException ex) {
			final List<Method> methods = fallbackInvoker.invokeFallback(ex.getMapper(), ex.getTargetClass(), executor);
			return realMethodInvoker.realCallMethod(methods, new Object[]{ex.getValue()}, sender, executor, parameters);
		}
	}

	private Object[] recalculateArguments(final Method method, final String... args) {
		final Argument argument = method.getAnnotation(Argument.class);
		final TypeFallback typeFallback = method.getAnnotation(TypeFallback.class);
		final Object[] data = new Object[method.getParameterCount()];
		int skip = 0;
		for (int i = 0; i < method.getParameterCount(); i++) {
			if (typeFallback != null && i == 0 || i == argument.position()) {
				skip++;
			}
			final Mapper mapper = method.getParameters()[i].getAnnotation(Mapper.class);
			if (method.getParameters()[i].isVarArgs()) {
				final Class<?> type = method.getParameters()[i].getType().getComponentType();
				final int length = args.length - i + skip - 2;
				final Object array = Array.newInstance(type, length);
				for (int j = 0; j < length; j++) {
					Array.set(array, j, map(mapper, type, args[j + i + skip]));
				}
				data[i] = array;
				break;
			} else {
				data[i] = map(mapper, method.getParameters()[i].getType(), args[i + skip]);
			}
		}
		return data;
	}

	private Object map(final Mapper mapper, final Class<?> type, final String value) {
		return getMatchingMappingSet(mapper, type)
				.map(mappingSet -> convert(mapper, mappingSet, type, value))
				.orElseGet(() -> Util.convert(type, value));
	}

	private Optional<? extends MappingSet<?>> getMatchingMappingSet(final Mapper mapper, final Class<?> clazz) {
		final Optional<? extends MappingSet<?>> mappingSet = Optional.ofNullable(mapper)
				.map(Mapper::value)
				.map(mappingConfig::get)
				.filter(set -> set.getClazz().equals(clazz));
		if (mappingSet.isPresent()) {
			return mappingSet;
		}
		return Optional.of(clazz).map(mappingConfig::get);
	}

	private Object convert(final Mapper mapper, final MappingSet<?> mappingSet, final Class<?> targetClass,
						   final String value) {
		final Object result = mappingSet.getFunction().apply(value);
		if (mappingSet.getFallbackCondition().test(result)) {
			throw new FallbackException("Fallback condition failed", mapper, targetClass, value);
		}
		return targetClass.cast(result);
	}

	@SuppressWarnings("unchecked")
	@SneakyThrows
	private <A extends AnnotatedCommandExecutor<E>> A createInstance(final CommandSender sender, final Class<A> executor, final Object... parameters) {
		if (executors.containsKey(sender)) {
			return (A) executors.get(sender);
		}
		final Constructor<A> c = findMatchingConstructor(executor)
				.orElseThrow(() -> new IllegalStateException("AnnotatedCommandExecutor subclass needs a constructor with at least 2 parameters: CommandSender and JavaPlugin as first two of them"));
		final Object[] o = new Object[parameters.length + 2];
		o[0] = sender;
		o[1] = plugin;
		System.arraycopy(parameters, 0, o, 2, parameters.length);
		final A result = c.newInstance(o);
		executors.put(sender, result);
		return result;
	}

	@SuppressWarnings({"unchecked", "java:S1612"})
	private <A extends AnnotatedCommandExecutor<E>> Optional<Constructor<A>> findMatchingConstructor(final Class<A> executor) {
		return Arrays.stream(executor.getDeclaredConstructors())
				.filter(c -> c.getParameterCount() >= 2)
				.filter(c -> c.getParameterTypes()[0].isAssignableFrom(CommandSender.class))
				.filter(c -> c.getParameterTypes()[1].isAssignableFrom(plugin.getClass()))
				.map(x -> (Constructor<A>) x)
				.findAny();
	}
}
