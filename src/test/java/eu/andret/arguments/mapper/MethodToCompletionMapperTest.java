package eu.andret.arguments.mapper;

import eu.andret.arguments.mapper.impl.MethodToCompletionMapper;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MethodToCompletionMapperTest {
	@Test
	void dummyTest() throws NoSuchMethodException {
		IMethodToCompletionMapper mapper = new MethodToCompletionMapper(new HashMap<>());

		Collection<String> collection = mapper.mapCommandToCompletion(TestMethodsProvider.class.getDeclaredMethod("testMethod"), new String[]{"testMethod"});

		assertTrue(collection.isEmpty());
	}
}
