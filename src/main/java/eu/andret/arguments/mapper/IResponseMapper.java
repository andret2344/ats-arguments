/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.List;

/**
 * The response mapper interface.
 *
 * @author Andret
 * @since Nov 25, 2021
 */
public interface IResponseMapper extends IMapper {
	/**
	 * Maps the response from Object to list of String.
	 *
	 * @param method Method, which return values is mapped. The method is NOT being called.
	 * @param result The result of passed method.
	 *
	 * @return The mapped values.
	 */
	@NotNull
	List<String> mapResponse(@NotNull Method method, @NotNull Object result);
}
