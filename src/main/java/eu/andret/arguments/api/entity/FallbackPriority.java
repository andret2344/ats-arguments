/*
 * Copyright Andret (c) 2018-2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.api.entity;

import eu.andret.arguments.api.annotation.TypeFallback;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents {@link TypeFallback} method's priority in execution.
 */
@Getter
@AllArgsConstructor
public enum FallbackPriority {
	/**
	 * method call is of very low importance and will be called lastly.
	 */
	LOWEST(0),

	/**
	 * Event call is of low importance.
	 */

	LOW(1),

	/**
	 * Event call is neither important nor unimportant, and may be ran normally. Default priority.
	 */
	NORMAL(2),

	/**
	 * Event call is of high importance.
	 */
	HIGH(3),

	/**
	 * Method call is critical and must have first possibility to execute.
	 */
	HIGHEST(4);

	private final int slot;
}
