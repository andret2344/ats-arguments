/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import java.lang.reflect.Method;

/**
 * An interface for mapping arguments.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
public interface IArgumentsMapper extends IMapper {
	/**
	 * @param method The {@link Method} that will be analyzed.
	 * @param command The array of {@link String} with arguments passed with command.
	 *
	 * @return {@code true} if {@code method} matches command arguments and mappers input arguments,
	 *        {@code false} otherwise.
	 */
	boolean mapArguments(Method method, String[] command);
}
