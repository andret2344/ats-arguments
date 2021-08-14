/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.api.entity;

import eu.andret.arguments.api.annotation.Argument;

/**
 * Describes who will get the result from executed method. Used in {@link Argument} to decide what will happen to method
 * result.
 *
 * @author Andret
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
