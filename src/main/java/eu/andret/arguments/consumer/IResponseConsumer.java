/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.consumer;

import eu.andret.arguments.AnnotatedCommand;
import eu.andret.arguments.mapper.IMapper;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A mapper interface to mapping response generated from method.
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
	void consumeResponse(@NotNull CommandSender sender, @Nullable String result,
						 @NotNull AnnotatedCommand.Options options);
}
