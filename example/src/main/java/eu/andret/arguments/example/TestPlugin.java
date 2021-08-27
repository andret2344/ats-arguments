/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.example;

import eu.andret.arguments.AnnotatedCommand;
import eu.andret.arguments.CommandManager;
import eu.andret.arguments.api.annotation.Fallback;
import eu.andret.arguments.example.entity.SomeEnum;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TestPlugin extends JavaPlugin {
	@Override
	public void onEnable() {
		final AnnotatedCommand<TestPlugin> testCommand = CommandManager.registerCommand(TestCommand.class, this);
		testCommand.setOnInsufficientPermissionsListener(sender -> sender.sendMessage("You don't have permissions"));
		testCommand.setOnUnknownSubCommandExecutionListener(sender -> sender.sendMessage("I don't know what you want from me"));
		testCommand.addTypeMapper(World.class, Bukkit::getWorld, Fallback.ON_NULL);
		testCommand.addTypeCompleter(World.class, Bukkit.getWorlds()
				.stream()
				.map(World::getName)
				.collect(Collectors.toList()));
		testCommand.addEnumMapper(SomeEnum.class, Fallback.ON_NULL);
		testCommand.addEnumCompleter(SomeEnum.class);
		testCommand.addArgumentMapper("basicPlayerMapper", Player.class, Bukkit::getPlayer, Fallback.ON_NULL);
		testCommand.addTypeCompleter(boolean.class, Arrays.asList("true", "false"));
		testCommand.addArgumentCompleter("basicPlayerCompleter", () -> Bukkit.getOnlinePlayers()
				.stream()
				.map(HumanEntity::getName)
				.collect(Collectors.toList()));
		testCommand.getOptions().setAutoTranslateColors(true);

		final AnnotatedCommand<TestPlugin> paramCommand = CommandManager.registerCommand(TestParametrizedCommand.class, this, getServer().getWorld("world"), 1);
		paramCommand.getOptions().setCaseSensitive(true);
		paramCommand.setOnUnknownSubCommandExecutionListener(sender -> sender.sendMessage("I don't know what you want from me"));
	}

	public boolean isSuperSecretSetting() {
		return false;
	}
}
