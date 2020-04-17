package eu.andret.arguments.mapper;

import eu.andret.arguments.Mapper;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * An interface for mapping arguments
 *
 * @author Andret
 * @since Apr 17, 2020
 */
public interface IArgumentMapper {
	/**
	 * @param method The {@link java.lang.reflect.Method} that will be analyzed
	 * @param command The array of {@link java.lang.String} with arguments passed with command
	 * @param mappers The {@link java.util.Map} that associates String to {@link
	 * eu.andret.arguments.Mapper}
	 *
	 * @return <code>true</code> if <code>method</code> matches command arguments and mappers input
	 * arguments, <code>false</code> otherwise
	 */
	boolean mapArguments(Method method, String[] command, Map<String, Mapper<?>> mappers);
}
