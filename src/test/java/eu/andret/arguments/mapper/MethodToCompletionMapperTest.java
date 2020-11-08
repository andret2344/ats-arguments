package eu.andret.arguments.mapper;

import eu.andret.arguments.mapper.impl.MethodToCompletionMapper;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class MethodToCompletionMapperTest {
	@Test
	void dummyTest() throws NoSuchMethodException {
		IMethodToCompletionMapper mapper = new MethodToCompletionMapper(new HashMap<>(), new HashMap<>());
		CommandSender sender = mock(CommandSender.class);

		Collection<String> collection = mapper.mapCommandToCompletion(TestMethodsProvider.class.getDeclaredMethod("testMethod"), new String[]{"testMethod"}, sender);

		assertTrue(collection.isEmpty());
	}
}
