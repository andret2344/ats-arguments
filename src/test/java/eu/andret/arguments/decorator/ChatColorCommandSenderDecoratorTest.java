package eu.andret.arguments.decorator;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.testng.annotations.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChatColorCommandSenderDecoratorTest {
	private static final String RAW = "&atext";
	private static final String TRANSLATED = "§atext";

	@Test
	void sendMessage() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final CommandSender decorator = new ChatColorCommandSenderDecorator(sender);

		// when
		decorator.sendMessage(RAW);

		// then
		verify(sender).sendMessage(TRANSLATED);
	}

	@Test
	void sendMessages() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final CommandSender decorator = new ChatColorCommandSenderDecorator(sender);

		// when
		decorator.sendMessage(RAW, RAW);

		// then
		verify(sender).sendMessage(TRANSLATED, TRANSLATED);
	}

	@Test
	void sendMessageWithUuid() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final CommandSender decorator = new ChatColorCommandSenderDecorator(sender);
		final UUID uuid = UUID.randomUUID();

		// when
		decorator.sendMessage(uuid, RAW);

		// then
		verify(sender).sendMessage(uuid, TRANSLATED);
	}

	@Test
	void sendMessagesWithUuid() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final CommandSender decorator = new ChatColorCommandSenderDecorator(sender);
		final UUID uuid = UUID.randomUUID();

		// when
		decorator.sendMessage(uuid, RAW, RAW);

		// then
		verify(sender).sendMessage(uuid, TRANSLATED, TRANSLATED);
	}

	@Test
	void getServer() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final Server server = mock(Server.class);
		when(sender.getServer()).thenReturn(server);
		final CommandSender decorator = new ChatColorCommandSenderDecorator(sender);

		// when
		final Server result = decorator.getServer();

		// then
		assertThat(result).isSameAs(server);
	}

	@Test
	void getName() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		when(sender.getName()).thenReturn("name");
		final CommandSender decorator = new ChatColorCommandSenderDecorator(sender);

		// when
		final String result = decorator.getName();

		// then
		assertThat(result).isEqualTo("name");
	}

	@Test
	void spigot() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final CommandSender.Spigot spigot = new CommandSender.Spigot();
		when(sender.spigot()).thenReturn(spigot);
		final CommandSender decorator = new ChatColorCommandSenderDecorator(sender);

		// when
		final CommandSender.Spigot result = decorator.spigot();

		// then
		assertThat(result).isSameAs(spigot);
	}

	@Test
	void testPermissionSetByName() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		when(sender.isPermissionSet("perm")).thenReturn(true);
		final CommandSender decorator = new ChatColorCommandSenderDecorator(sender);

		// when
		final boolean result = decorator.isPermissionSet("perm");

		// then
		assertThat(result).isTrue();
	}

	@Test
	void testPermissionSetByPermission() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final Permission permission = new Permission("perm");
		when(sender.isPermissionSet(permission)).thenReturn(true);
		final CommandSender decorator = new ChatColorCommandSenderDecorator(sender);

		// when
		final boolean result = decorator.isPermissionSet(permission);

		// then
		assertThat(result).isTrue();
	}

	@Test
	void testHasPermissionByName() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		when(sender.hasPermission("perm")).thenReturn(true);
		final CommandSender decorator = new ChatColorCommandSenderDecorator(sender);

		// when
		final boolean result = decorator.hasPermission("perm");

		// then
		assertThat(result).isTrue();
	}

	@Test
	void testHasPermissionByPermission() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final Permission permission = new Permission("perm");
		when(sender.hasPermission(permission)).thenReturn(true);
		final CommandSender decorator = new ChatColorCommandSenderDecorator(sender);

		// when
		final boolean result = decorator.hasPermission(permission);

		// then
		assertThat(result).isTrue();
	}

	@Test
	void addAttachmentWithNameAndValue() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final Plugin plugin = mock(Plugin.class);
		final PermissionAttachment attachment = mock(PermissionAttachment.class);
		when(sender.addAttachment(plugin, "perm", true)).thenReturn(attachment);
		final CommandSender decorator = new ChatColorCommandSenderDecorator(sender);

		// when
		final PermissionAttachment result = decorator.addAttachment(plugin, "perm", true);

		// then
		assertThat(result).isSameAs(attachment);
	}

	@Test
	void addAttachment() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final Plugin plugin = mock(Plugin.class);
		final PermissionAttachment attachment = mock(PermissionAttachment.class);
		when(sender.addAttachment(plugin)).thenReturn(attachment);
		final CommandSender decorator = new ChatColorCommandSenderDecorator(sender);

		// when
		final PermissionAttachment result = decorator.addAttachment(plugin);

		// then
		assertThat(result).isSameAs(attachment);
	}

	@Test
	void addAttachmentWithNameValueAndTicks() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final Plugin plugin = mock(Plugin.class);
		final PermissionAttachment attachment = mock(PermissionAttachment.class);
		when(sender.addAttachment(plugin, "perm", true, 10)).thenReturn(attachment);
		final CommandSender decorator = new ChatColorCommandSenderDecorator(sender);

		// when
		final PermissionAttachment result = decorator.addAttachment(plugin, "perm", true, 10);

		// then
		assertThat(result).isSameAs(attachment);
	}

	@Test
	void addAttachmentWithTicks() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final Plugin plugin = mock(Plugin.class);
		final PermissionAttachment attachment = mock(PermissionAttachment.class);
		when(sender.addAttachment(plugin, 10)).thenReturn(attachment);
		final CommandSender decorator = new ChatColorCommandSenderDecorator(sender);

		// when
		final PermissionAttachment result = decorator.addAttachment(plugin, 10);

		// then
		assertThat(result).isSameAs(attachment);
	}

	@Test
	void removeAttachment() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final PermissionAttachment attachment = mock(PermissionAttachment.class);
		final CommandSender decorator = new ChatColorCommandSenderDecorator(sender);

		// when
		decorator.removeAttachment(attachment);

		// then
		verify(sender).removeAttachment(attachment);
	}

	@Test
	void recalculatePermissions() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final CommandSender decorator = new ChatColorCommandSenderDecorator(sender);

		// when
		decorator.recalculatePermissions();

		// then
		verify(sender).recalculatePermissions();
	}

	@Test
	void getEffectivePermissions() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final Set<PermissionAttachmentInfo> permissions = Set.of();
		when(sender.getEffectivePermissions()).thenReturn(permissions);
		final CommandSender decorator = new ChatColorCommandSenderDecorator(sender);

		// when
		final Set<PermissionAttachmentInfo> result = decorator.getEffectivePermissions();

		// then
		assertThat(result).isSameAs(permissions);
	}

	@Test
	void testOp() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		when(sender.isOp()).thenReturn(true);
		final CommandSender decorator = new ChatColorCommandSenderDecorator(sender);

		// when
		final boolean result = decorator.isOp();

		// then
		assertThat(result).isTrue();
	}

	@Test
	void setOp() {
		// given
		final CommandSender sender = mock(CommandSender.class);
		final CommandSender decorator = new ChatColorCommandSenderDecorator(sender);

		// when
		decorator.setOp(true);

		// then
		verify(sender).setOp(true);
	}
}
