/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.example;

import eu.andret.arguments.AnnotatedCommand;
import eu.andret.arguments.CommandManager;
import eu.andret.arguments.api.annotation.Fallback;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TestPlugin extends JavaPlugin {
	@Override
	public void onEnable() {
		final AnnotatedCommand command = CommandManager.registerCommand(TestCommand.class, this);
		command.setOnInsufficientPermissionsListener(sender -> sender.sendMessage("You don't have permissions"));
		command.setOnUnknownSubCommandExecutionListener(sender -> sender.sendMessage("I don't know what you want from me"));
		command.addArgumentMapper("basicPlayerMapper", Player.class, Bukkit::getPlayer, Fallback.ON_NULL);
		command.addTypeCompleter(boolean.class, Arrays.asList("true", "false"));
		command.addArgumentCompleter("basicPlayerCompleter", () -> Bukkit.getOnlinePlayers()
				.stream()
				.map(HumanEntity::getName)
				.collect(Collectors.toList()));
		command.getOptions().setAutoTranslateColors(true);
	}

	public boolean isSuperSecretSetting() {
		return false;
	}
}
