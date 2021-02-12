/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.consumer;

import eu.andret.arguments.AnnotatedCommand;
import eu.andret.arguments.api.entity.ResponseType;
import eu.andret.arguments.consumer.impl.ResponseConsumer;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ResponseConsumerTest {
	@Test
	void nullResult() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final IResponseConsumer mapper = new ResponseConsumer();

		// when
		mapper.consumeResponse(sender, null, ResponseType.SENDER, new AnnotatedCommand.Options());

		// then
		verify(sender, times(0)).sendMessage(anyString());
	}

	@Test
	void methodWithNoneResponse() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final IResponseConsumer mapper = new ResponseConsumer();

		// when
		mapper.consumeResponse(sender, "test response", ResponseType.NONE, new AnnotatedCommand.Options());

		// then
		verify(sender, times(0)).sendMessage(anyString());
	}

	@Test
	void methodWithSenderResponse() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final IResponseConsumer mapper = new ResponseConsumer();

		// when
		mapper.consumeResponse(sender, "test response", ResponseType.SENDER, new AnnotatedCommand.Options());

		// then
		verify(sender, times(1)).sendMessage(eq("test response"));
	}

	@Test
	void methodWithConsoleResponse() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final Server server = mock(Server.class);
		final IResponseConsumer mapper = new ResponseConsumer();
		when(sender.getServer()).thenReturn(server);
		final Logger logger = mock(Logger.class);
		when(server.getLogger()).thenReturn(logger);

		// when
		mapper.consumeResponse(sender, "test response", ResponseType.CONSOLE, new AnnotatedCommand.Options());

		// then
		verify(logger, times(1)).info(eq("test response"));
	}

	@Test
	void methodWithBroadcastResponse() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final Server server = mock(Server.class);
		final IResponseConsumer mapper = new ResponseConsumer();
		when(sender.getServer()).thenReturn(server);

		// when
		mapper.consumeResponse(sender, "test response", ResponseType.BROADCAST, new AnnotatedCommand.Options());

		// then
		verify(server, times(1)).broadcastMessage(eq("test response"));
	}

	@Test
	void methodWithColoredResponse() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final IResponseConsumer mapper = new ResponseConsumer();
		final AnnotatedCommand.Options options = new AnnotatedCommand.Options();
		options.setAutoTranslateColors(true);

		// when
		mapper.consumeResponse(sender, "&7test&a response", ResponseType.SENDER, options);

		// then
		verify(sender, times(1)).sendMessage(eq(ChatColor.translateAlternateColorCodes('&', "&7test&a response")));
	}

	@Test
	void methodWithoutColoredResponse() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final IResponseConsumer mapper = new ResponseConsumer();

		// when
		mapper.consumeResponse(sender, "&7test&a response", ResponseType.SENDER, new AnnotatedCommand.Options());

		// then
		verify(sender, times(1)).sendMessage(eq("&7test&a response"));
	}
}
