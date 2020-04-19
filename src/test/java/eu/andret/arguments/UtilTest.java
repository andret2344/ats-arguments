/*
 * Copyright Andret (c) 2020. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UtilTest {
	public static Collection<Object[]> data() {
		Object[][] objects = {
				{"123", int.class, 123},
				{"false", boolean.class, false},
				{"12.34", double.class, 12.34},
				{"other", String.class, "other"}
		};
		return Arrays.asList(objects);
	}

	@ParameterizedTest
	@MethodSource("data")
	public void convert(String input, Class<?> targetClass, Object realValue) {
		Util util = Util.getInstance();
		Object i = util.convert(targetClass, input);
		assertEquals(realValue, i);
	}

	@ParameterizedTest
	@MethodSource("data")
	public void getRealClass(String input, Class<?> targetClass, Object realValue) {
		Util util = Util.getInstance();
		Class<?> realClass = util.getRealClass(input);
		assertTrue(targetClass.isAssignableFrom(realClass), String.format("Class %s is not assignable from %s", targetClass, realClass));
	}
}
