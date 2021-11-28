/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.api.entity;

import lombok.experimental.UtilityClass;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * The class that provides basic utilities for Fallbacks.
 *
 * @author Andret
 * @since Sep 04, 2021
 */
@UtilityClass
public final class FallbackConstants {
	public static final Predicate<Object> NEVER = x -> false;
	public static final Predicate<Object> ON_NULL = Objects::isNull;
	public static final Predicate<Object> ALWAYS = x -> true;
}
