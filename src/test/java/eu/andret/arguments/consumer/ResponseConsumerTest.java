/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.consumer;

import eu.andret.arguments.AnnotatedCommand;
import eu.andret.arguments.consumer.impl.ResponseConsumer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ResponseConsumerTest {
	@Test
	void nullResult() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final IResponseConsumer mapper = new ResponseConsumer();

		// when
		mapper.consumeResponse(sender, null, new AnnotatedCommand.Options());

		// then
		verify(sender, times(0)).sendMessage(anyString());
	}

	@Test
	void methodWithColoredResponse() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final IResponseConsumer mapper = new ResponseConsumer();
		final AnnotatedCommand.Options options = new AnnotatedCommand.Options();
		options.setAutoTranslateColors(true);

		// when
		mapper.consumeResponse(sender, "&7test&a response", options);

		// then
		verify(sender, times(1))
				.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7test&a response"));
	}

	@Test
	void methodWithoutColoredResponse() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final IResponseConsumer mapper = new ResponseConsumer();

		// when
		mapper.consumeResponse(sender, "&7test&a response", new AnnotatedCommand.Options());

		// then
		verify(sender, times(1)).sendMessage("&7test&a response");
	}
}
