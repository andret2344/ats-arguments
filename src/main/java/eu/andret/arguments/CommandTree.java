/*
 * Copyright Andret (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandTree<E extends JavaPlugin> {
	@Getter
	private final Node root;

	@RequiredArgsConstructor
	@Getter
	public final class Node {
		@NotNull
		private final Class<? extends AnnotatedCommandExecutor<E>> clazz;
		@NotNull
		private final Object[] parameters;
		@NotNull
		private final List<Node> children = new ArrayList<>();

		void add(@NotNull final Class<? extends AnnotatedCommandExecutor<E>> clazz, @NotNull final Object... parameters) {
			children.add(new Node(clazz, parameters));
		}
	}

	public CommandTree(@NotNull final Class<? extends AnnotatedCommandExecutor<E>> clazz, @NotNull final Object... parameters) {
		root = new Node(clazz, parameters);
	}

	@Nullable
	public Node search(@NotNull final Class<? extends AnnotatedCommandExecutor<E>> clazz) {
		return search(root, clazz);
	}

	@Nullable
	private Node search(@NotNull final Node current, @NotNull final Class<? extends AnnotatedCommandExecutor<E>> clazz) {
		if (current.clazz.equals(clazz)) {
			return current;
		}
		return current.children.stream()
				.map(node -> search(node, clazz))
				.filter(Objects::nonNull)
				.findAny()
				.orElse(null);
	}
}
