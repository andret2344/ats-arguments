package eu.andret.arguments;

import lombok.Value;

import java.util.function.Function;

@Value
class Mapper<T> {
	Class<T> clazz;
	Function<String, T> function;
}
