/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.IMapper;

import java.lang.reflect.Method;

/**
 * An interface to map {@link Method} to its description.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
@FunctionalInterface
public interface IMethodToDescriptionMapper extends IMapper {
	/**
	 * Method that maps {@link Method} to its description.
	 *
	 * @param method The {@link Method} to be mapped.
	 * @param command The base command of the argument.
	 *
	 * @return The created description.
	 */
	String mapMethodToDescription(Method method, String command);
}
