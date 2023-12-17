/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.example;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.api.annotation.SubCommand;
import eu.andret.arguments.example.entity.SomeEnum;
import org.bukkit.command.CommandSender;

@SubCommand(value = "sub", parent = TestCommand.class)
public class TestSubCommand extends AnnotatedCommandExecutor<TestPlugin> {
	private final SomeEnum someEnum;

	public TestSubCommand(final CommandSender sender, final TestPlugin plugin, final SomeEnum someEnum) {
		super(sender, plugin);
		this.someEnum = someEnum;
	}

	@Argument
	public String test() {
		return someEnum.name();
	}
}
