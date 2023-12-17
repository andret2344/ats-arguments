/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.api.entity;

import eu.andret.arguments.api.annotation.Argument;

/**
 * Describes who can execute the command. It's used in {@link Argument} to define who can execute command bound to
 * method.
 *
 * @author Andret
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
