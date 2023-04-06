/*
 * Copyright Andret (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.decorator;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.processing.Generated;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

@Generated("IntelliJ IDEA")
public class ChatColorCommandSenderDecorator implements CommandSender {
	private final CommandSender wrappedSender;

	public ChatColorCommandSenderDecorator(@NotNull final CommandSender wrappedSender) {
		this.wrappedSender = wrappedSender;
	}

	@Override
	public void sendMessage(@NotNull final String message) {
		wrappedSender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}

	@Override
	public void sendMessage(@NotNull final String... messages) {
		final String[] array = Arrays.stream(messages)
				.map(message -> ChatColor.translateAlternateColorCodes('&', message))
				.toArray(String[]::new);
		wrappedSender.sendMessage(array);
	}

	@Override
	public void sendMessage(@Nullable final UUID sender, @NotNull final String message) {
		wrappedSender.sendMessage(sender, ChatColor.translateAlternateColorCodes('&', message));
	}

	@Override
	public void sendMessage(@Nullable final UUID sender, @NotNull final String... messages) {
		final String[] array = Arrays.stream(messages)
				.map(message -> ChatColor.translateAlternateColorCodes('&', message))
				.toArray(String[]::new);
		wrappedSender.sendMessage(sender, array);
	}

	@Override
	@NotNull
	public Server getServer() {
		return wrappedSender.getServer();
	}

	@Override
	@NotNull
	public String getName() {
		return wrappedSender.getName();
	}

	@Override
	@NotNull
	public Spigot spigot() {
		return wrappedSender.spigot();
	}

	@Override
	public boolean isPermissionSet(@NotNull final String name) {
		return wrappedSender.isPermissionSet(name);
	}

	@Override
	public boolean isPermissionSet(@NotNull final Permission perm) {
		return wrappedSender.isPermissionSet(perm);
	}

	@Override
	public boolean hasPermission(@NotNull final String name) {
		return wrappedSender.hasPermission(name);
	}

	@Override
	public boolean hasPermission(@NotNull final Permission perm) {
		return wrappedSender.hasPermission(perm);
	}

	@Override
	@NotNull
	public PermissionAttachment addAttachment(@NotNull final Plugin plugin, @NotNull final String name, final boolean value) {
		return wrappedSender.addAttachment(plugin, name, value);
	}

	@Override
	@NotNull
	public PermissionAttachment addAttachment(@NotNull final Plugin plugin) {
		return wrappedSender.addAttachment(plugin);
	}

	@Override
	@Nullable
	public PermissionAttachment addAttachment(@NotNull final Plugin plugin, @NotNull final String name, final boolean value, final int ticks) {
		return wrappedSender.addAttachment(plugin, name, value, ticks);
	}

	@Override
	@Nullable
	public PermissionAttachment addAttachment(@NotNull final Plugin plugin, final int ticks) {
		return wrappedSender.addAttachment(plugin, ticks);
	}

	@Override
	public void removeAttachment(@NotNull final PermissionAttachment attachment) {
		wrappedSender.removeAttachment(attachment);
	}

	@Override
	public void recalculatePermissions() {
		wrappedSender.recalculatePermissions();
	}

	@Override
	@NotNull
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return wrappedSender.getEffectivePermissions();
	}

	@Override
	public boolean isOp() {
		return wrappedSender.isOp();
	}

	@Override
	public void setOp(final boolean value) {
		wrappedSender.setOp(value);
	}
}
