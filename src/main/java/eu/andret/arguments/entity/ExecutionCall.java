package eu.andret.arguments.entity;

import java.lang.reflect.Method;
import java.util.List;

/**
 * The class that holds the method that are about to call and their future arguments.
 *
 * @author Andret
 * @since Sep 04, 2021
 */
public record ExecutionCall(List<Method> methods, Object[] data) {
}
