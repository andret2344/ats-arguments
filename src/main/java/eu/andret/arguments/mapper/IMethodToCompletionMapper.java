/*
 * Copyright Andret (c) 2018-2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * An interface to map {@link Method} to its completion suggestions.
 *
 * @author Andret
 * @since Nov 07, 2020
 */
public interface IMethodToCompletionMapper extends IMapper {
	Collection<String> mapCommandToCompletion(Method m, String[] args, CommandSender sender);
}
