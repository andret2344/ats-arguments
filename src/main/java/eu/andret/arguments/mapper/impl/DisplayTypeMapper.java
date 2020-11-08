/*
 * Copyright Andret (c) 2018-2020 Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.annotation.Argument;
import eu.andret.arguments.entity.DisplayType;
import eu.andret.arguments.mapper.IDisplayTypeMapper;
import eu.andret.arguments.mapper.IPermissionMapper;
import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;

/**
 * An implementation for {@link eu.andret.arguments.mapper.IDisplayTypeMapper}.
 *
 * @author Andret
 * @since May 08, 2020
 */
@AllArgsConstructor
public class DisplayTypeMapper implements IDisplayTypeMapper {
	private final IPermissionMapper permissionMapper;

	@Override
	public boolean mapDisplayType(Method method, CommandSender sender) {
		DisplayType displayType = method.getAnnotation(Argument.class).displayType();
		if (displayType == DisplayType.ALWAYS) {
			return true;
		}
		return permissionMapper.mapPermission(method, sender) && displayType == DisplayType.IF_PERMS;
	}
}
