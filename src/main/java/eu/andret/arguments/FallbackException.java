/*
 * Copyright Andret (c) 2018-2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

/**
 * The exception thrown when parameter patches its edge value.
 *
 * @author Andret
 * @since Jun 10, 2020
 */
public class FallbackException extends RuntimeException {
	private static final long serialVersionUID = -4641609660635621400L;

	/**
	 * Constructor for the exception.
	 *
	 * @param message The exception message.
	 */
	public FallbackException(String message) {
		super(message);
	}
}
