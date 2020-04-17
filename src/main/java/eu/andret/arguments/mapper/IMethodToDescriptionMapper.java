package eu.andret.arguments.mapper;

import java.lang.reflect.Method;

public interface IMethodToDescriptionMapper {
	String mapMethodToDescription(Method method, String command);
}
