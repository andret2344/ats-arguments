/*
 * Copyright Andret (c) 2019. Copying and modifying allowed only keeping git link reference.
 */
package eu.andret.arguments;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The command executor to use instead of {@link org.bukkit.command.CommandExecutor}.
 * Should be registered as default command executor.
 *
 * @author Andret
 * @see org.bukkit.command.CommandExecutor
 * @see eu.andret.arguments.LocalCommandExecutor
 * @since May 18, 2019
 */
public class LocalCommandExecutor implements CommandExecutor {
	private static final Map<CommandSender, AnnotatedCommandExecutor> executors = new HashMap<>();
	private final Util util = Util.getInstance();
	private final Class<? extends AnnotatedCommandExecutor> commandExecutor;
	private final JavaPlugin plugin;
	private OnUnknownSubCommandExecutionListener onUnknownSubCommandExecutionListener;
	private OnInsufficientPermissionsListener onInsufficientPermissionsListener;
	private OnUsageExampleListener onUsageExampleListener = (sender, desc) -> true;
	private Debugger debugger =
			(level, message, args) -> System.out.println(message + Arrays.deepToString(args));

	/**
	 * Listener to define action when sender performs unknown sub-command.
	 */
	public interface OnUnknownSubCommandExecutionListener {
		/**
		 * Unknown sub-command executed.
		 *
		 * @param sender The sender that executed the unknown sub-command
		 */
		void unknownSubCommandExecuted(CommandSender sender);
	}

	/**
	 * Listener to define action when sender has insufficient permissions.
	 */
	public interface OnInsufficientPermissionsListener {
		/**
		 * Insufficient permissions.
		 *
		 * @param sender The sender that executed the command with no
		 * permissions.
		 */
		void insufficientPermissions(CommandSender sender);
	}

	/**
	 * The interface On usage example listener.
	 */
	public interface OnUsageExampleListener {
		/**
		 * Usage example boolean.
		 *
		 * @param sender the sender
		 * @param description the description
		 *
		 * @return the boolean
		 */
		boolean usageExample(CommandSender sender, String description);
	}

	public interface Debugger {
		void debug(Level level, String message, Object... args);
	}

	/**
	 * Constructs the LocalCommandExecutor.
	 *
	 * @param commandExecutor The {@link eu.andret.arguments.AnnotatedCommandExecutor}
	 * that will be analized in search of methods annotated with {@link
	 * eu.andret.arguments.Argument}
	 * @param plugin The {@link org.bukkit.plugin.java.JavaPlugin} superclass of
	 * main plugin class.
	 */
	public LocalCommandExecutor(Class<? extends AnnotatedCommandExecutor> commandExecutor, JavaPlugin plugin) {
		this.commandExecutor = commandExecutor;
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		debugger.debug(Level.FINE, "Got command %s with args %s",
				cmd, Arrays.deepToString(args));
		List<Method> methodList = Stream.of(commandExecutor.getDeclaredMethods())
				.filter(m -> m.getAnnotation(Argument.class) != null)
				.filter(m -> m.getAnnotation(Argument.class).value().equalsIgnoreCase(cmd.getName()))
				.filter(m -> !Modifier.isStatic(m.getModifiers()))
				.collect(Collectors.toList());
		if (args.length == 0) {
			debugger.debug(Level.FINE, "No args provided, performing default " +
					"action");
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
			debugger.debug(Level.FINE, "No matching method by name found");
			if (onUnknownSubCommandExecutionListener != null) {
				onUnknownSubCommandExecutionListener.unknownSubCommandExecuted(sender);
			}
			return true;
		}
		List<Class<?>> list = Stream.of(args).skip(1).map(util::getRealClass).collect(Collectors.toList());
		Method method = inferMethod(methods, list);
		if (method == null) {
			debugger.debug(Level.FINE, "No matching method by parameters " +
					"found");
			methods.stream()
					.filter(m -> onUsageExampleListener != null)
					.filter(m -> onUsageExampleListener.usageExample(sender, getDescription(cmd.getName(), m)))
					.forEach(m -> sender.sendMessage("Usage: " + getDescription(cmd.getName(), m)));
			return true;
		}
		debugger.debug(Level.FINE, "Found method " + method);
		if (!hasPermission(sender, method)) {
			if (onInsufficientPermissionsListener != null) {
				onInsufficientPermissionsListener.insufficientPermissions(sender);
			}
			return true;
		}
		debugger.debug(Level.FINE, "Permissions ok");
		Object[] data = recalculateArguments(method, args);
		Object result = invoke(method, sender, data);
		if (result == null) {
			debugger.debug(Level.FINE, "No method result");
			return true;
		}
		sendProperResponse(sender, result, method);
		return true;
	}

	/**
	 * Sets on unknown sub command execution listener.
	 *
	 * @param listener The {@link OnUnknownSubCommandExecutionListener}
	 */
	public void setOnUnknownSubCommandExecutionListener(OnUnknownSubCommandExecutionListener listener) {
		onUnknownSubCommandExecutionListener = listener;
	}

	/**
	 * Sets on insufficient permissions' listener.
	 *
	 * @param listener The {@link OnInsufficientPermissionsListener}
	 */
	public void setOnInsufficientPermissionsListener(OnInsufficientPermissionsListener listener) {
		onInsufficientPermissionsListener = listener;
	}

	/**
	 * Sets on usage example listener.
	 *
	 * @param listener The {@link OnUsageExampleListener}
	 */
	public void setOnUsageExampleListener(OnUsageExampleListener listener) {
		onUsageExampleListener = listener;
	}

	public void setDebugger(Debugger debugger) {
		if (debugger != null) {
			this.debugger = debugger;
		}
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
		plugin.getLogger().log(Level.FINE, "Sending response {0}", message);
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
				plugin.getLogger().log(Level.FINE, "No response sent");
				break;
		}
	}

	private Object[] recalculateArguments(Method method, String... args) {
		plugin.getLogger().log(Level.FINER, "Recalculating arguments for " +
						"method {0} with args {1}",
				new Object[]{method, Arrays.deepToString(args)});
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
		plugin.getLogger().log(Level.FINER, "Recalculated: {0}",
				Arrays.deepToString(data));
		return data;
	}

	private Object invoke(Method method, CommandSender sender, Object... data) {
		debugger.debug(Level.FINER, "Trying to invoke {0} with {1}",
				method, Arrays.deepToString(data));
		try {
			debugger.debug(Level.FINER, "try {...");
			if (!executors.containsKey(sender)) {
				debugger.debug(Level.FINER, "if (map not contains sender)");
				executors.put(sender, commandExecutor.getDeclaredConstructor(CommandSender.class, JavaPlugin.class).newInstance(sender, plugin));
			}
			debugger.debug(Level.FINER, "invoke method");
			return method.invoke(executors.get(sender), data);
		} catch (ReflectiveOperationException e) {
			debugger.debug(Level.SEVERE, e.toString());
		}
		debugger.debug(Level.FINER, "NULL?!");
		return null;
	}

	private String getCommandPattern(Method method) {
		plugin.getLogger().log(Level.FINER, "Generating help pattern for {0}",
				method);
		Parameter[] params = method.getParameters();
		Argument a = method.getAnnotation(Argument.class);
		StringBuilder message = new StringBuilder();
		if (params.length == 0) {
			return message.append(" ").append(method.getName()).toString();
		}
		for (int i = 0; i < params.length; i++) {
			if (i == a.position()) {
				message.append(" ").append(method.getName());
			}
			message.append(" <").append(params[i].getName());
			if (params[i].getType().isArray()) {
				message.append("...");
			}
			message.append((">"));
		}
		if (params.length == a.position()) {
			message.append(" ").append(method.getName());
		}
		plugin.getLogger().log(Level.FINER, "Generated {0}", message);
		return message.toString();
	}

	private Method inferMethod(List<Method> methods, List<Class<?>> classes) {
		plugin.getLogger().log(Level.FINER, "Trying to infer methods from {0}" +
				" using {1}", new Object[]{methods, classes});
		return methods.stream()
				.filter(m -> checkParameters(m, classes))
				.findFirst()
				.orElse(null);
	}

	private boolean checkParameters(Method method, List<Class<?>> classes) {
		plugin.getLogger().log(Level.FINER, "Checking params for {0} with {1}" +
				" with {1}", new Object[]{method, classes});
		List<Parameter> parameters = Stream.of(method.getParameters()).collect(Collectors.toList());
		int size = Math.min(classes.size(), method.getParameterCount());
		if (size == 0 && classes.size() + method.getParameterCount() != 0) {
			return false;
		}
		for (int i = 0; i < size; i++) {
			Parameter parameter = parameters.get(i);
			if (parameter.getType().isArray() && !parameter.isVarArgs()) {
				throw new IllegalArgumentException("Cannot be the classical " +
						"array! Use varargs instead. Method " + method);
			}
			if (parameter.isVarArgs()) {
				if (!isTypeMatchingVarArgParameter(parameter, classes, i)) {
					return false;
				}
			} else if (!classes.get(i).isAssignableFrom(parameter.getType())) {
				return false;
			}
		}
		return true;
	}

	private boolean isTypeMatchingVarArgParameter(Parameter parameter, List<Class<?>> classes, int i) {
		plugin.getLogger().log(Level.FINER, "Trying to match vararg for {0} " +
				" with {1}", new Object[]{parameter, classes});
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
