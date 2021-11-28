/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.consumer.impl;

import eu.andret.arguments.AnnotatedCommand;
import eu.andret.arguments.consumer.IResponseConsumer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Implementation for {@link IResponseConsumer}.
 *
 * @author Andret
 * @since Apr 19, 2020
 */
public class ResponseConsumer implements IResponseConsumer {
	@Override
	public void consumeResponse(@NotNull final CommandSender sender, @Nullable final String result,
								@NotNull final AnnotatedCommand.Options options) {
		Optional.ofNullable(result)
				.map(message -> mapToColored(message, options))
				.ifPresent(sender::sendMessage);
	}

	@NotNull
	private String mapToColored(@NotNull final String message, @NotNull final AnnotatedCommand.Options options) {
		if (!options.isAutoTranslateColors()) {
			return message;
		}
		return ChatColor.translateAlternateColorCodes('&', message);
	}
}
