/*
 * Copyright Andret (c) 2018-2020 Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.AnnotatedCommand;
import eu.andret.arguments.entity.ResponseType;
import eu.andret.arguments.mapper.IResponseMapper;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Optional;

public class ResponseMapper implements IResponseMapper {
	@Override
	public void mapResponse(CommandSender sender, Object result, ResponseType responseType, AnnotatedCommand.Options options) {
		Optional.ofNullable(result)
				.map(String::valueOf)
				.map(string -> {
					if (!options.isAutoTranslateColors()) {
						return string;
					}
					return ChatColor.translateAlternateColorCodes('&', string);
				})
				.ifPresent(message -> {
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
				});
	}
}
