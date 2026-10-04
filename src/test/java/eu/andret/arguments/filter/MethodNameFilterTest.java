package eu.andret.arguments.filter;

import eu.andret.arguments.AnnotatedCommand;
import eu.andret.arguments.filter.impl.MethodNameFilter;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.assertj.core.api.ThrowableAssert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MethodNameFilterTest {
	@DataProvider(name = "getData")
	static Object[][] getData() {
		return new Object[][]{
				{false, "testMethod", true},
				{false, "TestMethod", true},
				{true, "testMethod", true},
				{true, "TestMethod", false},
		};
	}

	@Test
	void staticAnnotatedMethodCalled() throws NoSuchMethodException {
		// given
		final String[] command = {};
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testStaticMethod");
		final IMethodNameFilter mapper = new MethodNameFilter();
		final AnnotatedCommand.Options options = new AnnotatedCommand.Options();

		// when
		final ThrowableAssert.ThrowingCallable callable = () -> mapper.filterMethodName(method, command, options);

		// then
		assertThatThrownBy(callable)
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("@Argument method cannot be static! Method: eu.andret.arguments.provider.TestMethodsProvider#testStaticMethod");
	}

	@Test
	void methodWithExceededPositionCalled() throws NoSuchMethodException {
		// given
		final String[] command = {};
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithExceededPosition");
		final IMethodNameFilter mapper = new MethodNameFilter();
		final AnnotatedCommand.Options options = new AnnotatedCommand.Options();

		// when
		final boolean result = mapper.filterMethodName(method, command, options);

		// then
		assertThat(result).isFalse();
	}

	@Test
	void methodWithoutAnnotation() throws NoSuchMethodException {
		// given
		final String[] command = {};
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithoutAnnotation");
		final IMethodNameFilter mapper = new MethodNameFilter();
		final AnnotatedCommand.Options options = new AnnotatedCommand.Options();

		// when
		final boolean result = mapper.filterMethodName(method, command, options);

		// then
		assertThat(result).isFalse();
	}

	@Test
	void method() throws NoSuchMethodException {
		// given
		final String[] command = {"testMethod"};
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethod");
		final IMethodNameFilter mapper = new MethodNameFilter();
		final AnnotatedCommand.Options options = new AnnotatedCommand.Options();

		// when
		final boolean result = mapper.filterMethodName(method, command, options);

		// then
		assertThat(result).isTrue();
	}

	@Test
	void methodWithAliases() throws NoSuchMethodException {
		// given
		final String[] command = {"testAlias1"};
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodWithAliases");
		final IMethodNameFilter mapper = new MethodNameFilter();
		final AnnotatedCommand.Options options = new AnnotatedCommand.Options();

		// when
		final boolean result = mapper.filterMethodName(method, command, options);

		// then
		assertThat(result).isTrue();
	}

	@Test
	void methodWithCorrectPosition() throws NoSuchMethodException {
		// given
		final String[] command = {"test", "testMethodSecondWithCorrectPosition"};
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodSecondWithCorrectPosition", String.class, String.class);
		final IMethodNameFilter mapper = new MethodNameFilter();
		final AnnotatedCommand.Options options = new AnnotatedCommand.Options();

		// when
		final boolean result = mapper.filterMethodName(method, command, options);

		// then
		assertThat(result).isTrue();
	}

	@Test(dataProvider = "getData")
	void methodCaseSensitiveWithIncorrectCase(final boolean caseSensitive, final String command, final boolean expected) throws NoSuchMethodException {
		// given
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethod");
		final IMethodNameFilter mapper = new MethodNameFilter();
		final AnnotatedCommand.Options options = new AnnotatedCommand.Options();
		options.setCaseSensitive(caseSensitive);

		// when
		final boolean result = mapper.filterMethodName(method, new String[]{command}, options);

		// then
		assertThat(result).isEqualTo(expected);
	}
}
