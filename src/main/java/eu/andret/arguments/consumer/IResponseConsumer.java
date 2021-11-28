/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.consumer;

import eu.andret.arguments.AnnotatedCommand;
import eu.andret.arguments.mapper.IMapper;
import org.bukkit.command.CommandSender;

/**
 * A consumer that accepts the response and presents it to sender.
 *
 * @author Andret
 * @since Apr 19, 2020
 */
public interface IResponseConsumer extends IMapper {
	/**
	 * The mapper function that maps response to exact behavior.
	 *
	 * @param sender The {@link CommandSender} who performed the command.
	 * @param result The result returned from called method.
	 * @param options The {@link AnnotatedCommand.Options}.
	 */
	void consumeResponse(CommandSender sender, Object result, AnnotatedCommand.Options options);
}
