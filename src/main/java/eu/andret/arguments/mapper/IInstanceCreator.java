package eu.andret.arguments.mapper;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.IMapper;
import eu.andret.arguments.api.annotation.Argument;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * An interface to create {@link AnnotatedCommandExecutor} instance.
 *
 * @author Andret
 * @since Sep 03, 2021
 */
@FunctionalInterface
public interface IInstanceCreator extends IMapper {
	/**
	 * @param sender The sender who executed the command.
	 * @param plugin The {@link JavaPlugin}.
	 * @param executorClass The class containing {@link Argument} methods.
	 * @param parameters The executor's constructor parameters.
	 * @param <E> The {@link JavaPlugin} subclass.
	 * @param <A> The {@link AnnotatedCommandExecutor} class.
	 *
	 * @return List with results from called methods.
	 */
	@NotNull <E extends JavaPlugin, A extends AnnotatedCommandExecutor<E>> A createInstance(
			@NotNull CommandSender sender,
			@NotNull JavaPlugin plugin,
			@NotNull Class<A> executorClass,
			@NotNull Object... parameters);
}
