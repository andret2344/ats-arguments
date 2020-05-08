/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.example;

import eu.andret.arguments.AnnotatedCommand;
import eu.andret.arguments.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin {
	@Override
	public void onEnable() {
		AnnotatedCommand command = CommandManager.registerCommand(TestCommand.class, this);
		command.setOnInsufficientPermissionsListener(sender -> sender.sendMessage("You don't have permissions"));
		command.setOnUnknownSubCommandExecutionListener(sender -> sender.sendMessage("I don't know what you want from me"));
		command.addArgumentMapper("basicPlayerMapper", Player.class, Bukkit::getPlayer);
		command.setAutoTranslateColors(true);
	}
}
