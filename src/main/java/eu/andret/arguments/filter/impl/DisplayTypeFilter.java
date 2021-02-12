/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.filter.impl;

import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.api.entity.DisplayType;
import eu.andret.arguments.filter.IDisplayTypeFilter;
import eu.andret.arguments.filter.IPermissionFilter;
import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;

/**
 * An implementation for {@link IDisplayTypeFilter}.
 *
 * @author Andret
 * @since May 08, 2020
 */
@AllArgsConstructor
public class DisplayTypeFilter implements IDisplayTypeFilter {
	private final IPermissionFilter permissionMapper;

	@Override
	public boolean mapDisplayType(final Method method, final CommandSender sender) {
		final DisplayType displayType = method.getAnnotation(Argument.class).displayType();
		if (displayType == DisplayType.ALWAYS) {
			return true;
		}
		return permissionMapper.filterPermission(method, sender) && displayType == DisplayType.IF_PERMS;
	}
}
