/*
 * Copyright Andret (c) 2019. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.argument;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UtilTest {

	@Test
	public void convert() {
		Util util = Util.getInstance();
		int i = (int) util.convert(int.class, "123");
		assertEquals(123, i);
	}

	@Test
	public void getRealClass() {
		Util util = Util.getInstance();
		Class<?> realClass = util.getRealClass("123");
		assertEquals(Integer.class, realClass);
	}
}
