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
		command.addArgumentMapper("basicPlayerMapper", Player.class, Bukkit::getPlayer);
	}
}

