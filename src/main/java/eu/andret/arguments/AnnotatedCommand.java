/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.entity.CompletingConfig;
import eu.andret.arguments.entity.MappingConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Wrapper class for classical {@link PluginCommand}.
 *
 * @author Andret
 * @since Jun 02, 2019
 */
@Value
@NonFinal
@AllArgsConstructor
public class AnnotatedCommand<E extends JavaPlugin> {
	@NotNull
	PluginCommand command;
	@NotNull
	Options options;
	@NotNull
	MappingConfig mappingConfig;
	@NotNull
	CompletingConfig completingConfig;

	/**
	 * The Options to manipulate the behavior.
	 */
	@Data
	public static class Options {
		private boolean autoTranslateColors;
		private boolean caseSensitive;
	}

	@NotNull
	@SuppressWarnings("unchecked")
	private LocalCommandExecutor<E> getLocalCommandExecutor() {
		return (LocalCommandExecutor<E>) command.getExecutor();
	}

	@SuppressWarnings("unchecked")
	private LocalTabCompleter<E> getLocalTabCompleter() {
		return (LocalTabCompleter<E>) command.getTabCompleter();
	}

	/**
	 * @param sender The sender that is assigned to the desired CommandExecutor.
	 * @return The command executor assigned to provided sender. Can return null, if provided sender never executed
	 * command.
	 */
	@Nullable
	public AnnotatedCommandExecutor<E> getCommandExecutor(@NotNull final CommandSender sender) {
		return getLocalCommandExecutor().getCommandExecutor(sender);
	}

	/**
	 * Sets an unknown sub command execution listener.
	 *
	 * @param listener The {@link Consumer}.
	 */
	public void setOnUnknownSubCommandExecutionListener(final Consumer<CommandSender> listener) {
		getLocalCommandExecutor().setOnUnknownSubCommandExecutionListener(listener);
	}

	/**
	 * Sets an insufficient permissions' listener.
	 *
	 * @param listener The {@link Consumer}.
	 */
	public void setOnInsufficientPermissionsListener(final Consumer<CommandSender> listener) {
		getLocalCommandExecutor().setOnInsufficientPermissionsListener(listener);
	}

	/**
	 * Sets the main command execution listener.
	 *
	 * @param listener The {@link Consumer}.
	 */
	public void setOnMainCommandExecutionListener(final Consumer<CommandSender> listener) {
		getLocalCommandExecutor().setOnMainCommandExecutionListener(listener);
	}
}
