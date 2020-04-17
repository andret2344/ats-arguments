package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.Mapper;
import eu.andret.arguments.Util;
import eu.andret.arguments.annotation.Argument;
import eu.andret.arguments.annotation.Param;
import eu.andret.arguments.mapper.IMethodInvoker;
import lombok.Value;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Value
public class MethodInvoker implements IMethodInvoker {
	private static final Map<CommandSender, AnnotatedCommandExecutor> executors = new HashMap<>();
	Util util = Util.getInstance();
	JavaPlugin plugin;
	Map<String, Mapper<?>> mappers;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void invokeMethod(Method method, String[] command, CommandSender sender, Class<? extends AnnotatedCommandExecutor> executor) {
		Object[] data = recalculateArguments(method, command);
		Object result = invoke(method, sender, executor, data);
		sendProperResponse(sender, result, method);
	}

	private void sendProperResponse(CommandSender sender, Object obj, Method method) {
		if (obj == null) {
			return;
		}
		String message = String.valueOf(obj);
		switch (method.getAnnotation(Argument.class).responseType()) {
			case SENDER:
				sender.sendMessage(message);
				break;
			case CONSOLE:
				Bukkit.getLogger().info(message);
				break;
			case BROADCAST:
				Bukkit.broadcastMessage(message);
				break;
			case NONE:
			default:
				break;
		}
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
		System.out.println(param);
		if (param != null && mappers.containsKey(param.value())) {
			Mapper<?> mapper = mappers.get(param.value());
			System.out.println(mapper);
			if (mapper.getClazz().equals(c)) {
				return c.cast(mapper.getFunction().apply(value));
			}
		}
		return util.convert(c, value);
	}

	private Object invoke(Method method, CommandSender sender, Class<? extends AnnotatedCommandExecutor> executor, Object... data) {
		try {
			if (!executors.containsKey(sender)) {
				Constructor<? extends AnnotatedCommandExecutor> constructor = executor.getDeclaredConstructor(CommandSender.class, plugin.getClass());
				executors.put(sender, constructor.newInstance(sender, plugin));
			}
			return method.invoke(executors.get(sender), data);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
			plugin.getLogger().throwing(getClass().getName(), "invoke", e);
		}
		return null;
	}
}
