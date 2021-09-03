/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.provider;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.api.annotation.Argument;
import eu.andret.arguments.api.annotation.ArgumentFallback;
import eu.andret.arguments.api.annotation.BaseCommand;
import eu.andret.arguments.api.annotation.Completer;
import eu.andret.arguments.api.annotation.Ignore;
import eu.andret.arguments.api.annotation.Mapper;
import eu.andret.arguments.api.annotation.TypeFallback;
import eu.andret.arguments.api.entity.DisplayType;
import eu.andret.arguments.api.entity.ExecutorType;
import eu.andret.arguments.api.entity.FallbackPriority;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
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

	@SuppressWarnings("ArgumentMethodStatic")
	@Argument
	public static void testStaticMethod() {
	}

	@SuppressWarnings("PositionOutOfBounds")
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
	public void testMethodWithArray(@SuppressWarnings("ArrayParameter") final String[] text) {
	}

	@ArgumentFallback(value = "testWorldMapper", priority = FallbackPriority.LOW)
	public void testMethodArgumentFallback(final String world) {
	}

	@ArgumentFallback(value = "testWorldMapper", priority = FallbackPriority.HIGH)
	public void testMethodArgumentSecondFallback(final String world) {
	}

	@ArgumentFallback("testWorldMapper")
	public void testMethodArgumentFallbackWithWrongArgs(final String world, final int unnecessary) {
	}

	@ArgumentFallback("testWorldMapper")
	public void testMethodArgumentFallbackWithWrongArg(final int wrong) {
	}

	@Argument
	public void testMethodWithParam(@Mapper("testWorldMapper") final World world) {
	}

	@Argument
	public void testMethodWithParamVarArg(@Mapper("testWorldMapper") final World... material) {
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

	@Argument
	public void testMethodWithMappedArgument(final Location location) {
	}

	@TypeFallback(value = Location.class, priority = FallbackPriority.LOWEST)
	public void testMethodWithMappedArgumentTypeFallbackLowest(final String text) {
	}

	@TypeFallback(value = Location.class, priority = FallbackPriority.HIGHEST)
	public void testMethodWithMappedArgumentTypeFallbackHighest(final String text) {
	}

	@TypeFallback(Location.class)
	public void testMethodWithMappedArgumentTypeFallbackWrongArg(final World wrong) {
	}

	@TypeFallback(Location.class)
	public void testMethodWithMappedArgumentTypeFallbackWrongArgs(final String text, final World wrong) {
	}
}
