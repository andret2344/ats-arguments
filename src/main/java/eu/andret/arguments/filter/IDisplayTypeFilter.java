/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.filter;

import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;

/**
 * An interface for mapping display type.
 *
 * @author Andret
 * @since May 08, 2020
 */
@FunctionalInterface
public interface IDisplayTypeFilter {
	/**
	 * Maps the argument's display type.
	 *
	 * @param method The method where Annotation should be got from.
	 * @param sender The sender who invoked the command.
	 *
	 * @return {@code true} if argument should be visible in held, {@code false} otherwise.
	 */
	boolean mapDisplayType(Method method, CommandSender sender);
}
