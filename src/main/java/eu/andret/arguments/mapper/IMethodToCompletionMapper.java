package eu.andret.arguments.mapper;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * An interface to map {@link java.lang.reflect.Method} to its completion suggestions.
 *
 * @author Andret
 * @since Nov 07, 2020
 */
public interface IMethodToCompletionMapper extends IMapper {
	@NotNull
	@NonNull
	Collection<String> mapCommandToCompletion(Method m, String[] args);
}
