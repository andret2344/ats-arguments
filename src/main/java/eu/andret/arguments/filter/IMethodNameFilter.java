/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.filter;

import eu.andret.arguments.AnnotatedCommand;
import eu.andret.arguments.IMapper;

import java.lang.reflect.Method;

/**
 * An interface to map method name due to command arguments.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
@FunctionalInterface
public interface IMethodNameFilter extends IMapper {
	/**
	 * The method that maps method to right command argument.
	 *
	 * @param method The {@link Method} that will be mapped.
	 * @param command The command arguments.
	 * @param options The command options.
	 *
	 * @return {@code true} if method's name matches argument at correct position, {@code false} otherwise.
	 */
	boolean filterMethodName(Method method, String[] command, AnnotatedCommand.Options options);
}
