/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Interface for mapping  command to exact method to be called.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
public interface ICommandToMethodMapper {
	/**
	 * @param methods Methods annotated with {@link eu.andret.arguments.annotation.Argument}.
	 * @param command The arguments array that followed up base command.
	 * @param sender The {@link org.bukkit.command.CommandSender} of the command.
	 *
	 * @return The Optional wrapping matching method that will be called, or {@link
	 * java.util.Optional#empty()} if none found.
	 */
	Optional<Method> mapCommandToMethod(Method[] methods, String[] command, CommandSender sender);
}
