package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.annotation.Completer;
import eu.andret.arguments.mapper.IMethodToCompletionMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

/**
 * An implementation for {@link eu.andret.arguments.mapper.IMethodToCompletionMapper}.
 *
 * @author Andret
 * @since Nov 07, 2020
 */
@Value
@Getter(AccessLevel.NONE)
public class MethodToCompletionMapper implements IMethodToCompletionMapper {
	Map<String, Supplier<Collection<String>>> completerMap;

	@NotNull
	@NonNull
	@Override
	public Collection<String> mapCommandToCompletion(Method m, String[] args) {
		if (args.length <= 1 || m.getParameterCount() == 0) {
			return Collections.emptyList();
		}
		if (args.length - 1 <= m.getParameterCount()) {
			return extractSuggestions(m.getParameters()[args.length - 2]);
		}
		Parameter parameter = m.getParameters()[m.getParameterCount() - 1];
		if (!parameter.isVarArgs()) {
			return Collections.emptyList();
		}
		return extractSuggestions(parameter);
	}

	@NotNull
	@NonNull
	private Collection<String> extractSuggestions(Parameter parameter) {
		if (!parameter.isAnnotationPresent(Completer.class)) {
			return Collections.emptyList();
		}
		Completer completer = parameter.getAnnotation(Completer.class);
		String value = completer.value();
		if (!completerMap.containsKey(value)) {
			return Collections.emptyList();
		}
		return completerMap.get(value).get();
	}
}
