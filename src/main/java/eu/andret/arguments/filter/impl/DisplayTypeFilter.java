package eu.andret.arguments.filter.impl;

import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.api.entity.DisplayType;
import eu.andret.arguments.filter.IDisplayTypeFilter;
import eu.andret.arguments.filter.IPermissionFilter;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;

/**
 * An implementation for {@link IDisplayTypeFilter}.
 *
 * @author Andret
 * @since May 08, 2020
 */
public class DisplayTypeFilter implements IDisplayTypeFilter {
	private final IPermissionFilter permissionMapper;

	/**
	 * A constructor.
	 *
	 * @param permissionMapper The permission filter.
	 */
	public DisplayTypeFilter(final IPermissionFilter permissionMapper) {
		this.permissionMapper = permissionMapper;
	}

	@Override
	public boolean mapDisplayType(final Method method, final CommandSender sender) {
		final DisplayType displayType = method.getAnnotation(Argument.class).displayType();
		return displayType == DisplayType.ALWAYS || permissionMapper.filterPermission(method, sender)
				&& displayType == DisplayType.IF_PERMS;
	}
}
