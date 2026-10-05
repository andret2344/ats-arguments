package eu.andret.arguments.mapper;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.IMapper;
import eu.andret.arguments.entity.ExecutionCall;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

/**
 * The interface to select the selected method.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
@FunctionalInterface
public interface IMethodSelector extends IMapper {
	/**
	 * Selects what to call for the command: the method with its arguments converted from the strings, or the fallback
	 * methods with the failed value when a mapper's fallback condition matches.
	 *
	 * @param method The {@link Method} matched to the command.
	 * @param args The real command arguments array.
	 * @param executorClass The class to search for the fallback methods in.
	 * @param <E> The {@link JavaPlugin} subclass.
	 *
	 * @return The methods to call and the arguments to call them with.
	 */
	@NotNull <E extends JavaPlugin> ExecutionCall selectMethod(@NotNull Method method,
			@NotNull String[] args,
			@NotNull Class<? extends AnnotatedCommandExecutor<E>> executorClass);
}
