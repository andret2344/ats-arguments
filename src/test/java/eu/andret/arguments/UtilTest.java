/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UtilTest {
	static Collection<Object[]> convertData() {
		final Object[][] objects = {
				{"123", int.class, 123},
				{"false", boolean.class, false},
				{"12.34", double.class, 12.34},
				{"test", char.class, 't'},
				{"99999999999", long.class, 99999999999L},
				{"9 9 9", String[].class, "9 9 9"},
				{"other", String.class, "other"}
		};
		return Arrays.asList(objects);
	}

	static Collection<Object[]> realClassData() {
		final Object[][] objects = {
				{"123", int.class, 123},
				{"false", boolean.class, false},
				{"true", boolean.class, true},
				{"12.34", double.class, 12.34},
				{"other", String.class, "other"}
		};
		return Arrays.asList(objects);
	}

	@ParameterizedTest
	@MethodSource("convertData")
	void convert(final String input, final Class<?> targetClass, final Object realValue) {
		// when
		final Object result = Util.convert(targetClass, input);

		// then
		assertEquals(realValue, result);
	}

	@Test
	void convertUnsupportedType() {
		// when
		final Executable result = () -> Util.convert(Stream.class, "input");

		// then
		assertThrows(UnsupportedOperationException.class, result);
	}

	@ParameterizedTest
	@MethodSource("realClassData")
	void getRealClass(final String input, final Class<?> targetClass, final Object realValue) {
		// when
		final Class<?> realClass = Util.getRealClass(input);

		// then
		assertTrue(targetClass.isAssignableFrom(realClass), String.format("Class %s is not assignable from %s", targetClass, realClass));
	}
}
