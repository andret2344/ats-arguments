/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.provider;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.annotation.BaseCommand;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

@Value
@BaseCommand("malformed")
@EqualsAndHashCode(callSuper = true)
public class FewArgumentsClass extends AnnotatedCommandExecutor<JavaPlugin> {
	@SuppressWarnings("ConstructorParameters")
	public FewArgumentsClass(final CommandSender sender) {
		super(sender, null);
	}
}
