/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.api.annotation;

import eu.andret.arguments.AnnotatedCommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation to use on the sub-command class that extends the {@link AnnotatedCommandExecutor}.
 *
 * @author Andret
 * @since Oct 08, 2022
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SubCommand {
	/**
	 * The subcommand text.
	 *
	 * @return The text.
	 */
	String value();

	/**
	 * The parent to be able to build the tree.
	 *
	 * @return The parent class.
	 */
	Class<? extends AnnotatedCommandExecutor<? extends JavaPlugin>> parent();
}
