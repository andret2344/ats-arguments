/*
 * Copyright Andret (c) 2018-2021. Copying and modifying allowed only keeping git link reference.
 */

package eu.andret.arguments.mapper;

import eu.andret.arguments.entity.MappingConfig;
import eu.andret.arguments.entity.ResponseMappingSet;
import eu.andret.arguments.mapper.impl.ResponseMapper;
import eu.andret.arguments.provider.TestMethodsProvider;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResponseMapperTest {

	public static final String MOCK_NAME = "mockName";
	public static final String MOCK_NAME_2 = "mockName2";
	public static final String MOCK_NAME_1 = "mockName1";

	@Test
	void mapStringWithoutMapper() throws NoSuchMethodException {
		// given
		final IResponseMapper responseMapper = new ResponseMapper(new MappingConfig());
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodReturningString");

		// when
		final List<String> strings = responseMapper.mapResponse(method, "");

		// then
		assertEquals(1, strings.size());
		assertEquals("", strings.get(0));
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
		assertEquals(2, strings.size());
		assertEquals("one", strings.get(0));
		assertEquals("two", strings.get(1));
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
		assertEquals(2, strings.size());
		assertEquals("one", strings.get(0));
		assertEquals("two", strings.get(1));
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
		assertEquals(1, strings.size());
		assertEquals(MOCK_NAME, strings.get(0));
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
		assertEquals(2, strings.size());
		assertEquals(MOCK_NAME_1, strings.get(0));
		assertEquals(MOCK_NAME_2, strings.get(1));
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
		assertEquals(2, strings.size());
		assertEquals(MOCK_NAME_1, strings.get(0));
		assertEquals(MOCK_NAME_2, strings.get(1));
	}

	@Test
	void mapPlayerWithTypeResponseMapper() throws NoSuchMethodException {
		// given
		final MappingConfig mappingConfig = new MappingConfig();
		mappingConfig.addTypeResponseMapper(OfflinePlayer.class, new ResponseMappingSet<>(OfflinePlayer.class, OfflinePlayer::getName));
		final IResponseMapper responseMapper = new ResponseMapper(mappingConfig);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodReturningPlayer");
		final OfflinePlayer player = mock(OfflinePlayer.class);
		when(player.getName()).thenReturn(MOCK_NAME);

		// when
		final List<String> strings = responseMapper.mapResponse(method, player);

		// then
		assertEquals(1, strings.size());
		assertEquals(MOCK_NAME, strings.get(0));
	}

	@Test
	void mapPlayerArrayWithTypeResponseMapper() throws NoSuchMethodException {
		// given
		final MappingConfig mappingConfig = new MappingConfig();
		mappingConfig.addTypeResponseMapper(OfflinePlayer.class, new ResponseMappingSet<>(OfflinePlayer.class, OfflinePlayer::getName));
		final IResponseMapper responseMapper = new ResponseMapper(mappingConfig);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodReturningPlayerArray");
		final OfflinePlayer player1 = mock(OfflinePlayer.class);
		when(player1.getName()).thenReturn(MOCK_NAME_1);
		final OfflinePlayer player2 = mock(OfflinePlayer.class);
		when(player2.getName()).thenReturn(MOCK_NAME_2);
		final OfflinePlayer[] result = {player1, player2};

		// when
		final List<String> strings = responseMapper.mapResponse(method, result);

		// then
		assertEquals(2, strings.size());
		assertEquals(MOCK_NAME_1, strings.get(0));
		assertEquals(MOCK_NAME_2, strings.get(1));
	}

	@Test
	void mapPlayerListWithTypeResponseMapper() throws NoSuchMethodException {
		// given
		final MappingConfig mappingConfig = new MappingConfig();
		mappingConfig.addTypeResponseMapper(OfflinePlayer.class, new ResponseMappingSet<>(OfflinePlayer.class, OfflinePlayer::getName));
		final IResponseMapper responseMapper = new ResponseMapper(mappingConfig);
		final Method method = TestMethodsProvider.class.getDeclaredMethod("testMethodReturningPlayerList");
		final OfflinePlayer player1 = mock(OfflinePlayer.class);
		when(player1.getName()).thenReturn(MOCK_NAME_1);
		final OfflinePlayer player2 = mock(OfflinePlayer.class);
		when(player2.getName()).thenReturn(MOCK_NAME_2);
		final List<OfflinePlayer> result = Arrays.asList(player1, player2);

		// when
		final List<String> strings = responseMapper.mapResponse(method, result);

		// then
		assertEquals(2, strings.size());
		assertEquals(MOCK_NAME_1, strings.get(0));
		assertEquals(MOCK_NAME_2, strings.get(1));
	}
}
