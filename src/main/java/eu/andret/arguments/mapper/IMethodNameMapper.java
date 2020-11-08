/*
 * Copyright Andret (c) 2018-2020 Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import java.lang.reflect.Method;

/**
 * An interface to map method name due to command arguments.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
public interface IMethodNameMapper extends IMapper {
	/**
	 * The method that maps method to right command argument.
	 *
	 * @param method The {@link java.lang.reflect.Method} that will be mapped.
	 * @param command The command arguments.
	 *
	 * @return {@code true} if method's name matches argument at correct position, {@code false}
	 * 		otherwise.
	 */
	boolean mapMethodName(Method method, String[] command);
}
