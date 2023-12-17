/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.mapper.IMethodToDescriptionMapper;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

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
		final String description = method.getAnnotation(Argument.class).description();
		final StringBuilder stringBuilder = new StringBuilder("/").append(command).append(getCommandPattern(method));
		if (description.isBlank()) {
			return stringBuilder.toString();
		}
		return stringBuilder.append(" - ").append(description).toString();
	}

	@NotNull
	private String getCommandPattern(@NotNull final Method method) {
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

	@NotNull
	private String getArgumentWithAliases(@NotNull final Method method) {
		final Argument argument = method.getAnnotation(Argument.class);
		if (argument.aliases().length == 0) {
			return method.getName();
		}
		final String aliases = String.join("|", argument.aliases());
		return String.format("<%s|%s>", method.getName(), aliases);
	}
}
