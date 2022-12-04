/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.api.annotation.Completer;
import eu.andret.arguments.api.annotation.Ignore;
import eu.andret.arguments.entity.CompletingConfig;
import eu.andret.arguments.mapper.IMethodToCompletionMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

/**
 * An implementation for {@link IMethodToCompletionMapper}.
 *
 * @author Andret
 * @since Nov 07, 2020
 */
@Value
@Getter(AccessLevel.NONE)
public class MethodToCompletionMapper implements IMethodToCompletionMapper {
	CompletingConfig completingConfig;

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
			return (sender, collection) -> Collections.emptyList();
		}
		if (!parameter.isAnnotationPresent(Completer.class)) {
			return getTypeSuggestion(parameter);
		}
		final String value = parameter.getAnnotation(Completer.class).value();
		final BiFunction<CommandSender, Collection<String>, Collection<String>> argumentCompleter
				= completingConfig.getArgumentCompleter(value);
		if (argumentCompleter != null) {
			return argumentCompleter;
		}
		return getTypeSuggestion(parameter);
	}

	private BiFunction<List<String>, CommandSender, Collection<String>> getTypeSuggestion(final Parameter parameter) {
		final BiFunction<CommandSender, Collection<String>, Collection<String>> typeCompleter
				= completingConfig.getTypeCompleter(parameter.getType());
		if (typeCompleter != null) {
			return typeCompleter;
		}
		return (sender, collection) -> Collections.emptyList();
	}
}
