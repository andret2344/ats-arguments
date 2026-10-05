package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.api.annotation.Completer;
import eu.andret.arguments.api.annotation.Ignore;
import eu.andret.arguments.mapper.IMethodToCompletionMapper;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * An implementation for {@link IMethodToCompletionMapper}.
 *
 * @author Andret
 * @since Nov 07, 2020
 */
public final class MethodToCompletionMapper implements IMethodToCompletionMapper {
	private final Map<Class<?>, BiFunction<List<String>, CommandSender, Collection<String>>> typeCompleterMap;
	private final Map<String, BiFunction<List<String>, CommandSender, Collection<String>>> argumentCompleterMap;

	/**
	 * A constructor.
	 *
	 * @param typeCompleterMap The completers by type.
	 * @param argumentCompleterMap The completers by id.
	 */
	public MethodToCompletionMapper(
			final Map<Class<?>, BiFunction<List<String>, CommandSender, Collection<String>>> typeCompleterMap,
			final Map<String, BiFunction<List<String>, CommandSender, Collection<String>>> argumentCompleterMap) {
		this.typeCompleterMap = typeCompleterMap;
		this.argumentCompleterMap = argumentCompleterMap;
	}

	@Override
	public Collection<String> mapCommandToCompletion(final Method method, final String[] args, final CommandSender sender) {
		if (args.length <= 1 || method.getParameterCount() == 0) {
			return Collections.emptyList();
		}
		if (args.length - 1 <= method.getParameterCount()) {
			return extractSuggestions(method.getParameters()[args.length - 2]).apply(Arrays.asList(args), sender);
		}
		final Parameter parameter = method.getParameters()[method.getParameterCount() - 1];
		if (!parameter.isVarArgs()) {
			return Collections.emptyList();
		}
		return extractSuggestions(parameter).apply(Arrays.asList(args), sender);
	}

	@NotNull
	private BiFunction<List<String>, CommandSender, Collection<String>> extractSuggestions(final Parameter parameter) {
		if (parameter.isAnnotationPresent(Ignore.class)) {
			return (_, _) -> Collections.emptyList();
		}
		if (!parameter.isAnnotationPresent(Completer.class)) {
			return getTypeSuggestion(parameter);
		}
		final String value = parameter.getAnnotation(Completer.class).value();
		if (!argumentCompleterMap.containsKey(value)) {
			return getTypeSuggestion(parameter);
		}
		return argumentCompleterMap.get(value);
	}

	private BiFunction<List<String>, CommandSender, Collection<String>> getTypeSuggestion(final Parameter parameter) {
		if (typeCompleterMap.containsKey(parameter.getType())) {
			return typeCompleterMap.get(parameter.getType());
		}
		return (_, _) -> Collections.emptyList();
	}
}
