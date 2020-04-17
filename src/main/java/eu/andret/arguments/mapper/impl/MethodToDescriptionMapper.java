package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.annotation.Argument;
import eu.andret.arguments.mapper.IMethodToDescriptionMapper;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.stream.Collectors;

public final class MethodToDescriptionMapper implements IMethodToDescriptionMapper {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String mapMethodToDescription(Method method, String command) {
		String message = "/" + command + getCommandPattern(method);
		String description = method.getAnnotation(Argument.class).description();
		if (!description.isEmpty()) {
			message += " - " + description;
		}
		return message.replace('&', '\u00A7');
	}

	private String getCommandPattern(Method method) {
		Parameter[] params = method.getParameters();
		Argument a = method.getAnnotation(Argument.class);
		StringBuilder message = new StringBuilder();
		String argumentWithAliases = getArgumentWithAliases(method);
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
			message.append((">"));
		}
		if (params.length == a.position()) {
			message.append(" ").append(method.getName());
		}
		return message.toString();
	}

	private String getArgumentWithAliases(Method method) {
		Argument a = method.getAnnotation(Argument.class);
		if (a.aliases().length == 0) {
			return method.getName();
		}
		return "<" + method.getName() + Arrays.stream(a.aliases())
				.map(alias -> "|" + alias)
				.collect(Collectors.joining("")) + ">";
	}
}
