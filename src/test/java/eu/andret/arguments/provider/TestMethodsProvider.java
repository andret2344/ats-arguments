/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */
package eu.andret.arguments.provider;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.ExecutorType;
import eu.andret.arguments.annotation.Argument;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class TestMethodsProvider extends AnnotatedCommandExecutor {
	public TestMethodsProvider(CommandSender sender, JavaPlugin plugin) {
		super(sender, plugin);
	}

	@Argument(executorType = ExecutorType.PLAYER)
	public void testMethodWithExecutorTypePlayer() {
	}

	@Argument(executorType = ExecutorType.CONSOLE)
	public void testMethodWithExecutorTypeConsole() {
	}

	@Argument(executorType = ExecutorType.ALL)
	public void testMethodWithExecutorTypeAll() {
	}

	@Argument(permission = "ats.test.method")
	public void testMethodWithPermission() {
	}

	@Argument
	public static void testStaticMethod() {
	}

	@Argument(position = 1)
	public void testMethodWithExceededPosition() {
	}

	public void testMethodWithoutAnnotation() {
	}

	@Argument
	public void testMethod() {
	}

	@Argument(aliases = {"testAlias1"})
	public void testMethodWithAliases() {
	}

	@Argument(position = 1)
	public void testMethodWithCorrectPosition(String text) {
	}

	@Argument
	public void testMethodWithArgument(String text) {
	}

	@Argument
	public void testMethodWithVararg(String... text) {
	}

	@Argument(description = "test description")
	public void testMethodWithDescription() {
	}
}
