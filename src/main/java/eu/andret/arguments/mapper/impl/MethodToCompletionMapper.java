/*
 * Copyright Andret (c) 2018-2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.annotation.Completer;
import eu.andret.arguments.annotation.Ignore;
import eu.andret.arguments.mapper.IMethodToCompletionMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

/**
 * An implementation for {@link IMethodToCompletionMapper}.
 *
 * @author Andret
 * @since Nov 07, 2020
 */
@Value
@Getter(AccessLevel.NONE)
public class MethodToCompletionMapper implements IMethodToCompletionMapper {
	Map<Class<?>, Function<CommandSender, Collection<String>>> typeCompleterMap;
	Map<String, Function<CommandSender, Collection<String>>> argumentCompleterMap;

	@Override
	public Collection<String> mapCommandToCompletion(Method m, String[] args, CommandSender sender) {
		if (args.length <= 1 || m.getParameterCount() == 0) {
			return Collections.emptyList();
		}
		if (args.length - 1 <= m.getParameterCount()) {
			return extractSuggestions(m.getParameters()[args.length - 2]).apply(sender);
		}
		Parameter parameter = m.getParameters()[m.getParameterCount() - 1];
		if (!parameter.isVarArgs()) {
			return Collections.emptyList();
		}
		return extractSuggestions(parameter).apply(sender);
	}

	@NotNull
	@NonNull
	private Function<CommandSender, Collection<String>> extractSuggestions(Parameter parameter) {
		if (parameter.isAnnotationPresent(Ignore.class)) {
			return sender -> Collections.emptyList();
		}
		if (!parameter.isAnnotationPresent(Completer.class)) {
			return getTypeSuggestion(parameter);
		}
		String value = parameter.getAnnotation(Completer.class).value();
		if (!argumentCompleterMap.containsKey(value)) {
			return getTypeSuggestion(parameter);
		}
		return argumentCompleterMap.get(value);
	}

	private Function<CommandSender, Collection<String>> getTypeSuggestion(Parameter parameter) {
		if (typeCompleterMap.containsKey(parameter.getType())) {
			return typeCompleterMap.get(parameter.getType());
		}
		return sender -> Collections.emptyList();
	}
}
