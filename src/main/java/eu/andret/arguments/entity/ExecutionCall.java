/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.entity;

import lombok.Value;

import java.lang.reflect.Method;
import java.util.List;

/**
 * The class that holds the method that are about to call and their future arguments.
 *
 * @author Andret
 * @since Sep 04, 2021
 */
@Value
public class ExecutionCall {
	List<Method> methods;
	Object[] data;
}
