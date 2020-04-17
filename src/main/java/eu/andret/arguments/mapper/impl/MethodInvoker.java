package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.Util;
import eu.andret.arguments.annotation.Argument;
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
public class MethodInvoker {
	private static final Map<CommandSender, AnnotatedCommandExecutor> executors = new HashMap<>();
	JavaPlugin plugin;
	Util util = Util.getInstance();

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
			if (method.getParameters()[i].isVarArgs()) {
				Class<?> type = method.getParameters()[i].getType().getComponentType();
				int length = args.length - i + skip - 2;
				Object array = Array.newInstance(type, length);
				for (int j = 0; j < length; j++) {
					Array.set(array, j, util.convert(type, args[j + i + skip]));
				}
				data[i] = array;
				break;
			} else {
				data[i] = util.convert(method.getParameters()[i].getType(), args[i + skip]);
			}
		}
		return data;
	}

	private Object invoke(Method method, CommandSender sender, Class<? extends AnnotatedCommandExecutor> executor, Object... data) {
		try {
			if (!executors.containsKey(sender)) {
				Constructor<? extends AnnotatedCommandExecutor> constructor = executor.getDeclaredConstructor(CommandSender.class, JavaPlugin.class);
				executors.put(sender, constructor.newInstance(sender, plugin));
			}
			return method.invoke(executors.get(sender), data);
		} catch (ReflectiveOperationException e) {
			Bukkit.getLogger().throwing(getClass().getName(), "invoke", e);
		}
		return null;
	}
}
