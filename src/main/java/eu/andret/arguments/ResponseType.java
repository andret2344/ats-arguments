/*
 * Copyright Andret (c) 2019. Copying and modifying allowed only keeping git link reference.
 */
package eu.andret.arguments;

/**
 * Describes who will get the result from executed method. Used in {@link
 * eu.andret.arguments.Argument} to decide what will happen to method result.
 *
 * @author Andret
 * @see eu.andret.arguments.Argument
 * @since May 18, 2019
 */
public enum ResponseType {
	/**
	 * None response type.
	 */
	NONE,
	/**
	 * Console response type.
	 */
	CONSOLE,
	/**
	 * Sender response type.
	 */
	SENDER,
	/**
	 * Broadcast response type.
	 */
	BROADCAST
}
