package eu.andret.arguments.mapper;

import java.lang.reflect.Method;

public interface IMethodNameMapper {
	boolean mapMethodName(Method method, String[] command);
}
