/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.AnnotatedCommand;
import eu.andret.arguments.entity.ResponseType;
import org.bukkit.command.CommandSender;

/**
 * A mapper interface to mapping response generated from method.
 *
 * @author Andret
 * @since Apr 19, 2020
 */
public interface IResponseMapper extends IMapper {
	/**
	 * The mapper function that maps response to exact behavior
	 *
	 * @param sender The {@link org.bukkit.command.CommandSender} who performed the command.
	 * @param result The result returned from called method.
	 * @param responseType The {@link eu.andret.arguments.entity.ResponseType}.
	 * @param options The {@link eu.andret.arguments.AnnotatedCommand.Options}.
	 */
	void mapResponse(CommandSender sender, Object result, ResponseType responseType, AnnotatedCommand.Options options);
}
