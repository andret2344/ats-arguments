/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.IMapper;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * An interface to map {@link Method} to its completion suggestions.
 *
 * @author Andret
 * @since Nov 07, 2020
 */
@FunctionalInterface
public interface IMethodToCompletionMapper extends IMapper {
	/**
	 * The method that maps method and typed arguments {@link Collection} of suggestions.
	 *
	 * @param method The method on which basis suggestions are going to be created
	 * @param args Arguments that has already been typed into the command line.
	 * @param sender The command sender.
	 *
	 * @return {@link Collection} of possible suggestions.
	 */
	Collection<String> mapCommandToCompletion(Method method, String[] args, CommandSender sender);
}
