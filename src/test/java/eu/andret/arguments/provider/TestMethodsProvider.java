/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.provider;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.annotation.Argument;
import eu.andret.arguments.annotation.BaseCommand;
import eu.andret.arguments.annotation.Param;
import eu.andret.arguments.entity.DisplayType;
import eu.andret.arguments.entity.ExecutorType;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

@BaseCommand("test")
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

	@Argument
	public void testMethodWithIntVararg(int... text) {
	}

	@Argument(description = "test description")
	public void testMethodWithDescription() {
	}

	@Argument
	public void testMethodWithMultipleArguments(String text, int value, boolean bool) {
	}

	@Argument
	public void testMethodWithArray(String[] text) {
	}

	@Argument
	public void testMethodWithParam(@Param("testWorldMapper") World block) {
	}

	@Argument
	public void testMethodWithParamVarArg(@Param("testWorldMapper") World... material) {
	}

	@Argument
	public void testMethodWithException() {
		throw new IllegalArgumentException();
	}

	@Argument(displayType = DisplayType.ALWAYS)
	public void testMethodDisplayedAlways() {
	}

	@Argument(displayType = DisplayType.IF_PERMS)
	public void testMethodDisplayedConditionally() {
	}

	@Argument(displayType = DisplayType.NONE)
	public void testMethodDisplayedNever() {
	}
}
