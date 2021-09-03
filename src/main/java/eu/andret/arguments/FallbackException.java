/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.api.annotation.Mapper;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The exception thrown when parameter patches its edge value.
 *
 * @author Andret
 * @since Jun 10, 2020
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class FallbackException extends RuntimeException {
	transient Mapper mapper;
	Class<?> targetClass;
	String value;

	/**
	 * Constructor for the exception.
	 *
	 * @param message The exception message.
	 * @param mapper The {@link Mapper} that couldn't produce value.
	 * @param targetClass The target class.
	 * @param value The implicit string.
	 */
	public FallbackException(@Nullable final String message, @Nullable final Mapper mapper,
							 @NotNull final Class<?> targetClass, @NotNull final String value) {
		super(message);
		this.mapper = mapper;
		this.value = value;
		this.targetClass = targetClass;
	}
}
