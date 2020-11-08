/*
 * Copyright Andret (c) 2018=2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The utility class for {@link Class} mappings.
 *
 * @author Andret
 * @since Apr 28, 2019
 */
@UtilityClass
@Value
@Getter(AccessLevel.NONE)
public class Util {
	Map<Class<?>, Predicate<String>> realClassPredicates = new HashMap<>();
	Map<Predicate<Class<?>>, Function<String, ?>> convertFunctions = new HashMap<>();

	static {
		realClassPredicates.put(int.class, value -> value.matches("\\d+"));
		realClassPredicates.put(double.class, value -> value.matches("(\\d*[.,]\\d+)|(\\d+[.,]\\d*)"));
		realClassPredicates.put(boolean.class, value -> value.equals("false") || value.equals("true"));

		convertFunctions.put(Class::isArray, value -> value);
		convertFunctions.put(c -> c.isAssignableFrom(int.class), Integer::parseInt);
		convertFunctions.put(c -> c.isAssignableFrom(double.class), Double::parseDouble);
		convertFunctions.put(c -> c.isAssignableFrom(char.class), value -> value.charAt(0));
		convertFunctions.put(c -> c.isAssignableFrom(long.class), Long::parseLong);
		convertFunctions.put(c -> c.isAssignableFrom(boolean.class), Boolean::parseBoolean);
		convertFunctions.put(c -> c.isAssignableFrom(String.class), value -> value);
	}

	/**
	 * Method that tries to convert value in {@link String} into {@link Class} type provides.
	 *
	 * @param clazz The {@link Class} which type variable is trying to be made
	 * @param value The {@link String} containing possible to convert value, eg. {@code "1"},
	 *        {@code "false"} or {@code "0.009"}.
	 *
	 * @return The {@link Object} with converted value, or not if no possible assignment found, or
	 * 		is an array.
	 */
	public Object convert(Class<?> clazz, String value) {
		return convertFunctions.entrySet()
				.stream()
				.filter(entry -> entry.getKey().test(clazz))
				.findFirst()
				.map(Map.Entry::getValue)
				.map(predicate -> predicate.apply(value))
				.orElseThrow(() -> new UnsupportedOperationException("Use primitive type or String!"));
	}

	/**
	 * Method that will determine the actual class by the content of {@link String}.
	 *
	 * @param value The value that need to be parsed. Accepts only int, double, boolean and {@link
	 *        String}. Not matching any of them will result as {@link String}.
	 *
	 * @return The {@link Class}, which value inside the {@link String} arguments matches
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public Class<?> getRealClass(String value) {
		return realClassPredicates.entrySet()
				.stream()
				.filter(entry -> entry.getValue().test(value))
				.map(Map.Entry::getKey)
				.findFirst()
				.orElse((Class) String.class);
	}
}
