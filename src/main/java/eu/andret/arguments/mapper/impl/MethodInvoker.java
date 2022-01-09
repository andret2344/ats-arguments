/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.entity.MappingConfig;
import eu.andret.arguments.mapper.IMethodInvoker;
import eu.andret.arguments.mapper.IResponseMapper;
import lombok.AllArgsConstructor;
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
 * The method invoker implementation.
 *
 * @author Andret
 * @since Nov 25, 2021
 */
@Value
@AllArgsConstructor
public class MethodInvoker implements IMethodInvoker {
	@NotNull
	IResponseMapper responseMapper;

	/**
	 * A constructor.
	 *
	 * @param mappingConfig The config.
	 */
	public MethodInvoker(@NotNull final MappingConfig mappingConfig) {
		this(new ResponseMapper(mappingConfig));
	}

	@NotNull
	@Override
	public <E extends JavaPlugin> List<String> invokeMethod(@NotNull final Method method,
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
	private <E extends JavaPlugin> Object invoke(@NotNull final Method method,
												 @NotNull final AnnotatedCommandExecutor<E> executor,
												 @NotNull final Object[] data) {
		return method.invoke(executor, data);
	}
}
