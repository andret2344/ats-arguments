package eu.andret.arguments.filter.impl;

import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.filter.IPermissionFilter;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.lang.reflect.Method;

/**
 * An implementation for {@link IPermissionFilter}.
 *
 * @author Andret
 * @since Apr 17, 2020
 */
public final class PermissionFilter implements IPermissionFilter {
	@Override
	public boolean filterPermission(final Method method, final CommandSender sender) {
		if (sender instanceof ConsoleCommandSender) {
			return true;
		}
		if (sender.isOp()) {
			return true;
		}
		final Argument argument = method.getAnnotation(Argument.class);
		return argument.permission().isEmpty() || sender.hasPermission(argument.permission());
	}
}
