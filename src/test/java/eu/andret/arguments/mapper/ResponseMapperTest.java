/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.AnnotatedCommand;
import eu.andret.arguments.entity.ResponseType;
import eu.andret.arguments.mapper.impl.ResponseMapper;
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

class ResponseMapperTest {
	@Test
	void nullResult() {
		// given
		CommandSender sender = mock(CommandSender.class);
		IResponseMapper mapper = new ResponseMapper();

		// when
		mapper.mapResponse(sender, null, ResponseType.SENDER, new AnnotatedCommand.Options());

		// then
		verify(sender, times(0)).sendMessage(anyString());
	}

	@Test
	void methodWithNoneResponse() {
		// given
		CommandSender sender = mock(CommandSender.class);
		IResponseMapper mapper = new ResponseMapper();

		// when
		mapper.mapResponse(sender, "test response", ResponseType.NONE, new AnnotatedCommand.Options());

		// then
		verify(sender, times(0)).sendMessage(anyString());
	}

	@Test
	void methodWithSenderResponse() {
		// given
		CommandSender sender = mock(CommandSender.class);
		IResponseMapper mapper = new ResponseMapper();

		// when
		mapper.mapResponse(sender, "test response", ResponseType.SENDER, new AnnotatedCommand.Options());

		// then
		verify(sender, times(1)).sendMessage(eq("test response"));
	}

	@Test
	void methodWithConsoleResponse() {
		// given
		CommandSender sender = mock(CommandSender.class);
		Server server = mock(Server.class);
		IResponseMapper mapper = new ResponseMapper();
		when(sender.getServer()).thenReturn(server);
		Logger logger = mock(Logger.class);
		when(server.getLogger()).thenReturn(logger);

		// when
		mapper.mapResponse(sender, "test response", ResponseType.CONSOLE, new AnnotatedCommand.Options());

		// then
		verify(logger, times(1)).info(eq("test response"));
	}

	@Test
	void methodWithBroadcastResponse() {
		// given
		CommandSender sender = mock(CommandSender.class);
		Server server = mock(Server.class);
		IResponseMapper mapper = new ResponseMapper();
		when(sender.getServer()).thenReturn(server);

		// when
		mapper.mapResponse(sender, "test response", ResponseType.BROADCAST, new AnnotatedCommand.Options());

		// then
		verify(server, times(1)).broadcastMessage(eq("test response"));
	}

	@Test
	void methodWithColoredResponse() {
		// given
		CommandSender sender = mock(CommandSender.class);
		IResponseMapper mapper = new ResponseMapper();
		AnnotatedCommand.Options options = new AnnotatedCommand.Options();
		options.setAutoTranslateColors(true);

		// when
		mapper.mapResponse(sender, "&7test&a response", ResponseType.SENDER, options);

		// then
		verify(sender, times(1)).sendMessage(eq(ChatColor.translateAlternateColorCodes('&', "&7test&a response")));
	}

	@Test
	void methodWithoutColoredResponse() {
		// given
		CommandSender sender = mock(CommandSender.class);
		IResponseMapper mapper = new ResponseMapper();

		// when
		mapper.mapResponse(sender, "&7test&a response", ResponseType.SENDER, new AnnotatedCommand.Options());

		// then
		verify(sender, times(1)).sendMessage(eq("&7test&a response"));
	}
}
