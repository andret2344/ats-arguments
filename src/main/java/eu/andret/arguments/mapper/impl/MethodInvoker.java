package eu.andret.arguments.mapper.impl;

import eu.andret.arguments.AnnotatedCommandExecutor;
import eu.andret.arguments.entity.MappingConfig;
import eu.andret.arguments.mapper.IMethodInvoker;
import eu.andret.arguments.mapper.IResponseMapper;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * The method invoker implementation.
 *
 * @author Andret
 * @since Nov 25, 2021
 */
public record MethodInvoker(@NotNull IResponseMapper responseMapper) implements IMethodInvoker {
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
			@NotNull final Object[] data)
			throws ReflectiveOperationException {
		return Optional.ofNullable(method.invoke(executor, data))
				.map(result -> responseMapper.mapResponse(method, result))
				.stream()
				.flatMap(Collection::stream)
				.flatMap(String::lines)
				.toList();
	}
}
