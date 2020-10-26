package com.sparkystudios.traklibrary.game.service.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.mapstruct.factory.Mappers;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GameMappers {

    public static final DeveloperMapper DEVELOPER_MAPPER = Mappers.getMapper(DeveloperMapper.class);

    public static final FranchiseMapper FRANCHISE_MAPPER = Mappers.getMapper(FranchiseMapper.class);

    public static final GameBarcodeMapper GAME_BARCODE_MAPPER = Mappers.getMapper(GameBarcodeMapper.class);

    public static final GameDetailsMapper GAME_INFO_MAPPER = Mappers.getMapper(GameDetailsMapper.class);

    public static final GameMapper GAME_MAPPER = Mappers.getMapper(GameMapper.class);

    public static final GameReleaseDateMapper GAME_RELEASE_DATE_MAPPER = Mappers.getMapper(GameReleaseDateMapper.class);

    public static final GameRequestMapper GAME_REQUEST_MAPPER = Mappers.getMapper(GameRequestMapper.class);

    public static final GameUserEntryMapper GAME_USER_ENTRY_MAPPER = Mappers.getMapper(GameUserEntryMapper.class);

    public static final GenreMapper GENRE_MAPPER = Mappers.getMapper(GenreMapper.class);

    public static final PlatformMapper PLATFORM_MAPPER = Mappers.getMapper(PlatformMapper.class);

    public static final PlatformReleaseDateMapper PLATFORM_RELEASE_DATE_MAPPER = Mappers.getMapper(PlatformReleaseDateMapper.class);

    public static final PublisherMapper PUBLISHER_MAPPER = Mappers.getMapper(PublisherMapper.class);
}
