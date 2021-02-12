/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.provider;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.api.annotation.BaseCommand;
import eu.andret.arguments.api.annotation.Completer;
import eu.andret.arguments.api.annotation.Fallback;
import eu.andret.arguments.api.annotation.Ignore;
import eu.andret.arguments.api.annotation.Param;
import eu.andret.arguments.api.entity.DisplayType;
import eu.andret.arguments.api.entity.ExecutorType;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@BaseCommand("test")
public class TestMethodsProvider extends AnnotatedCommandExecutor<JavaPlugin> {
	public TestMethodsProvider(final CommandSender sender, final JavaPlugin plugin) {
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
	public void testMethodWithCorrectPosition(final String text) {
	}

	@Argument
	public void testMethodWithArgument(final String text) {
	}

	@Argument
	public void testMethodWithVararg(final String... text) {
	}

	@Argument
	public void testMethodWithIntVararg(final int... text) {
	}

	@Argument(description = "test description")
	public void testMethodWithDescription() {
	}

	@Argument
	public void testMethodWithMultipleArguments(final String text, final int value, final boolean bool) {
	}

	@Argument
	public void testMethodWithArray(final String[] text) {
	}

	@Fallback
	public void testMethodWithParam(final String world) {
	}

	@Argument
	public void testMethodWithParam(@Param("testWorldMapper") final World world) {
	}

	@Argument
	public void testMethodWithParamVarArg(@Param("testWorldMapper") final World... material) {
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

	@Argument
	public void testMethodWithTypeCompletion(final boolean value) {
	}

	@Argument
	public void testMethodWithIgnoredTypeCompletion(@Ignore final boolean value) {
	}

	@Argument
	public void testMethodWithMismatchedTypeCompletion(final Player value) {
	}

	@Argument
	public void testMethodWithArgumentCompletion(@Completer("testWorldCompleter") final World world) {
	}

	@Argument
	public void testMethodWithVarArgArgumentCompletion(@Completer("testWorldCompleter") final World... worlds) {
	}

	@Argument
	public void testMethodWithMismatchedArgumentCompletion(@Completer("mismatch") final World world) {
	}
}
