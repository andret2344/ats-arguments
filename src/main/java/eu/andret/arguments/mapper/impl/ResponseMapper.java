/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.annotation.Argument;
import eu.andret.arguments.mapper.IResponseMapper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;

public class ResponseMapper implements IResponseMapper {
	@Override
	public void mapResponse(CommandSender sender, Object result, Method method) {
		if (result == null) {
			return;
		}
		String message = String.valueOf(result);
		switch (method.getAnnotation(Argument.class).responseType()) {
			case SENDER:
				sender.sendMessage(message);
				break;
			case CONSOLE:
				Bukkit.getLogger().info(message);
				break;
			case BROADCAST:
				Bukkit.broadcastMessage(message);
				break;
			case NONE:
			default:
				break;
		}
	}
}
