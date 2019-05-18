/*
 * Copyright Andret (c) 2019. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.argument;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type Local command executorType.
 */
public class LocalCommandExecutor implements CommandExecutor {
	private static final Map<CommandSender, AnnotatedCommandExecutor> executors = new HashMap<>();
	private final Util util = Util.getInstance();
	private final Class<? extends AnnotatedCommandExecutor> commandExecutor;
	private final JavaPlugin plugin;
	private OnUnknownSubCommandExecutionListener onUnknownSubCommandExecutionListener;
	private OnInsufficientPermissionsListener onInsufficientPermissionsListener;
	private OnUsageExampleListener onUsageExampleListener = (sender, desc) -> true;

	public interface OnUnknownSubCommandExecutionListener {
		void unknownSubCommandExecuted(CommandSender sender);
	}

	public interface OnInsufficientPermissionsListener {
		void insufficientPermissions(CommandSender sender);
	}

	public interface OnUsageExampleListener {
		boolean usageExample(CommandSender sender, String description);
	}

	/**
	 * Instantiates a new Local command executorType.
	 *
	 * @param commandExecutor the command executorType
	 */
	public LocalCommandExecutor(Class<? extends AnnotatedCommandExecutor> commandExecutor, JavaPlugin plugin) {
		this.commandExecutor = commandExecutor;
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		List<Method> methodList = Stream.of(commandExecutor.getDeclaredMethods())
				.filter(m -> m.getAnnotation(Argument.class) != null)
				.filter(m -> m.getAnnotation(Argument.class).value().equalsIgnoreCase(cmd.getName()))
				.filter(m -> !Modifier.isStatic(m.getModifiers()))
				.collect(Collectors.toList());
		if (args.length == 0) {
			methodList.stream()
					.filter(m -> m.getAnnotation(Argument.class).showIfNoPerms() || hasPermission(sender, m))
					.map(m -> getDescription(cmd.getName(), m))
					.forEach(sender::sendMessage);
			return true;
		}
		List<Method> methods = methodList.stream()
				.filter(m -> args.length >= m.getAnnotation(Argument.class).position())
				.filter(m -> m.getName().equalsIgnoreCase(args[m.getAnnotation(Argument.class).position()]))
				.filter(m -> {
					ExecutorType executorType = m.getAnnotation(Argument.class).executorType();
					return executorType.equals(ExecutorType.ALL)
							|| executorType.equals(ExecutorType.CONSOLE) && sender instanceof ConsoleCommandSender
							|| executorType.equals(ExecutorType.PLAYER) && sender instanceof Player;

				})
				.collect(Collectors.toList());
		if (methods.isEmpty()) {
			if (onUnknownSubCommandExecutionListener != null) {
				onUnknownSubCommandExecutionListener.unknownSubCommandExecuted(sender);
			}
			return true;
		}
		List<Class<?>> list = Stream.of(args).skip(1).map(util::getRealClass).collect(Collectors.toList());
		Method method = inferMethod(methods, list);
		if (method == null) {
			methods.stream()
					.filter(m -> onUsageExampleListener != null)
					.filter(m -> onUsageExampleListener.usageExample(sender, getDescription(cmd.getName(), m)))
					.forEach(m -> sender.sendMessage("Usage: " + getDescription(cmd.getName(), m)));
			return true;
		}
		if (!hasPermission(sender, method)) {
			if (onInsufficientPermissionsListener != null) {
				onInsufficientPermissionsListener.insufficientPermissions(sender);
			}
			return true;
		}
		Object[] data = recalculateArguments(method, args);
		Object result = invoke(method, sender, data);
		if (result == null) {
			return true;
		}
		sendProperResponse(sender, result, method);
		return true;
	}

	public void setOnUnknownSubCommandExecutionListener(OnUnknownSubCommandExecutionListener listener) {
		onUnknownSubCommandExecutionListener = listener;
	}

	public void setOnInsufficientPermissionsListener(OnInsufficientPermissionsListener listener) {
		onInsufficientPermissionsListener = listener;
	}

	public void setOnUsageExampleListener(OnUsageExampleListener listener) {
		onUsageExampleListener = listener;
	}

	private boolean hasPermission(CommandSender sender, Method method) {
		Argument argument = method.getAnnotation(Argument.class);
		if (argument == null) {
			return false;
		}
		return !(sender instanceof Player) || sender.hasPermission(argument.permission()) && !argument.permission().equals("");
	}

	private void sendProperResponse(CommandSender sender, Object obj, Method method) {
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

	private Object invoke(Method method, CommandSender sender, Object... data) {
		try {
			if (!executors.containsKey(sender)) {
				executors.put(sender, commandExecutor.getDeclaredConstructor(CommandSender.class, JavaPlugin.class).newInstance(sender, plugin));
			}
			return method.invoke(executors.get(sender), data);
		} catch (ReflectiveOperationException e) {
			Bukkit.getLogger().throwing(getClass().getName(), "onCommand", e);
		}
		return null;
	}

	private String getCommandPattern(Method m) {
		Parameter[] params = m.getParameters();
		Argument a = m.getAnnotation(Argument.class);
		StringBuilder message = new StringBuilder();
		if (params.length == 0) {
			return message.append(" ").append(m.getName()).toString();
		}
		for (int i = 0; i < params.length; i++) {
			if (i == a.position()) {
				message.append(" ").append(m.getName());
			}
			message.append(" <").append(params[i].getName());
			if (params[i].getType().isArray()) {
				message.append("...");
			}
			message.append((">"));
		}
		if (params.length == a.position()) {
			message.append(" ").append(m.getName());
		}
		return message.toString();
	}

	private Method inferMethod(List<Method> methods, List<Class<?>> classes) {
		return methods.stream()
				.filter(m -> checkParameters(m, classes))
				.findFirst()
				.orElse(null);
	}

	private boolean checkParameters(Method method, List<Class<?>> classes) {
		List<Parameter> parameters = Stream.of(method.getParameters()).collect(Collectors.toList());
		int size = Math.min(classes.size(), method.getParameterCount());
		if (size == 0 && classes.size() + method.getParameterCount() != 0) {
			return false;
		}
		for (int i = 0; i < size; i++) {
			Parameter parameter = parameters.get(i);
			if (parameter.getType().isArray() && !parameter.isVarArgs()) {
				throw new IllegalArgumentException("Cannot be classical array! Use varargs instead. "
						+ "Method " + method);
			}
			if (parameter.isVarArgs()) {
				if (!isTypeMatchungVarArgParameter(parameter, classes, i)) {
					return false;
				}
			} else if (!classes.get(i).isAssignableFrom(parameter.getType())) {
				return false;
			}
		}
		return true;
	}

	private boolean isTypeMatchungVarArgParameter(Parameter parameter, List<Class<?>> classes, int i) {
		Class<?> varArgType = parameter.getType().getComponentType();
		for (int j = i; j < classes.size(); j++) {
			if (!classes.get(j).isAssignableFrom(varArgType)) {
				return false;
			}
		}
		return true;
	}

	private String getDescription(String command, Method method) {
		String message = "/" + command + getCommandPattern(method);
		String description = method.getAnnotation(Argument.class).description();
		if (!description.isEmpty()) {
			message += " - " + description;
		}
		return message.replace('&', '\u00A7');
	}
}
