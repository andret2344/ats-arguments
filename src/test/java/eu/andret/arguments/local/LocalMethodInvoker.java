/*
 * Copyright Andret (c) 2018-2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.local;

import eu.andret.arguments.mapper.impl.MethodInvoker;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class LocalMethodInvoker extends MethodInvoker<JavaPlugin> {
	public LocalMethodInvoker(final JavaPlugin plugin) {
		super(plugin);
	}
}
