package com.sparkystudios.traklibrary.game.server.utils;

import com.sparkystudios.traklibrary.game.service.dto.*;
import org.hamcrest.Matchers;
import org.mockito.Mock;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class ResponseVerifier {

    public static void verifyDeveloperDto(String root, ResultActions resultActions, DeveloperDto developerDto) throws Exception {
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".id", Matchers.is((int)developerDto.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".name", Matchers.is(developerDto.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".description", Matchers.is(developerDto.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".foundedDate", Matchers.is(developerDto.getFoundedDate().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".createdAt").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".updatedAt").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".version", Matchers.is((int)developerDto.getVersion().longValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + "._links.self").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + "._links.games").exists());
    }

    public static void verifyFranchiseDto(String root, ResultActions resultActions, FranchiseDto franchiseDto) throws Exception {
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".id", Matchers.is((int)franchiseDto.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".title", Matchers.is(franchiseDto.getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".description", Matchers.is(franchiseDto.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".createdAt").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".updatedAt").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".version", Matchers.is((int)franchiseDto.getVersion().longValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + "._links.self").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + "._links.games").exists());
    }

    public static void verifyGameDto(String root, ResultActions resultActions, GameDto gameDto) throws Exception {
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".id", Matchers.is((int)gameDto.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".title", Matchers.is(gameDto.getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".description", Matchers.is(gameDto.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".ageRating", Matchers.is(gameDto.getAgeRating().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".gameModes").hasJsonPath())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".franchiseId", Matchers.is(gameDto.getFranchiseId() != null ? gameDto.getFranchiseId().intValue() : null)))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".createdAt").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".updatedAt").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".version", Matchers.is((int)gameDto.getVersion().longValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".releaseDates").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + "._links.self").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + "._links.image").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + "._links.platforms").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + "._links.genres").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + "._links.developers").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + "._links.publishers").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + "._links.entries").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + "._links.info").exists());

        if (gameDto.getFranchiseId() != null) {
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$" + root + "._links.franchise").exists());
        }
    }

    public static void verifyGameDetailsDto(String root, ResultActions resultActions, GameDetailsDto gameDetailsDto) throws Exception {
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".id", Matchers.is((int) gameDetailsDto.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".title", Matchers.is(gameDetailsDto.getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".description", Matchers.is(gameDetailsDto.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".ageRating", Matchers.is(gameDetailsDto.getAgeRating().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".gameModes").hasJsonPath())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".franchiseId", Matchers.is(gameDetailsDto.getFranchiseId() != null ? gameDetailsDto.getFranchiseId().intValue() : null)))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".franchise").hasJsonPath())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".createdAt").hasJsonPath())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".updatedAt").hasJsonPath())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".version", Matchers.is((int) gameDetailsDto.getVersion().longValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".platforms").hasJsonPath())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".publishers").hasJsonPath())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".genres").hasJsonPath())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".releaseDates").hasJsonPath())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + "._links.self").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + "._links.image").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + "._links.platforms").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + "._links.genres").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + "._links.developers").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + "._links.publishers").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + "._links.entries").exists());
    }

    public static void verifyGameRequestDto(String root, ResultActions resultActions, GameRequestDto gameRequestDto) throws Exception {
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".id", Matchers.is((int)gameRequestDto.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".title", Matchers.is(gameRequestDto.getTitle())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".notes", Matchers.is(gameRequestDto.getNotes())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".completed", Matchers.is(gameRequestDto.isCompleted())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".completedDate").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".userId", Matchers.is((int)gameRequestDto.getUserId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".createdAt").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".updatedAt").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".version", Matchers.is((int)gameRequestDto.getVersion().longValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + "._links.self").exists());
    }

    public static void verifyGameUserEntryDto(String root, ResultActions resultActions) throws Exception {
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".id").hasJsonPath())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".gameId").hasJsonPath())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".gameTitle").hasJsonPath())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".userId").hasJsonPath())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".status").hasJsonPath())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".publishers").hasJsonPath())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".rating").hasJsonPath())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".gameUserEntryPlatforms").hasJsonPath())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".createdAt").hasJsonPath())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".updatedAt").hasJsonPath())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".version").hasJsonPath())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + "._links.game").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + "._links.gameDetails").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + "._links.image").exists());
    }

    public static void verifyGenreDto(String root, ResultActions resultActions, GenreDto genreDto) throws Exception {
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".id", Matchers.is((int)genreDto.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".name", Matchers.is(genreDto.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".description", Matchers.is(genreDto.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".createdAt").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".updatedAt").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".version", Matchers.is((int)genreDto.getVersion().longValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + "._links.self").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + "._links.games").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + "._links.gameDetails").exists());
    }

    public static void verifyPlatformDto(String root, ResultActions resultActions, PlatformDto platformDto) throws Exception {
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".id", Matchers.is((int)platformDto.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".name", Matchers.is(platformDto.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".description", Matchers.is(platformDto.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".createdAt").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".updatedAt").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".version", Matchers.is((int)platformDto.getVersion().longValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".releaseDates").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + "._links.self").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + "._links.games").exists());
    }

    public static void verifyPublisherDto(String root, ResultActions resultActions, PublisherDto publisherDto) throws Exception {
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".id", Matchers.is((int)publisherDto.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".name", Matchers.is(publisherDto.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".description", Matchers.is(publisherDto.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".foundedDate", Matchers.is(publisherDto.getFoundedDate().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".createdAt").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".updatedAt").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + ".version", Matchers.is((int)publisherDto.getVersion().longValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + "._links.self").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$" + root + "._links.games").exists());
    }
}
