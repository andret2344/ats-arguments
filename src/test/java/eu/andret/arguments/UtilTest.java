/*
 * Copyright Andret (c) 2019. Copying and modifying allowed only keeping git link reference.
 */
package eu.andret.arguments;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class UtilTest {
	private final String input;
	private final Class<?> targetClass;
	private final Object realValue;

	@Parameterized.Parameters
	public static Collection<Object[]> data() {
		Object[][] objects = {
				{"123", int.class, 123},
				{"false", boolean.class, false},
				{"12.34", double.class, 12.34},
				{"other", String.class, "other"}
		};
		return Arrays.asList(objects);
	}

	public UtilTest(String input, Class<?> targetClass, Object realValue) {
		this.input = input;
		this.targetClass = targetClass;
		this.realValue = realValue;
	}

	@Test
	public void convert() {
		Util util = Util.getInstance();
		Object i = util.convert(targetClass, input);
		assertEquals(realValue, i);
	}

	@Test
	public void getRealClass() {
		Util util = Util.getInstance();
		Class<?> realClass = util.getRealClass(input);
		assertTrue(String.format("Class %s is not assignable from %s", targetClass, realClass), targetClass.isAssignableFrom(realClass));
	}
}
