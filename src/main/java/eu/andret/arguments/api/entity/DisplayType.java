package eu.andret.arguments.api.entity;

import eu.andret.arguments.api.annotation.Argument;

/**
 * Decides when {@link Argument} should be shown in help.
 *
 * @author Andret
 * @since May 08, 2020
 */
public enum DisplayType {
	/**
	 * Argument will be shown always.
	 */
	ALWAYS,
	/**
	 * Argument will be shown only if sender has permissions to it.
	 */
	IF_PERMS,
	/**
	 * Argument will not be shown.
	 */
	NONE
}
