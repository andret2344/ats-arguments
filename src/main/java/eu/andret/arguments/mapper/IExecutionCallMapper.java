/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.entity.ExecutionCall;

import java.lang.reflect.Method;

/**
 * The interface for mapping execution call.
 *
 * @author Andret
 * @since Jun 10, 2020
 */
public interface IExecutionCallMapper extends IMapper {
	/**
	 * The mapper method.
	 *
	 * @param method The method that is being mapped.
	 * @param methods All methods where {@link eu.andret.arguments.annotation.Fallback} annotated
	 * 		method will be looked for.
	 *
	 * @return The {@link ExecutionCall} of command.
	 */
	ExecutionCall mapExecutionCall(Method method, Method[] methods);
}
