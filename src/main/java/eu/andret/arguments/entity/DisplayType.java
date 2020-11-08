/*
 * Copyright Andret (c) 2018-2020 Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.entity;

/**
 * Decides when argument should be shown in help.
 *
 * @author Andret
 * @see eu.andret.arguments.annotation.Argument
 * @since May 08, 2020
 */
public enum DisplayType {
	/**
	 * Argument will be shown always
	 */
	ALWAYS,
	/**
	 * Argument will be shown only if sender has permissions to it.
	 */
	IF_PERMS,
	/**
	 * Argument won't be shown
	 */
	NONE
}
