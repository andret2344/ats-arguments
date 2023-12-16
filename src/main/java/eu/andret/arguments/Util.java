/*
 * Copyright (c) 2018 Andret Tools System. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Simple utility class for type mappings.
 *
 * @author Andret
 * @since Apr 28, 2020
 */
@UtilityClass
@Value
@Getter(AccessLevel.NONE)
public class Util {
	private static final Pattern PATTERN_INTEGER = Pattern.compile("\\d+");
	private static final Pattern PATTERN_DOUBLE = Pattern.compile("(\\d*[.,]\\d+)|(\\d+[.,]\\d*)");

	Map<Class<?>, Predicate<String>> realClassPredicates = Map.ofEntries(
			Map.entry(int.class, value -> PATTERN_INTEGER.matcher(value).matches()),
			Map.entry(double.class, value -> PATTERN_DOUBLE.matcher(value).matches()),
			Map.entry(boolean.class, value -> value.equals("false") || value.equals("true"))
	);

	List<Converter<?>> converters = List.of(
			new Converter<>(String.class, Class::isArray, Function.identity()),
			new Converter<>(int.class, clazz -> clazz.isAssignableFrom(int.class), Integer::parseInt),
			new Converter<>(double.class, clazz -> clazz.isAssignableFrom(double.class), Double::parseDouble),
			new Converter<>(char.class, clazz -> clazz.isAssignableFrom(char.class), value -> value.charAt(0)),
			new Converter<>(long.class, clazz -> clazz.isAssignableFrom(long.class), Long::parseLong),
			new Converter<>(boolean.class, clazz -> clazz.isAssignableFrom(boolean.class), Boolean::parseBoolean),
			new Converter<>(String.class, clazz -> clazz.isAssignableFrom(String.class), Function.identity()));

	@Value
	private static class Converter<T> {
		@NotNull
		Class<T> source;
		@NotNull
		Predicate<Class<T>> predicate;
		@NotNull
		Function<String, T> function;
	}

	@NotNull
	@SuppressWarnings("unchecked")
	private <T> Optional<Converter<T>> getConverter(@NotNull final Class<T> clazz) {
		return converters.stream()
				.map(converter -> (Converter<T>) converter)
				.filter(entry -> entry.getPredicate().test(clazz))
				.findFirst();
	}

	/**
	 * Method that tries to convert value in string into class type provides.
	 *
	 * @param clazz The {@link Class} which type variable is trying to be made
	 * @param value The string containing possible to convert value, e.g. "1", "false" or "0.009".
	 * @param <T>   The predicted result type and the class type.
	 * @return The converted value, or not if no possible assignment found, or is an array.
	 */
	@NotNull
	public <T> T convert(@NotNull final Class<T> clazz, @NotNull final String value) {
		return getConverter(clazz)
				.map(Converter::getFunction)
				.map(function -> function.apply(value))
				.orElseThrow(() -> new UnsupportedOperationException("Use primitive type or String!"));
	}

	/**
	 * Method that will determine the actual class by the content of String.
	 *
	 * @param value The value that need to be parsed. Accepts only int, double, boolean and String. Not matching any
	 *              of them will result as String.
	 * @return the class, which value inside the string arguments matches
	 */
	@NotNull
	@SuppressWarnings({"unchecked", "rawtypes"})
	public Class<?> getRealClass(@NotNull final String value) {
		return realClassPredicates.entrySet()
				.stream()
				.filter(entry -> entry.getValue().test(value))
				.map(Map.Entry::getKey)
				.findFirst()
				.orElse((Class) String.class);
	}
}
