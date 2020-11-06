package eu.andret.arguments;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FallbackExceptionTest {
	@Test
	void constructsTest() {
		// when
		FallbackException fallbackException = new FallbackException("fallback");

		// then
		assertEquals("fallback", fallbackException.getMessage());
	}
}
