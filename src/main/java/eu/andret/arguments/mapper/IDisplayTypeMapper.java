/*
 * Copyright Andret (c) 2018-2020 Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;

/**
 * An interface for mapping display type.
 *
 * @author Andret
 * @since May 08, 2020
 */
public interface IDisplayTypeMapper {
	/**
	 * Maps the argument's display type.
	 *
	 * @param method The method where Annotation should be get from.
	 * @param sender The sender who invoked the command.
	 *
	 * @return {@code true} if argument should be visible in held, {@code false} otherwise.
	 */
	boolean mapDisplayType(Method method, CommandSender sender);
}
