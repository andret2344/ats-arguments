/*
 * Copyright Andret (c) 2019. Copying and modifying allowed only keeping git link reference.
 */
package eu.andret.argument;

class Util {
	private static Util instance;
	private Util() {
	}

	Object convert(Class<?> c, String value) {
		if (c.isArray()) {
			return value;
		}
		if (c.isAssignableFrom(int.class)) {
			return Integer.parseInt(value);
		}
		if (c.isAssignableFrom(double.class)) {
			return Double.parseDouble(value);
		}
		if (c.isAssignableFrom(float.class)) {
			return Float.parseFloat(value);
		}
		if (c.isAssignableFrom(char.class)) {
			return value.charAt(0);
		}
		if (c.isAssignableFrom(short.class)) {
			return Short.parseShort(value);
		}
		if (c.isAssignableFrom(long.class)) {
			return Long.parseLong(value);
		}
		if (c.isAssignableFrom(byte.class)) {
			return Byte.parseByte(value);
		}
		if (c.isAssignableFrom(boolean.class)) {
			return Boolean.parseBoolean(value);
		}
		if (c.isAssignableFrom(String.class)) {
			return value;
		}
		throw new UnsupportedOperationException("Use primitive type or String!");
	}

	Class<?> getRealClass(String value) {
		try {
			Integer.parseInt(value);
			return int.class;
		} catch (Exception ex) {
			// empty
		}
		try {
			Double.parseDouble(value);
			return double.class;
		} catch (Exception ex) {
			// empty
		}
		try {
			Float.parseFloat(value);
			return float.class;
		} catch (Exception ex) {
			// empty
		}
		try {
			Long.parseLong(value);
			return int.class;
		} catch (Exception ex) {
			// empty
		}
		return String.class;
	}

	public static Util getInstance() {
		if (instance == null) {
			instance = new Util();
		}
		return instance;
	}
}
