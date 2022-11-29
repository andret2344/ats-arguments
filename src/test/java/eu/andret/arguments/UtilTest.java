/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import org.assertj.core.api.ThrowableAssert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UtilTest {
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
				{"123", int.class},
				{"false", boolean.class},
				{"true", boolean.class},
				{"12.34", double.class},
				{"other", String.class}
		};
		return Arrays.asList(objects);
	}

	//	@ParameterizedTest
//	@MethodSource("convertData")
	void convert(final String input, final Class<?> targetClass, final Object realValue) {
		// when
		final Object result = Util.convert(targetClass, input);

		// then
		assertThat(result).isEqualTo(realValue);
	}

	@Test
	void convertUnsupportedType() {
		// when
		final ThrowableAssert.ThrowingCallable callable = () -> Util.convert(Stream.class, "input");

		// then
		assertThatThrownBy(callable).isInstanceOf(UnsupportedOperationException.class);
	}

	//	@ParameterizedTest
//	@MethodSource("realClassData")
	void getRealClass(final String input, final Class<?> targetClass) {
		// when
		final Class<?> realClass = Util.getRealClass(input);

		// then
		assertThat(targetClass).isAssignableFrom(realClass);
	}
}
