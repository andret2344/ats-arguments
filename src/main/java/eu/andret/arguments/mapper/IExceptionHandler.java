package eu.andret.arguments.mapper;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.IMapper;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.List;

/**
 * The interface to invoke the method and handle a thrown exception and call a
 * {@link eu.andret.arguments.api.annotation.ExceptionFallback} method.
 *
 * @author Andret
 * @since Nov 30, 2022
 */
@FunctionalInterface
public interface IExceptionHandler extends IMapper {
	/**
	 * Invokes the passed method and if an exception occurs, tries to find a matching exception fallback method to call
	 * instead, or rethrow the exception if found none.
	 *
	 * @param method {@link Method} that may throw an exception.
	 * @param executor The executor on which the method has been called.
	 * @param data The arguments to be passed to invoked method.
	 * @param executorMethods Methods to search if any of them can handle the thrown exception.
	 * @param <E> The {@link JavaPlugin} subclass.
	 *
	 * @return Common result from the called method if no exceptions were thrown or the aggregated results from all
	 *        {@link eu.andret.arguments.api.annotation.ExceptionFallback} methods executed in random order. Rethrow any
	 * 		thrown exception that haven't been caught.
	 */
	@NotNull <E extends JavaPlugin> List<String> handleException(@NotNull Method method,
																 @NotNull AnnotatedCommandExecutor<E> executor,
																 @NotNull Object[] data,
																 @NotNull Method[] executorMethods);
}
