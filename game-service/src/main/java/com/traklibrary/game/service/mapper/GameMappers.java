package com.traklibrary.game.service.mapper;

import org.mapstruct.factory.Mappers;

public final class GameMappers {

    public static final DeveloperMapper DEVELOPER_MAPPER = Mappers.getMapper(DeveloperMapper.class);

    public static final GameBarcodeMapper GAME_BARCODE_MAPPER = Mappers.getMapper(GameBarcodeMapper.class);

    public static final GameInfoMapper GAME_INFO_MAPPER = Mappers.getMapper(GameInfoMapper.class);

    public static final GameMapper GAME_MAPPER = Mappers.getMapper(GameMapper.class);

    public static final GameRequestMapper GAME_REQUEST_MAPPER = Mappers.getMapper(GameRequestMapper.class);

    public static final GameUserEntryMapper GAME_USER_ENTRY_MAPPER = Mappers.getMapper(GameUserEntryMapper.class);

    public static final GenreMapper GENRE_MAPPER = Mappers.getMapper(GenreMapper.class);

    public static final PlatformMapper PLATFORM_MAPPER = Mappers.getMapper(PlatformMapper.class);

    public static final PublisherMapper PUBLISHER_MAPPER = Mappers.getMapper(PublisherMapper.class);
}
