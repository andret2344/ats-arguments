package eu.andret.arguments.mapper;

import eu.andret.arguments.AnnotatedCommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.List;

/**
 * The interface to invoke the method and handle thrown exception and call
 * {@link eu.andret.arguments.api.annotation.ExceptionFallback} method.
 *
 * @author Andret
 * @since Nov 30, 2022
 */
public interface IExceptionHandler {
	/**
	 * Invokes the passed method and if exception occurred, trying to find matching exception fallback method to call
	 * instead, or rethrow exception if found none.
	 *
	 * @param method The {@link Method} that can throw an exception.
	 * @param executor The executor that the method is called on.
	 * @param data The arguments to pass to invoked method.
	 * @param executorMethods Methods to search if there is any that handles the thrown exception, if thrown.
	 * @param <E> The {@link JavaPlugin} subclass.
	 *
	 * @return Common result from the called method if no exception thrown or all exception fallbacks in random order.
	 */
	@NotNull <E extends JavaPlugin> List<String> handleException(@NotNull final Method method,
																 @NotNull final AnnotatedCommandExecutor<E> executor,
																 @NotNull final Object[] data,
																 @NotNull final Method[] executorMethods);
}
