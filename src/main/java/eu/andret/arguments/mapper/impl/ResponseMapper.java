/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.entity.ResponseType;
import eu.andret.arguments.mapper.IResponseMapper;
import org.bukkit.command.CommandSender;

public class ResponseMapper implements IResponseMapper {
	@Override
	public void mapResponse(CommandSender sender, Object result, ResponseType responseType) {
		if (result == null) {
			return;
		}
		String message = String.valueOf(result);
		switch (responseType) {
			case SENDER:
				sender.sendMessage(message);
				break;
			case CONSOLE:
				sender.getServer().getLogger().info(message);
				break;
			case BROADCAST:
				sender.getServer().broadcastMessage(message);
				break;
			case NONE:
			default:
				break;
		}
	}
}
