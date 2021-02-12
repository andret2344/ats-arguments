/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.entity.ExecutionCall;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Interface for mapping  command to exact method to be called.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
public interface ICommandToMethodMapper extends IMapper {
	/**
	 * @param methods Methods annotated with {@link Argument}.
	 * @param command The arguments array that followed up base command.
	 * @param sender The {@link CommandSender} of the command.
	 *
	 * @return The Optional wrapping matching method that will be called, or {@link
	 *        Optional#empty()} if none found.
	 */
	Optional<ExecutionCall> mapCommandToMethod(Method[] methods, String[] command, CommandSender sender);
}
