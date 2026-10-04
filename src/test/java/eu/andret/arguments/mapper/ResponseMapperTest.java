package eu.andret.arguments.mapper;

import eu.andret.arguments.entity.MappingConfig;
import eu.andret.arguments.entity.ResponseMappingSet;
import eu.andret.arguments.mapper.impl.ResponseMapper;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.World;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ResponseMapperTest {
	private static final String MOCK_NAME = "mockName";
	private static final String MOCK_NAME_1 = "mockName1";
	private static final String MOCK_NAME_2 = "mockName2";

	@Test
	void mapStringWithoutMapper() throws NoSuchMethodException {
		// given
		final IResponseMapper responseMapper = new ResponseMapper(new MappingConfig());
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodReturningString");

		// when
		final List<String> strings = responseMapper.mapResponse(method, "");

		// then
		assertThat(strings).containsExactly("");
	}

	@Test
	void mapStringArrayWithoutMapper() throws NoSuchMethodException {
		// given
		final IResponseMapper responseMapper = new ResponseMapper(new MappingConfig());
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodReturningStringArray");
		final String[] result = {"one", "two"};

		// when
		final List<String> strings = responseMapper.mapResponse(method, result);

		// then
		assertThat(strings).containsExactly("one", "two");
	}

	@Test
	void mapStringListWithoutMapper() throws NoSuchMethodException {
		// given
		final IResponseMapper responseMapper = new ResponseMapper(new MappingConfig());
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodReturningStringList");
		final List<String> result = Arrays.asList("one", "two");

		// when
		final List<String> strings = responseMapper.mapResponse(method, result);

		// then
		assertThat(strings).containsExactly("one", "two");
	}

	@Test
	void mapSingleWorldWithArgumentResponseMapper() throws NoSuchMethodException {
		// given
		final MappingConfig mappingConfig = new MappingConfig();
		mappingConfig.addArgumentResponseMapper("worldResponse", new ResponseMappingSet<>(World.class, World::getName));
		final IResponseMapper responseMapper = new ResponseMapper(mappingConfig);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodReturningWorld");
		final World world = mock(World.class);
		when(world.getName()).thenReturn(MOCK_NAME);

		// when
		final List<String> strings = responseMapper.mapResponse(method, world);

		// then
		assertThat(strings).containsExactly(MOCK_NAME);
	}

	@Test
	void mapWorldArrayWithArgumentResponseMapper() throws NoSuchMethodException {
		// given
		final MappingConfig mappingConfig = new MappingConfig();
		mappingConfig.addArgumentResponseMapper("worldResponse", new ResponseMappingSet<>(World.class, World::getName));
		final IResponseMapper responseMapper = new ResponseMapper(mappingConfig);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodReturningWorldArray");
		final World world1 = mock(World.class);
		when(world1.getName()).thenReturn(MOCK_NAME_1);
		final World world2 = mock(World.class);
		when(world2.getName()).thenReturn(MOCK_NAME_2);
		final World[] result = {world1, world2};

		// when
		final List<String> strings = responseMapper.mapResponse(method, result);

		// then
		assertThat(strings).containsExactly(MOCK_NAME_1, MOCK_NAME_2);
	}

	@Test
	void mapWorldListWithArgumentResponseMapper() throws NoSuchMethodException {
		// given
		final MappingConfig mappingConfig = new MappingConfig();
		mappingConfig.addArgumentResponseMapper("worldResponse", new ResponseMappingSet<>(World.class, World::getName));
		final IResponseMapper responseMapper = new ResponseMapper(mappingConfig);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodReturningWorldList");
		final World world1 = mock(World.class);
		when(world1.getName()).thenReturn(MOCK_NAME_1);
		final World world2 = mock(World.class);
		when(world2.getName()).thenReturn(MOCK_NAME_2);
		final List<World> result = Arrays.asList(world1, world2);

		// when
		final List<String> strings = responseMapper.mapResponse(method, result);

		// then
		assertThat(strings).containsExactly(MOCK_NAME_1, MOCK_NAME_2);
	}

	@Test
	void mapPlayerWithTypeResponseMapper() throws NoSuchMethodException {
		// given
		final MappingConfig mappingConfig = new MappingConfig();
		mappingConfig.addTypeResponseMapper(World.class, new ResponseMappingSet<>(World.class, World::getName));
		final IResponseMapper responseMapper = new ResponseMapper(mappingConfig);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodReturningWorldWithoutMapper");
		final World world = mock(World.class);
		when(world.getName()).thenReturn(MOCK_NAME);

		// when
		final List<String> strings = responseMapper.mapResponse(method, world);

		// then
		assertThat(strings).containsExactly(MOCK_NAME);
	}

	@Test
	void mapPlayerArrayWithTypeResponseMapper() throws NoSuchMethodException {
		// given
		final MappingConfig mappingConfig = new MappingConfig();
		mappingConfig.addTypeResponseMapper(World.class, new ResponseMappingSet<>(World.class, World::getName));
		final IResponseMapper responseMapper = new ResponseMapper(mappingConfig);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodReturningWorldArrayWithoutMapper");
		final World world1 = mock(World.class);
		when(world1.getName()).thenReturn(MOCK_NAME_1);
		final World world2 = mock(World.class);
		when(world2.getName()).thenReturn(MOCK_NAME_2);
		final World[] result = {world1, world2};

		// when
		final List<String> strings = responseMapper.mapResponse(method, result);

		// then
		assertThat(strings).containsExactly(MOCK_NAME_1, MOCK_NAME_2);
	}

	@Test
	void mapPlayerListWithTypeResponseMapper() throws NoSuchMethodException {
		// given
		final MappingConfig mappingConfig = new MappingConfig();
		mappingConfig.addTypeResponseMapper(World.class, new ResponseMappingSet<>(World.class, World::getName));
		final IResponseMapper responseMapper = new ResponseMapper(mappingConfig);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodReturningWorldListWithoutMapper");
		final World world1 = mock(World.class);
		when(world1.getName()).thenReturn(MOCK_NAME_1);
		final World world2 = mock(World.class);
		when(world2.getName()).thenReturn(MOCK_NAME_2);
		final List<World> result = Arrays.asList(world1, world2);

		// when
		final List<String> strings = responseMapper.mapResponse(method, result);

		// then
		assertThat(strings).containsExactly(MOCK_NAME_1, MOCK_NAME_2);
	}
}
