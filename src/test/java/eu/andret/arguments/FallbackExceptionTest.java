package eu.andret.arguments;

import eu.andret.arguments.api.annotation.Mapper;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class FallbackExceptionTest {
	@Test
	void gettersReturnConstructorValues() {
		// given
		final Mapper mapper = mock(Mapper.class);

		// when
		final FallbackException exception = new FallbackException("message", mapper, Integer.class, "value");

		// then
		assertThat(exception.getMessage()).isEqualTo("message");
		assertThat(exception.getMapper()).isSameAs(mapper);
		assertThat(exception.getTargetClass()).isEqualTo(Integer.class);
		assertThat(exception.getValue()).isEqualTo("value");
	}
}
