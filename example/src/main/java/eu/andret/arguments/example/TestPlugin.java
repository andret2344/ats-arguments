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
		command.setOnUsageExampleListener((sender, description) -> {
			sender.sendMessage("Here's what you probably wanted: " + description);
			return true;
		});
		command.addArgumentMapper("basicPlayerMapper", Player.class, Bukkit::getPlayer);
	}
}

