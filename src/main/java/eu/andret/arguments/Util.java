/*
 * Copyright Andret (c) 2019. Copying and modifying allowed only keeping git link reference.
 */
package eu.andret.arguments;

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

	/**
	 * Method that will determine the actual class by the content of String.
	 *
	 * @param value The value that need to be parsed. Accepts only int, double, boolean and String.
	 * Not matching any of them will result as String.
	 *
	 * @return the class, which value inside the string arguments matches
	 */
	Class<?> getRealClass(String value) {
		if (value.matches("\\d+")) {
			return int.class;
		}
		if (value.matches("(\\d*[.,]\\d+)|(\\d+[.,]\\d*)")) {
			return double.class;
		}
		if (value.equals("false") || value.equals("true")) {
			return boolean.class;
		}
		return String.class;
	}

	/**
	 * The singleton accessor method
	 *
	 * @return The instance of this singleton class
	 */
	static Util getInstance() {
		if (instance == null) {
			instance = new Util();
		}
		return instance;
	}
}
