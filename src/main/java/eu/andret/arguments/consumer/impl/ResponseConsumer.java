/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.consumer.impl;

import eu.andret.arguments.AnnotatedCommand;
import eu.andret.arguments.api.entity.ResponseType;
import eu.andret.arguments.consumer.IResponseConsumer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Implementation for {@link IResponseConsumer}.
 */
public class ResponseConsumer implements IResponseConsumer {
	@Override
	public void consumeResponse(final CommandSender sender, final Object result, final ResponseType responseType, final AnnotatedCommand.Options options) {
		Optional.ofNullable(result)
				.map(this::createResponseStream)
				.orElse(Stream.empty())
				.map(message -> mapToColored(message, options))
				.forEach(message -> consumeMessage(message, sender, responseType));
	}

	private Stream<String> createResponseStream(final Object result) {
		if (result.getClass().isArray()) {
			return Optional.of(result)
					.map(Object[].class::cast)
					.stream()
					.flatMap(Arrays::stream)
					.map(String::valueOf);
		}
		if (Collection.class.isAssignableFrom(result.getClass())) {
			return Optional.of(result)
					.map(x -> (Collection<?>) x)
					.stream()
					.flatMap(Collection::stream)
					.map(String::valueOf);
		}
		return Stream.of(result).map(String::valueOf);
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
