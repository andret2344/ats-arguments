package eu.andret.arguments;

import eu.andret.arguments.api.annotation.Mapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The exception thrown when parameter patches its edge value.
 *
 * @author Andret
 * @since Jun 10, 2020
 */
public final class FallbackException extends RuntimeException {
	private final transient Mapper mapper;
	/**
	 * The target class.
	 */
	private final Class<?> targetClass;
	/**
	 * The implicit string.
	 */
	private final String value;

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

	/**
	 * Gets the mapper.
	 *
	 * @return The {@link Mapper} that couldn't produce value.
	 */
	@Nullable
	public Mapper getMapper() {
		return mapper;
	}

	/**
	 * Gets the target class.
	 *
	 * @return The target class.
	 */
	@NotNull
	public Class<?> getTargetClass() {
		return targetClass;
	}

	/**
	 * Gets the value.
	 *
	 * @return The implicit string.
	 */
	@NotNull
	public String getValue() {
		return value;
	}
}
