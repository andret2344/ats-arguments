package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.annotation.ExceptionFallback;
import eu.andret.arguments.mapper.IExceptionHandler;
import eu.andret.arguments.mapper.IMethodInvoker;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The {@link IExceptionHandler} implementation.
 *
 * @author Andret
 * @since Nov 30, 2022
 */
public class ExceptionHandler implements IExceptionHandler {
	private final IMethodInvoker methodInvoker;

	/**
	 * A constructor.
	 *
	 * @param methodInvoker The invoker of the methods.
	 */
	public ExceptionHandler(final IMethodInvoker methodInvoker) {
		this.methodInvoker = methodInvoker;
	}

	@NotNull
	@Override
	public <E extends JavaPlugin> List<String> handleException(@NotNull final Method method,
			@NotNull final AnnotatedCommandExecutor<E> executor,
			@NotNull final Object[] data,
			@NotNull final Method[] executorMethods)
			throws ReflectiveOperationException {
		try {
			return methodInvoker.invokeMethod(method, executor, data);
		} catch (final InvocationTargetException exception) {
			final Throwable cause = exception.getCause();
			final Map<Boolean, List<Method>> groupedMethods = getGroupedMethods(executorMethods, cause.getClass());
			if (groupedMethods.isEmpty()) {
				throw new RuntimeException(cause);
			}
			final List<String> result = new ArrayList<>();
			for (final Map.Entry<Boolean, List<Method>> entry : groupedMethods.entrySet()) {
				for (final Method fallbackMethod : entry.getValue()) {
					result.addAll(methodInvoker.invokeMethod(fallbackMethod, executor, getArguments(entry.getKey(), cause)));
				}
			}
			return result;
		}
	}

	@NotNull
	private Map<Boolean, List<Method>> getGroupedMethods(@NotNull final Method[] executorMethods, @NotNull final Class<? extends Throwable> exceptionClass) {
		return Arrays.stream(executorMethods)
				.filter(method -> method.isAnnotationPresent(ExceptionFallback.class))
				.filter(method -> methodHandlesException(exceptionClass, method))
				.collect(Collectors.groupingBy(method1 -> method1.getParameterCount() == 0));
	}

	private boolean methodHandlesException(@NotNull final Class<? extends Throwable> exceptionClass, @NotNull final Method method) {
		final ExceptionFallback declaredAnnotation = method.getDeclaredAnnotation(ExceptionFallback.class);
		final Class<? extends Throwable>[] value = declaredAnnotation.value();
		return Arrays.asList(value).contains(exceptionClass);
	}

	@NotNull
	@Contract(value = "_, _ -> new", pure = true)
	private Object @NotNull [] getArguments(final boolean value, @NotNull final Throwable exception) {
		if (value) {
			return new Object[0];
		}
		return new Object[]{exception};
	}
}
