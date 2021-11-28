/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.entity.MappingConfig;
import eu.andret.arguments.mapper.IResponseMapper;
import eu.andret.arguments.mapper.IInstanceCreator;
import lombok.SneakyThrows;
import lombok.Value;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The implementation of {@link InstanceCreator}.
 *
 * @param <E> The JavaPlugin
 *
 * @author Andret
 * @since Nov 25, 2021
 */
@Value
public class InstanceCreator<E extends JavaPlugin> implements IInstanceCreator<E> {
	@NotNull
	IResponseMapper responseMapper;

	/**
	 * A constructor.
	 *
	 * @param mappingConfig The config.
	 */
	public InstanceCreator(@NotNull final MappingConfig mappingConfig) {
		this(new ResponseMapper(mappingConfig));
	}

	@NotNull
	@Override
	public List<String> invokeMethod(@NotNull final Method method,
									 @NotNull final AnnotatedCommandExecutor<E> executor,
									 @NotNull final Object[] data) {
		return Optional.ofNullable(invoke(method, executor, data))
				.map(result -> responseMapper.mapResponse(method, result))
				.stream()
				.flatMap(Collection::stream)
				.flatMap(String::lines)
				.collect(Collectors.toList());
	}

	@Nullable
	@SneakyThrows
	private Object invoke(@NotNull final Method method,
						  @NotNull final AnnotatedCommandExecutor<E> executor,
						  @NotNull final Object[] data) {
		return method.invoke(executor, data);
	}
}
