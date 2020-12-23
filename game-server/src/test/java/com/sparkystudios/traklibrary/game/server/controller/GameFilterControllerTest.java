package com.sparkystudios.traklibrary.game.server.controller;

import com.sparkystudios.traklibrary.game.domain.AgeRating;
import com.sparkystudios.traklibrary.game.server.assembler.GameDetailsRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.server.assembler.GameFilterRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.server.assembler.GameUserEntryFilterRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.server.assembler.GameUserEntryRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.server.configuration.TrakHalJsonMediaTypeConfiguration;
import com.sparkystudios.traklibrary.game.server.converter.JsonMergePatchHttpMessageConverter;
import com.sparkystudios.traklibrary.game.server.exception.GlobalExceptionHandler;
import com.sparkystudios.traklibrary.game.server.utils.ResponseVerifier;
import com.sparkystudios.traklibrary.game.service.GameFilterService;
import com.sparkystudios.traklibrary.game.service.dto.GameDetailsDto;
import com.sparkystudios.traklibrary.game.service.dto.GameFiltersDto;
import com.sparkystudios.traklibrary.game.service.dto.GameUserEntryDto;
import com.sparkystudios.traklibrary.game.service.dto.GameUserEntryFiltersDto;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Import({GameFilterController.class, TrakHalJsonMediaTypeConfiguration.class, GlobalExceptionHandler.class, JsonMergePatchHttpMessageConverter.class})
@WebMvcTest(controllers = GameFilterController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class, useDefaultFilters = false)
@AutoConfigureMockMvc(addFilters = false)
public class GameFilterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameFilterService gameFilterService;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public GameFilterRepresentationModelAssembler gameFilterRepresentationModelAssembler() {
            return new GameFilterRepresentationModelAssembler();
        }

        @Bean
        public GameUserEntryFilterRepresentationModelAssembler gameUserEntryFilterRepresentationModelAssembler() {
            return new GameUserEntryFilterRepresentationModelAssembler();
        }

        @Bean
        public GameDetailsRepresentationModelAssembler gameDetailsRepresentationModelAssembler() {
            return new GameDetailsRepresentationModelAssembler(null);
        }

        @Bean
        public GameUserEntryRepresentationModelAssembler gameUserEntryRepresentationModelAssembler() {
            return new GameUserEntryRepresentationModelAssembler();
        }
    }

    @Test
    void getGameFilters_withNoData_return200AndValidResponse() throws Exception {
        // Arrange
        GameFiltersDto gameFiltersDto = new GameFiltersDto();
        gameFiltersDto.setPlatforms(Collections.emptySet());
        gameFiltersDto.setGenres(Collections.emptySet());

        Mockito.when(gameFilterService.getGameFilters())
                .thenReturn(gameFiltersDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/filters")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyGameFiltersDto("", resultActions);
    }

    @Test
    void getUserGameEntryFilters_withNoData_return200AndValidResponse() throws Exception {
        // Arrange
        GameUserEntryFiltersDto gameUserEntryFiltersDto = new GameUserEntryFiltersDto();
        gameUserEntryFiltersDto.setPlatforms(Collections.emptySet());
        gameUserEntryFiltersDto.setGenres(Collections.emptySet());

        Mockito.when(gameFilterService.getGameUserEntryFilters())
                .thenReturn(gameUserEntryFiltersDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/entries/filters")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyGameUserEntryFiltersDto("", resultActions);
    }

    @Test
    void findGamesByFilters_withNoRequestParameters_returns200AndEmptyCollection() throws Exception {
        // Arrange
        Mockito.when(gameFilterService.findGamesByFilters(ArgumentMatchers.anySet(), ArgumentMatchers.anySet(), ArgumentMatchers.anySet(), ArgumentMatchers.anySet(), ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/search")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded").doesNotExist());
    }

    @Test
    void findGamesByFilters_withPlatformIds_returns200AndCollection() throws Exception {
        // Arrange
        GameDetailsDto gameDetailsDto1 = new GameDetailsDto();
        gameDetailsDto1.setId(1L);
        gameDetailsDto1.setTitle("test-title-1");
        gameDetailsDto1.setDescription("test-description-1");
        gameDetailsDto1.setAgeRating(AgeRating.MATURE);
        gameDetailsDto1.setCreatedAt(LocalDateTime.now());
        gameDetailsDto1.setUpdatedAt(LocalDateTime.now());
        gameDetailsDto1.setVersion(1L);

        GameDetailsDto gameDetailsDto2 = new GameDetailsDto();
        gameDetailsDto2.setId(2L);
        gameDetailsDto2.setTitle("test-title-2");
        gameDetailsDto2.setDescription("test-description-2");
        gameDetailsDto2.setAgeRating(AgeRating.MATURE);
        gameDetailsDto2.setCreatedAt(LocalDateTime.now());
        gameDetailsDto2.setUpdatedAt(LocalDateTime.now());
        gameDetailsDto2.setVersion(2L);

        Mockito.when(gameFilterService.findGamesByFilters(ArgumentMatchers.anySet(), ArgumentMatchers.isNull(), ArgumentMatchers.isNull(), ArgumentMatchers.isNull(), ArgumentMatchers.any()))
                .thenReturn(List.of(gameDetailsDto1, gameDetailsDto2));

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/search?platform-ids=1,2")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyGameDetailsDto("._embedded.data[0]", resultActions, gameDetailsDto1);
        ResponseVerifier.verifyGameDetailsDto("._embedded.data[1]", resultActions, gameDetailsDto2);
    }

    @Test
    void findGamesByFilters_withGenreIds_returns200AndCollection() throws Exception {
        // Arrange
        GameDetailsDto gameDetailsDto1 = new GameDetailsDto();
        gameDetailsDto1.setId(1L);
        gameDetailsDto1.setTitle("test-title-1");
        gameDetailsDto1.setDescription("test-description-1");
        gameDetailsDto1.setAgeRating(AgeRating.MATURE);
        gameDetailsDto1.setCreatedAt(LocalDateTime.now());
        gameDetailsDto1.setUpdatedAt(LocalDateTime.now());
        gameDetailsDto1.setVersion(1L);

        GameDetailsDto gameDetailsDto2 = new GameDetailsDto();
        gameDetailsDto2.setId(2L);
        gameDetailsDto2.setTitle("test-title-2");
        gameDetailsDto2.setDescription("test-description-2");
        gameDetailsDto2.setAgeRating(AgeRating.MATURE);
        gameDetailsDto2.setCreatedAt(LocalDateTime.now());
        gameDetailsDto2.setUpdatedAt(LocalDateTime.now());
        gameDetailsDto2.setVersion(2L);

        Mockito.when(gameFilterService.findGamesByFilters(ArgumentMatchers.isNull(), ArgumentMatchers.anySet(), ArgumentMatchers.isNull(), ArgumentMatchers.isNull(), ArgumentMatchers.any()))
                .thenReturn(List.of(gameDetailsDto1, gameDetailsDto2));

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/search?genre-ids=1,2")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyGameDetailsDto("._embedded.data[0]", resultActions, gameDetailsDto1);
        ResponseVerifier.verifyGameDetailsDto("._embedded.data[1]", resultActions, gameDetailsDto2);
    }

    @Test
    void findGamesByFilters_withGameModes_returns200AndCollection() throws Exception {
        // Arrange
        GameDetailsDto gameDetailsDto1 = new GameDetailsDto();
        gameDetailsDto1.setId(1L);
        gameDetailsDto1.setTitle("test-title-1");
        gameDetailsDto1.setDescription("test-description-1");
        gameDetailsDto1.setAgeRating(AgeRating.MATURE);
        gameDetailsDto1.setCreatedAt(LocalDateTime.now());
        gameDetailsDto1.setUpdatedAt(LocalDateTime.now());
        gameDetailsDto1.setVersion(1L);

        GameDetailsDto gameDetailsDto2 = new GameDetailsDto();
        gameDetailsDto2.setId(2L);
        gameDetailsDto2.setTitle("test-title-2");
        gameDetailsDto2.setDescription("test-description-2");
        gameDetailsDto2.setAgeRating(AgeRating.MATURE);
        gameDetailsDto2.setCreatedAt(LocalDateTime.now());
        gameDetailsDto2.setUpdatedAt(LocalDateTime.now());
        gameDetailsDto2.setVersion(2L);

        Mockito.when(gameFilterService.findGamesByFilters(ArgumentMatchers.isNull(), ArgumentMatchers.isNull(), ArgumentMatchers.anySet(), ArgumentMatchers.isNull(), ArgumentMatchers.any()))
                .thenReturn(List.of(gameDetailsDto1, gameDetailsDto2));

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/search?game-modes=SINGLE_PLAYER")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyGameDetailsDto("._embedded.data[0]", resultActions, gameDetailsDto1);
        ResponseVerifier.verifyGameDetailsDto("._embedded.data[1]", resultActions, gameDetailsDto2);
    }

    @Test
    void findGamesByFilters_withAgeRatings_returns200AndCollection() throws Exception {
        // Arrange
        GameDetailsDto gameDetailsDto1 = new GameDetailsDto();
        gameDetailsDto1.setId(1L);
        gameDetailsDto1.setTitle("test-title-1");
        gameDetailsDto1.setDescription("test-description-1");
        gameDetailsDto1.setAgeRating(AgeRating.MATURE);
        gameDetailsDto1.setCreatedAt(LocalDateTime.now());
        gameDetailsDto1.setUpdatedAt(LocalDateTime.now());
        gameDetailsDto1.setVersion(1L);

        GameDetailsDto gameDetailsDto2 = new GameDetailsDto();
        gameDetailsDto2.setId(2L);
        gameDetailsDto2.setTitle("test-title-2");
        gameDetailsDto2.setDescription("test-description-2");
        gameDetailsDto2.setAgeRating(AgeRating.MATURE);
        gameDetailsDto2.setCreatedAt(LocalDateTime.now());
        gameDetailsDto2.setUpdatedAt(LocalDateTime.now());
        gameDetailsDto2.setVersion(2L);

        Mockito.when(gameFilterService.findGamesByFilters(ArgumentMatchers.isNull(), ArgumentMatchers.isNull(), ArgumentMatchers.isNull(), ArgumentMatchers.anySet(), ArgumentMatchers.any()))
                .thenReturn(List.of(gameDetailsDto1, gameDetailsDto2));

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/search?age-ratings=EVERYONE")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyGameDetailsDto("._embedded.data[0]", resultActions, gameDetailsDto1);
        ResponseVerifier.verifyGameDetailsDto("._embedded.data[1]", resultActions, gameDetailsDto2);
    }

    @Test
    void findGamesByFilters_withAllData_returns200AndCollection() throws Exception {
        // Arrange
        GameDetailsDto gameDetailsDto1 = new GameDetailsDto();
        gameDetailsDto1.setId(1L);
        gameDetailsDto1.setTitle("test-title-1");
        gameDetailsDto1.setDescription("test-description-1");
        gameDetailsDto1.setAgeRating(AgeRating.MATURE);
        gameDetailsDto1.setCreatedAt(LocalDateTime.now());
        gameDetailsDto1.setUpdatedAt(LocalDateTime.now());
        gameDetailsDto1.setVersion(1L);

        GameDetailsDto gameDetailsDto2 = new GameDetailsDto();
        gameDetailsDto2.setId(2L);
        gameDetailsDto2.setTitle("test-title-2");
        gameDetailsDto2.setDescription("test-description-2");
        gameDetailsDto2.setAgeRating(AgeRating.MATURE);
        gameDetailsDto2.setCreatedAt(LocalDateTime.now());
        gameDetailsDto2.setUpdatedAt(LocalDateTime.now());
        gameDetailsDto2.setVersion(2L);

        Mockito.when(gameFilterService.findGamesByFilters(ArgumentMatchers.anySet(), ArgumentMatchers.anySet(), ArgumentMatchers.anySet(), ArgumentMatchers.anySet(), ArgumentMatchers.any()))
                .thenReturn(List.of(gameDetailsDto1, gameDetailsDto2));

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/search?platform-ids=1,2&genre-ids=1,2&game-modes=SINGLE_PLAYER&age-ratings=EVERYONE")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyGameDetailsDto("._embedded.data[0]", resultActions, gameDetailsDto1);
        ResponseVerifier.verifyGameDetailsDto("._embedded.data[1]", resultActions, gameDetailsDto2);
    }

    @Test
    void findGameUserEntriesByFilters_withPlatformIds_returns200AndCollection() throws Exception {
        // Arrange
        Mockito.when(gameFilterService.findGameUserEntriesByFilters(ArgumentMatchers.anySet(), ArgumentMatchers.isNull(), ArgumentMatchers.isNull(), ArgumentMatchers.isNull(), ArgumentMatchers.isNull(), ArgumentMatchers.any()))
                .thenReturn(List.of(new GameUserEntryDto(), new GameUserEntryDto()));

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/entries/search?platform-ids=1,2")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyGameUserEntryDto("._embedded.data[0]", resultActions);
        ResponseVerifier.verifyGameUserEntryDto("._embedded.data[1]", resultActions);
    }

    @Test
    void findGameUserEntriesByFilters_withGenreIds_returns200AndCollection() throws Exception {
        // Arrange
        Mockito.when(gameFilterService.findGameUserEntriesByFilters(ArgumentMatchers.isNull(), ArgumentMatchers.anySet(), ArgumentMatchers.isNull(), ArgumentMatchers.isNull(), ArgumentMatchers.isNull(), ArgumentMatchers.any()))
                .thenReturn(List.of(new GameUserEntryDto(), new GameUserEntryDto()));

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/entries/search?genre-ids=1,2")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyGameUserEntryDto("._embedded.data[0]", resultActions);
        ResponseVerifier.verifyGameUserEntryDto("._embedded.data[1]", resultActions);
    }

    @Test
    void findGameUserEntriesByFilters_returns200AndCollection() throws Exception {
        // Arrange
        Mockito.when(gameFilterService.findGameUserEntriesByFilters(ArgumentMatchers.isNull(), ArgumentMatchers.isNull(), ArgumentMatchers.anySet(), ArgumentMatchers.isNull(), ArgumentMatchers.isNull(), ArgumentMatchers.any()))
                .thenReturn(List.of(new GameUserEntryDto(), new GameUserEntryDto()));

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/entries/search?game-modes=SINGLE_PLAYER")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyGameUserEntryDto("._embedded.data[0]", resultActions);
        ResponseVerifier.verifyGameUserEntryDto("._embedded.data[1]", resultActions);
    }

    @Test
    void findGameUserEntriesByFilters_withAgeRatings_returns200AndCollection() throws Exception {
        // Arrange
        Mockito.when(gameFilterService.findGameUserEntriesByFilters(ArgumentMatchers.isNull(), ArgumentMatchers.isNull(), ArgumentMatchers.isNull(), ArgumentMatchers.anySet(), ArgumentMatchers.isNull(), ArgumentMatchers.any()))
                .thenReturn(List.of(new GameUserEntryDto(), new GameUserEntryDto()));

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/entries/search?age-ratings=EVERYONE")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyGameUserEntryDto("._embedded.data[0]", resultActions);
        ResponseVerifier.verifyGameUserEntryDto("._embedded.data[1]", resultActions);
    }

    @Test
    void findGameUserEntriesByFilters_withStatuses_returns200AndCollection() throws Exception {
        // Arrange
        Mockito.when(gameFilterService.findGameUserEntriesByFilters(ArgumentMatchers.isNull(), ArgumentMatchers.isNull(), ArgumentMatchers.isNull(), ArgumentMatchers.isNull(), ArgumentMatchers.anySet(), ArgumentMatchers.any()))
                .thenReturn(List.of(new GameUserEntryDto(), new GameUserEntryDto()));

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/entries/search?statuses=BACKLOG")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyGameUserEntryDto("._embedded.data[0]", resultActions);
        ResponseVerifier.verifyGameUserEntryDto("._embedded.data[1]", resultActions);
    }

    @Test
    void findGameUserEntriesByFilters_withAllData_returns200AndCollection() throws Exception {
        // Arrange
        Mockito.when(gameFilterService.findGameUserEntriesByFilters(ArgumentMatchers.anySet(), ArgumentMatchers.anySet(), ArgumentMatchers.anySet(), ArgumentMatchers.anySet(), ArgumentMatchers.anySet(), ArgumentMatchers.any()))
                .thenReturn(List.of(new GameUserEntryDto(), new GameUserEntryDto()));
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/entries/search?platform-ids=1,2&genre-ids=1,2&game-modes=SINGLE_PLAYER&age-ratings=EVERYONE&statuses=BACKLOG")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyGameUserEntryDto("._embedded.data[0]", resultActions);
        ResponseVerifier.verifyGameUserEntryDto("._embedded.data[1]", resultActions);
    }
}
