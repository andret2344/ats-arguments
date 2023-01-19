/*
 * Copyright Andret (c) 2018-2022. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class CommandTree<E extends JavaPlugin> {
	private final Node root;

	@RequiredArgsConstructor
	final class Node {
		@NotNull
		final Class<? extends AnnotatedCommandExecutor<E>> clazz;
		@NotNull
		final List<Node> children = new ArrayList<>();

		void add(@NotNull final Class<? extends AnnotatedCommandExecutor<E>> clazz) {
			children.add(new Node(clazz));
		}
	}

	public CommandTree(final Class<? extends AnnotatedCommandExecutor<E>> clazz) {
		root = new Node(clazz);
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
