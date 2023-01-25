/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.AnnotatedCommand;
import eu.andret.arguments.CommandTree;
import eu.andret.arguments.IMapper;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Interface for mapping command to exact method to be called.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
public interface ICommandToMethodMapper extends IMapper {
	/**
	 * @param commandTree The {@link CommandTree} instance.
	 * @param command The arguments array that followed up base command.
	 * @param sender The {@link CommandSender} of the command.
	 * @param options The command options.
	 *
	 * @return The Optional wrapping matching method that will be called, or {@link Optional#empty()} if none found.
	 */
	Optional<Method> mapCommandToMethod(@NotNull CommandTree<? extends JavaPlugin> commandTree,
										@NotNull String[] command,
										@NotNull CommandSender sender,
										@NotNull AnnotatedCommand.Options options);
}
