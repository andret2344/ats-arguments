/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.consumer.impl;

import eu.andret.arguments.AnnotatedCommand;
import eu.andret.arguments.api.entity.ResponseType;
import eu.andret.arguments.consumer.IResponseConsumer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Optional;

/**
 * Implementation for {@link IResponseConsumer}.
 */
public class ResponseConsumer implements IResponseConsumer {
	@Override
	public void consumeResponse(final CommandSender sender, final Object result, final ResponseType responseType, final AnnotatedCommand.Options options) {
		Optional.ofNullable(result)
				.map(String::valueOf)
				.map(message -> mapToColored(message, options))
				.ifPresent(message -> consumeMessage(message, sender, responseType));
	}

	private String mapToColored(final String message, final AnnotatedCommand.Options options) {
		if (!options.isAutoTranslateColors()) {
			return message;
		}
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	private void consumeMessage(final String message, final CommandSender sender, final ResponseType responseType) {
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
