/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.api.annotation.BaseCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

@BaseCommand("IntegrationTest")
public class IntegrationTestCommandExecutor extends AnnotatedCommandExecutor<JavaPlugin> {
	public IntegrationTestCommandExecutor(final CommandSender sender, final JavaPlugin plugin) {
		super(sender, plugin);
	}

	@Argument
	public String testWithoutParameters() {
		return "none";
	}

	@Argument
	public int testWithParameter(final int x) {
		return x;
	}

	@Argument
	public String testWithParameter(final String s) {
		return s;
	}

	@Argument(position = 1)
	public String testWithChangedPosition(final String x) {
		return x;
	}

	@Argument(permission = "test")
	public String testWithPermission() {
		return "permission";
	}
}
