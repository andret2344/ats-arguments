/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.annotation.Argument;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * An interface to call methods.
 *
 * @param <E> The JavaPlugin instance.
 *
 * @author Andret
 * @since Sep 03, 2021
 */
public interface IInstanceCreator<E extends JavaPlugin> extends IMapper {
	/**
	 * @param sender The sender who executed the command.
	 * @param plugin The {@link JavaPlugin}.
	 * @param executorClass The class containing {@link Argument} methods.
	 * @param parameters The executor's constructor parameters.
	 * @param <A> The {@link AnnotatedCommandExecutor} class.
	 *
	 * @return List with results from called methods.
	 */
	@NotNull <A extends AnnotatedCommandExecutor<E>> A createInstance(@NotNull CommandSender sender,
																	  @NotNull JavaPlugin plugin,
																	  @NotNull Class<A> executorClass,
																	  @NotNull Object... parameters);
}
