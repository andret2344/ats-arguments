/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.mapper.IMethodToDescriptionMapper;
import lombok.Value;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * An implementation for {@link IMethodToDescriptionMapper}.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
@Value
public class MethodToDescriptionMapper implements IMethodToDescriptionMapper {
	@Override
	public String mapMethodToDescription(final Method method, final String command) {
		String message = "/" + command + getCommandPattern(method);
		final String description = method.getAnnotation(Argument.class).description();
		if (!description.isEmpty()) {
			message += " - " + description;
		}
		return message.replace('&', '\u00A7');
	}

	private String getCommandPattern(final Method method) {
		final Parameter[] params = method.getParameters();
		final Argument a = method.getAnnotation(Argument.class);
		final StringBuilder message = new StringBuilder();
		final String argumentWithAliases = getArgumentWithAliases(method);
		if (params.length == 0) {
			return message.append(" ").append(argumentWithAliases).toString();
		}
		for (int i = 0; i < params.length; i++) {
			if (i == a.position()) {
				message.append(" ").append(argumentWithAliases);
			}
			message.append(" <").append(params[i].getName());
			if (params[i].getType().isArray()) {
				message.append("...");
			}
			message.append(">");
		}
		if (params.length == a.position()) {
			message.append(" ").append(method.getName());
		}
		return message.toString();
	}

	private String getArgumentWithAliases(final Method method) {
		final Argument a = method.getAnnotation(Argument.class);
		if (a.aliases().length == 0) {
			return method.getName();
		}
		return "<" + method.getName() + Arrays.stream(a.aliases())
				.map(alias -> "|" + alias)
				.collect(Collectors.joining("")) + ">";
	}
}
