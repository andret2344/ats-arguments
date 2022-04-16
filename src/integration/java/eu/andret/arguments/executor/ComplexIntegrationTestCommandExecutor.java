/*
 * Copyright Andret (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.executor;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.api.annotation.BaseCommand;
import eu.andret.arguments.api.annotation.TypeFallback;
import eu.andret.arguments.entity.SomeEnum;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

@BaseCommand("ComplexIntegrationTest")
public class ComplexIntegrationTestCommandExecutor extends AnnotatedCommandExecutor<JavaPlugin> {
	public ComplexIntegrationTestCommandExecutor(final CommandSender sender, final JavaPlugin plugin) {
		super(sender, plugin);
	}

	@Argument
	public String list() {
		return list(1);
	}

	@Argument
	public String list(final SomeEnum someEnum) {
		return list(1, someEnum);
	}

	@Argument
	public String list(final int number) {
		return "I've got " + number;
	}

	@Argument
	public String list(final int number, final SomeEnum someEnum) {
		return "I've got " + someEnum + " " + number + " times";
	}

	@TypeFallback(SomeEnum.class)
	public String someEnumFallback(final String someEnum) {
		return String.format("%s is not a valid value!", someEnum);
	}
}
