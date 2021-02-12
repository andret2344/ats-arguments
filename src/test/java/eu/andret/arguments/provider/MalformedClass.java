/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.provider;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.api.annotation.BaseCommand;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

@Value
@BaseCommand("malformed")
@EqualsAndHashCode(callSuper = true)
public class MalformedClass extends AnnotatedCommandExecutor<JavaPlugin> {
	String world;

	public MalformedClass(final CommandSender sender, final JavaPlugin plugin, final String world) {
		super(sender, plugin);
		this.world = world;
	}

	@Argument
	public String world() {
		return world;
	}
}
