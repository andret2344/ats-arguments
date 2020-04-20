/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.Mapper;
import eu.andret.arguments.Util;
import eu.andret.arguments.annotation.Argument;
import eu.andret.arguments.annotation.Param;
import eu.andret.arguments.mapper.IMethodInvoker;
import lombok.AccessLevel;
import lombok.Getter;
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
import java.util.logging.Level;

/**
 * An implementation of {@link eu.andret.arguments.mapper.IMethodInvoker}.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
@Value
@Getter(AccessLevel.NONE)
public class MethodInvoker implements IMethodInvoker {
	private static final Map<CommandSender, AnnotatedCommandExecutor> EXECUTORS = new HashMap<>();
	JavaPlugin plugin;
	Map<String, Mapper<?>> mappers;

	@Override
	public Object invokeMethod(Method method, String[] command, CommandSender sender, Class<? extends AnnotatedCommandExecutor> executor) {
		Object[] data = recalculateArguments(method, command);
		return invoke(method, sender, executor, data);
	}

	private Object[] recalculateArguments(Method method, String... args) {
		Argument argument = method.getAnnotation(Argument.class);
		Object[] data = new Object[method.getParameterCount()];
		int skip = 0;
		for (int i = 0; i < method.getParameterCount(); i++) {
			if (i == argument.position()) {
				skip++;
			}
			Param param = method.getParameters()[i].getAnnotation(Param.class);
			if (method.getParameters()[i].isVarArgs()) {
				Class<?> type = method.getParameters()[i].getType().getComponentType();
				int length = args.length - i + skip - 2;
				Object array = Array.newInstance(type, length);
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

	private Object convert(Param param, Class<?> c, String value) {
		if (param != null && mappers.containsKey(param.value())) {
			Mapper<?> mapper = mappers.get(param.value());
			if (mapper.getClazz().equals(c)) {
				return c.cast(mapper.getFunction().apply(value));
			}
		}
		return Util.convert(c, value);
	}

	private <E extends AnnotatedCommandExecutor> Object invoke(Method method, CommandSender sender, Class<E> executor, Object... data) {
		try {
			if (!EXECUTORS.containsKey(sender)) {
				Optional<Constructor<?>> optionalConstructor = findMatchingConstructor(executor);
				if (optionalConstructor.isPresent()) {
					Constructor<?> c = optionalConstructor.get();
					EXECUTORS.put(sender, (AnnotatedCommandExecutor) c.newInstance(sender, plugin));
				} else {
					throw new IllegalStateException("AnnotatedCommandExecutor subclass has to contain a constructor that takes 2 parameters: CommandSender and JavaPlugin");
				}
			}
			return method.invoke(EXECUTORS.get(sender), data);
		} catch (ReflectiveOperationException e) {
			plugin.getLogger().log(Level.SEVERE, e, String::new);
		}
		return null;
	}

	private Optional<Constructor<?>> findMatchingConstructor(Class<?> executor) {
		return Arrays.stream(executor.getDeclaredConstructors())
				.filter(c -> c.getParameterCount() == 2)
				.filter(c -> c.getParameterTypes()[0].isAssignableFrom(CommandSender.class))
				.filter(c -> c.getParameterTypes()[1].isAssignableFrom(plugin.getClass()))
				.findAny();
	}
}
