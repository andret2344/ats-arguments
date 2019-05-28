/*
 * Copyright Andret (c) 2019. Copying and modifying allowed only keeping git link reference.
 */
package eu.andret.arguments;

/**
 * Describes who can execute the command. It's used in {@link
 * eu.andret.arguments.Argument} to define who can execute command bound to
 * method.
 *
 * @author Andret
 * @see eu.andret.arguments.Argument
 * @since May 18, 2019
 */
public enum ExecutorType {
	/**
	 * Executor can be either player or console.
	 */
	ALL,
	/**
	 * Executor can be only player, console has no permissions.
	 */
	PLAYER,
	/**
	 * Executor can be only console, player has no permissions.
	 */
	CONSOLE
}
