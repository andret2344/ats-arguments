/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.FallbackException;
import eu.andret.arguments.IMapper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

/**
 * The interface to select the selected method.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
public interface IMethodSelector extends IMapper {
	/**
	 * Invokes one of  methods inside method and correctly puts all arguments.
	 *
	 * @param method The {@link Method} containing method to be called.
	 * @param args The real command arguments array.
	 *
	 * @return The result contains created objects from Strings.
	 *
	 * @throws FallbackException if fallback condition matches.
	 */
	@NotNull
	Object[] recalculateArguments(@NotNull Method method, @NotNull String... args);
}
