package com.sparkystudios.traklibrary.game.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparkystudios.traklibrary.game.domain.AgeRating;
import com.sparkystudios.traklibrary.game.domain.GameUserEntryStatus;
import com.sparkystudios.traklibrary.game.server.assembler.DeveloperRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.server.assembler.GameDetailsRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.server.assembler.GameRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.server.assembler.GameUserEntryRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.server.assembler.GenreRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.server.assembler.PlatformRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.server.assembler.PublisherRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.server.configuration.TrakHalJsonMediaTypeConfiguration;
import com.sparkystudios.traklibrary.game.server.converter.JsonMergePatchHttpMessageConverter;
import com.sparkystudios.traklibrary.game.server.exception.GlobalExceptionHandler;
import com.sparkystudios.traklibrary.game.server.utils.ResponseVerifier;
import com.sparkystudios.traklibrary.game.service.DeveloperService;
import com.sparkystudios.traklibrary.game.service.GameDetailsService;
import com.sparkystudios.traklibrary.game.service.GameService;
import com.sparkystudios.traklibrary.game.service.GameUserEntryService;
import com.sparkystudios.traklibrary.game.service.GenreService;
import com.sparkystudios.traklibrary.game.service.PlatformService;
import com.sparkystudios.traklibrary.game.service.PublisherService;
import com.sparkystudios.traklibrary.game.service.dto.DeveloperDto;
import com.sparkystudios.traklibrary.game.service.dto.GameDetailsDto;
import com.sparkystudios.traklibrary.game.service.dto.GameDto;
import com.sparkystudios.traklibrary.game.service.dto.GameUserEntryDto;
import com.sparkystudios.traklibrary.game.service.dto.GenreDto;
import com.sparkystudios.traklibrary.game.service.dto.PlatformDto;
import com.sparkystudios.traklibrary.game.service.dto.PublisherDto;
import org.hamcrest.Matchers;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Import({GameController.class, TrakHalJsonMediaTypeConfiguration.class, GlobalExceptionHandler.class, JsonMergePatchHttpMessageConverter.class})
@WebMvcTest(controllers = GameController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class, useDefaultFilters = false)
@AutoConfigureMockMvc(addFilters = false)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GameService gameService;

    @MockBean
    private GameDetailsService gameDetailsService;

    @MockBean
    private GenreService genreService;

    @MockBean
    private PlatformService platformService;

    @MockBean
    private DeveloperService developerService;

    @MockBean
    private PublisherService publisherService;

    @MockBean
    private GameUserEntryService gameUserEntryService;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public GameRepresentationModelAssembler gameRepresentationModelAssembler() {
            return new GameRepresentationModelAssembler(null);
        }

        @Bean
        public GameDetailsRepresentationModelAssembler gameDetailsRepresentationModelAssembler() {
            return new GameDetailsRepresentationModelAssembler(null);
        }

        @Bean
        public GenreRepresentationModelAssembler genreRepresentationModelAssembler() {
            return new GenreRepresentationModelAssembler(null, null);
        }

        @Bean
        public PlatformRepresentationModelAssembler platformRepresentationModelAssembler() {
            return new PlatformRepresentationModelAssembler(null);
        }

        @Bean
        public DeveloperRepresentationModelAssembler developerRepresentationModelAssembler() {
            return new DeveloperRepresentationModelAssembler(null);
        }

        @Bean
        public PublisherRepresentationModelAssembler publisherRepresentationModelAssembler() {
            return new PublisherRepresentationModelAssembler(null);
        }

        @Bean
        public GameUserEntryRepresentationModelAssembler gameUserEntryRepresentationModelAssembler() {
            return new GameUserEntryRepresentationModelAssembler();
        }
    }

    @Test
    void save_withInvalidGameDto_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json")
                .content(objectMapper.writeValueAsString(new GameDto())));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").exists());
    }

    @Test
    void save_withValidGameDto_returns201AndValidResponse() throws Exception {
        // Arrange
        GameDto gameDto = new GameDto();
        gameDto.setId(5L);
        gameDto.setTitle("test-title");
        gameDto.setDescription("test-description");
        gameDto.setCreatedAt(LocalDateTime.now());
        gameDto.setUpdatedAt(LocalDateTime.now());
        gameDto.setVersion(1L);

        Mockito.when(gameService.save(ArgumentMatchers.any()))
                .thenReturn(gameDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json")
                .content(objectMapper.writeValueAsString(gameDto)));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isCreated());

        ResponseVerifier.verifyGameDto("", resultActions, gameDto);
    }

    @Test
    void findById_withValidId_return200AndValidResponse() throws Exception {
        // Arrange
        GameDto gameDto = new GameDto();
        gameDto.setId(5L);
        gameDto.setTitle("test-title");
        gameDto.setDescription("test-description");
        gameDto.setSlug("test-slug");
        gameDto.setCreatedAt(LocalDateTime.now());
        gameDto.setUpdatedAt(LocalDateTime.now());
        gameDto.setVersion(1L);

        Mockito.when(gameService.findById(ArgumentMatchers.anyLong()))
                .thenReturn(gameDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/1")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyGameDto("", resultActions, gameDto);
    }


    @Test
    void findBySlug_withValidSlug_return200AndValidResponse() throws Exception {
        // Arrange
        GameDto gameDto = new GameDto();
        gameDto.setId(5L);
        gameDto.setTitle("test-title");
        gameDto.setDescription("test-description");
        gameDto.setSlug("test-slug");
        gameDto.setCreatedAt(LocalDateTime.now());
        gameDto.setUpdatedAt(LocalDateTime.now());
        gameDto.setVersion(1L);

        Mockito.when(gameService.findBySlug(ArgumentMatchers.anyString()))
                .thenReturn(gameDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/slug/test-slug")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyGameDto("", resultActions, gameDto);
    }

    @Test
    void findGameDetailsById_withValidId_return200AndValidResponse() throws Exception {
        // Arrange
        GameDetailsDto gameDetailsDto = new GameDetailsDto();
        gameDetailsDto.setId(1L);
        gameDetailsDto.setTitle("test-title-1");
        gameDetailsDto.setDescription("test-description-1");
        gameDetailsDto.setSlug("test-slug");
        gameDetailsDto.setCreatedAt(LocalDateTime.now());
        gameDetailsDto.setUpdatedAt(LocalDateTime.now());
        gameDetailsDto.setVersion(1L);

        Mockito.when(gameDetailsService.findByGameId(ArgumentMatchers.anyLong()))
                .thenReturn(gameDetailsDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/1/details")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyGameDetailsDto("", resultActions, gameDetailsDto);
    }

    @Test
    void findGameDetailsBySlug_withValidSlug_return200AndValidResponse() throws Exception {
        // Arrange
        GameDetailsDto gameDetailsDto = new GameDetailsDto();
        gameDetailsDto.setId(1L);
        gameDetailsDto.setTitle("test-title-1");
        gameDetailsDto.setDescription("test-description-1");
        gameDetailsDto.setSlug("test-slug");
        gameDetailsDto.setCreatedAt(LocalDateTime.now());
        gameDetailsDto.setUpdatedAt(LocalDateTime.now());
        gameDetailsDto.setVersion(1L);

        Mockito.when(gameDetailsService.findByGameSlug(ArgumentMatchers.anyString()))
                .thenReturn(gameDetailsDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/slug/test-slug/details")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyGameDetailsDto("", resultActions, gameDetailsDto);
    }

    @Test
    void findGenresById_withNoData_returns200AndEmptyCollection() throws Exception {
        // Arrange
        Mockito.when(genreService.findGenresByGameId(ArgumentMatchers.anyLong()))
                .thenReturn(Collections.emptyList());

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/1/genres")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("{}"));
    }

    @Test
    void findGenresById_withData_returns200AndCollection() throws Exception {
        // Arrange
        GenreDto genreDto1 = new GenreDto();
        genreDto1.setId(1L);
        genreDto1.setName("test-name-1");
        genreDto1.setDescription("test-description-1");
        genreDto1.setSlug("test-slug-1");
        genreDto1.setCreatedAt(LocalDateTime.now());
        genreDto1.setUpdatedAt(LocalDateTime.now());
        genreDto1.setVersion(1L);

        GenreDto genreDto2 = new GenreDto();
        genreDto2.setId(2L);
        genreDto2.setName("test-name-2");
        genreDto2.setDescription("test-description-2");
        genreDto2.setSlug("test-slug-2");
        genreDto2.setCreatedAt(LocalDateTime.now());
        genreDto2.setUpdatedAt(LocalDateTime.now());
        genreDto2.setVersion(2L);

        Mockito.when(genreService.findGenresByGameId(ArgumentMatchers.anyLong()))
                .thenReturn(Arrays.asList(genreDto1, genreDto2));

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/1/genres")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyGenreDto("._embedded.data[0]", resultActions, genreDto1);
        ResponseVerifier.verifyGenreDto("._embedded.data[1]", resultActions, genreDto2);
    }

    @Test
    void saveGenresForGameId_withNoBody_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/1/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").exists());
    }

    @Test
    void saveGenresForGameId_withBody_returns200AndValidResponse() throws Exception {
        // Arrange
        GameDto gameDto = new GameDto();
        gameDto.setId(5L);
        gameDto.setTitle("test-title");
        gameDto.setDescription("test-description");
        gameDto.setSlug("test-slug");
        gameDto.setCreatedAt(LocalDateTime.now());
        gameDto.setUpdatedAt(LocalDateTime.now());
        gameDto.setVersion(1L);

        Mockito.when(gameService.saveGenresForGameId(ArgumentMatchers.anyLong(), ArgumentMatchers.anyCollection()))
                .thenReturn(gameDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/1/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json")
                .content(objectMapper.writeValueAsString(List.of(0L, 1L))));

        // Assert
        ResponseVerifier.verifyGameDto("", resultActions, gameDto);
    }

    @Test
    void updateGenresForGameId_withNoBody_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/1/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").exists());
    }

    @Test
    void updateGenresForGameId_withBody_returns200AndValidResponse() throws Exception {
        // Arrange
        GameDto gameDto = new GameDto();
        gameDto.setId(5L);
        gameDto.setTitle("test-title");
        gameDto.setDescription("test-description");
        gameDto.setSlug("test-slug");
        gameDto.setCreatedAt(LocalDateTime.now());
        gameDto.setUpdatedAt(LocalDateTime.now());
        gameDto.setVersion(1L);

        Mockito.when(gameService.updateGenresForGameId(ArgumentMatchers.anyLong(), ArgumentMatchers.anyCollection()))
                .thenReturn(gameDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/1/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json")
                .content(objectMapper.writeValueAsString(List.of(0L, 1L))));

        // Assert
        ResponseVerifier.verifyGameDto("", resultActions, gameDto);
    }

    @Test
    void findPlatformsById_withNoData_returns200AndEmptyCollection() throws Exception {
        // Arrange
        Mockito.when(platformService.findPlatformsByGameId(ArgumentMatchers.anyLong()))
                .thenReturn(Collections.emptyList());

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/1/platforms")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("{}"));
    }

    @Test
    void findPlatformsById_withData_returns200AndCollection() throws Exception {
        // Arrange
        PlatformDto platformDto1 = new PlatformDto();
        platformDto1.setId(1L);
        platformDto1.setName("test-name-1");
        platformDto1.setDescription("test-description-1");
        platformDto1.setSlug("test-slug-1");
        platformDto1.setCreatedAt(LocalDateTime.now());
        platformDto1.setUpdatedAt(LocalDateTime.now());
        platformDto1.setVersion(1L);

        PlatformDto platformDto2 = new PlatformDto();
        platformDto2.setId(2L);
        platformDto2.setName("test-name-2");
        platformDto2.setDescription("test-description-2");
        platformDto2.setSlug("test-slug-2");
        platformDto2.setCreatedAt(LocalDateTime.now());
        platformDto2.setUpdatedAt(LocalDateTime.now());
        platformDto2.setVersion(2L);

        Mockito.when(platformService.findPlatformsByGameId(ArgumentMatchers.anyLong()))
                .thenReturn(Arrays.asList(platformDto1, platformDto2));

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/1/platforms")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyPlatformDto("._embedded.data[0]", resultActions, platformDto1);
        ResponseVerifier.verifyPlatformDto("._embedded.data[1]", resultActions, platformDto2);
    }

    @Test
    void savePlatformsForGameId_withNoBody_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/1/platforms")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").exists());
    }

    @Test
    void savePlatformsForGameId_withBody_returns200AndValidResponse() throws Exception {
        // Arrange
        GameDto gameDto = new GameDto();
        gameDto.setId(5L);
        gameDto.setTitle("test-title");
        gameDto.setDescription("test-description");
        gameDto.setSlug("test-slug");
        gameDto.setCreatedAt(LocalDateTime.now());
        gameDto.setUpdatedAt(LocalDateTime.now());
        gameDto.setVersion(1L);

        Mockito.when(gameService.savePlatformsForGameId(ArgumentMatchers.anyLong(), ArgumentMatchers.anyCollection()))
                .thenReturn(gameDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/1/platforms")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json")
                .content(objectMapper.writeValueAsString(List.of(0L, 1L))));

        // Assert
        ResponseVerifier.verifyGameDto("", resultActions, gameDto);
    }

    @Test
    void updatePlatformsForGameId_withNoBody_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/1/platforms")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").exists());
    }

    @Test
    void updatePlatformsForGameId_withBody_returns200AndValidResponse() throws Exception {
        // Arrange
        GameDto gameDto = new GameDto();
        gameDto.setId(5L);
        gameDto.setTitle("test-title");
        gameDto.setDescription("test-description");
        gameDto.setSlug("test-slug");
        gameDto.setCreatedAt(LocalDateTime.now());
        gameDto.setUpdatedAt(LocalDateTime.now());
        gameDto.setVersion(1L);

        Mockito.when(gameService.updatePlatformsForGameId(ArgumentMatchers.anyLong(), ArgumentMatchers.anyCollection()))
                .thenReturn(gameDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/1/platforms")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json")
                .content(objectMapper.writeValueAsString(List.of(0L, 1L))));

        // Assert
        ResponseVerifier.verifyGameDto("", resultActions, gameDto);
    }

    @Test
    void findDevelopersById_withNoData_returns200AndEmptyCollection() throws Exception {
        // Arrange
        Mockito.when(developerService.findDevelopersByGameId(ArgumentMatchers.anyLong()))
                .thenReturn(Collections.emptyList());

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/1/developers")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("{}"));
    }

    @Test
    void findDevelopersById_withData_returns200AndCollection() throws Exception {
        // Arrange
        DeveloperDto developerDto1 = new DeveloperDto();
        developerDto1.setId(1L);
        developerDto1.setName("test-name-1");
        developerDto1.setDescription("test-description-1");
        developerDto1.setFoundedDate(LocalDate.now());
        developerDto1.setSlug("test-slug-1");
        developerDto1.setCreatedAt(LocalDateTime.now());
        developerDto1.setUpdatedAt(LocalDateTime.now());
        developerDto1.setVersion(1L);

        DeveloperDto developerDto2 = new DeveloperDto();
        developerDto2.setId(2L);
        developerDto2.setName("test-name-2");
        developerDto2.setDescription("test-description-2");
        developerDto2.setFoundedDate(LocalDate.now());
        developerDto2.setSlug("test-slug-2");
        developerDto2.setCreatedAt(LocalDateTime.now());
        developerDto2.setUpdatedAt(LocalDateTime.now());
        developerDto2.setVersion(2L);

        Mockito.when(developerService.findDevelopersByGameId(ArgumentMatchers.anyLong()))
                .thenReturn(Arrays.asList(developerDto1, developerDto2));

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/1/developers")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyDeveloperDto("._embedded.data[0]", resultActions, developerDto1);
        ResponseVerifier.verifyDeveloperDto("._embedded.data[1]", resultActions, developerDto2);
    }

    @Test
    void saveDevelopersForGameId_withNoBody_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/1/developers")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").exists());
    }

    @Test
    void saveDevelopersForGameId_withBody_returns200AndValidResponse() throws Exception {
        // Arrange
        GameDto gameDto = new GameDto();
        gameDto.setId(5L);
        gameDto.setTitle("test-title");
        gameDto.setDescription("test-description");
        gameDto.setSlug("test-slug");
        gameDto.setCreatedAt(LocalDateTime.now());
        gameDto.setUpdatedAt(LocalDateTime.now());
        gameDto.setVersion(1L);

        Mockito.when(gameService.saveDevelopersForGameId(ArgumentMatchers.anyLong(), ArgumentMatchers.anyCollection()))
                .thenReturn(gameDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/1/developers")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json")
                .content(objectMapper.writeValueAsString(List.of(0L, 1L))));

        // Assert
        ResponseVerifier.verifyGameDto("", resultActions, gameDto);
    }

    @Test
    void updateDevelopersForGameId_withNoBody_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/1/developers")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").exists());
    }

    @Test
    void updateDevelopersForGameId_withBody_returns200AndValidResponse() throws Exception {
        // Arrange
        GameDto gameDto = new GameDto();
        gameDto.setId(5L);
        gameDto.setTitle("test-title");
        gameDto.setDescription("test-description");
        gameDto.setSlug("test-slug");
        gameDto.setCreatedAt(LocalDateTime.now());
        gameDto.setUpdatedAt(LocalDateTime.now());
        gameDto.setVersion(1L);

        Mockito.when(gameService.updateDevelopersForGameId(ArgumentMatchers.anyLong(), ArgumentMatchers.anyCollection()))
                .thenReturn(gameDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/1/developers")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json")
                .content(objectMapper.writeValueAsString(List.of(0L, 1L))));

        // Assert
        ResponseVerifier.verifyGameDto("", resultActions, gameDto);
    }

    @Test
    void findPublishersById_withNoData_returns200AndEmptyCollection() throws Exception {
        // Arrange
        Mockito.when(developerService.findDevelopersByGameId(ArgumentMatchers.anyLong()))
                .thenReturn(Collections.emptyList());

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/1/publishers")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("{}"));
    }

    @Test
    void findPublishersById_withData_returns200AndCollection() throws Exception {
        // Arrange
        PublisherDto publisherDto1 = new PublisherDto();
        publisherDto1.setId(1L);
        publisherDto1.setName("test-name-1");
        publisherDto1.setDescription("test-description-1");
        publisherDto1.setFoundedDate(LocalDate.now());
        publisherDto1.setSlug("test-slug-1");
        publisherDto1.setCreatedAt(LocalDateTime.now());
        publisherDto1.setUpdatedAt(LocalDateTime.now());
        publisherDto1.setVersion(1L);

        PublisherDto publisherDto2 = new PublisherDto();
        publisherDto2.setId(2L);
        publisherDto2.setName("test-name-2");
        publisherDto2.setDescription("test-description-2");
        publisherDto2.setFoundedDate(LocalDate.now());
        publisherDto2.setSlug("test-slug-2");
        publisherDto2.setCreatedAt(LocalDateTime.now());
        publisherDto2.setUpdatedAt(LocalDateTime.now());
        publisherDto2.setVersion(2L);

        Mockito.when(publisherService.findPublishersByGameId(ArgumentMatchers.anyLong()))
                .thenReturn(Arrays.asList(publisherDto1, publisherDto2));

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/1/publishers")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyPublisherDto("._embedded.data[0]", resultActions, publisherDto1);
        ResponseVerifier.verifyPublisherDto("._embedded.data[1]", resultActions, publisherDto2);
    }


    @Test
    void savePublishersForGameId_withNoBody_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/1/publishers")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").exists());
    }

    @Test
    void savePublishersForGameId_withBody_returns200AndValidResponse() throws Exception {
        // Arrange
        GameDto gameDto = new GameDto();
        gameDto.setId(5L);
        gameDto.setTitle("test-title");
        gameDto.setDescription("test-description");
        gameDto.setSlug("test-slug");
        gameDto.setCreatedAt(LocalDateTime.now());
        gameDto.setUpdatedAt(LocalDateTime.now());
        gameDto.setVersion(1L);

        Mockito.when(gameService.savePublishersForGameId(ArgumentMatchers.anyLong(), ArgumentMatchers.anyCollection()))
                .thenReturn(gameDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/1/publishers")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json")
                .content(objectMapper.writeValueAsString(List.of(0L, 1L))));

        // Assert
        ResponseVerifier.verifyGameDto("", resultActions, gameDto);
    }

    @Test
    void updatePublishersForGameId_withNoBody_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/1/publishers")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").exists());
    }

    @Test
    void updatePublishersForGameId_withBody_returns200AndValidResponse() throws Exception {
        // Arrange
        GameDto gameDto = new GameDto();
        gameDto.setId(5L);
        gameDto.setTitle("test-title");
        gameDto.setDescription("test-description");
        gameDto.setSlug("test-slug");
        gameDto.setCreatedAt(LocalDateTime.now());
        gameDto.setUpdatedAt(LocalDateTime.now());
        gameDto.setVersion(1L);

        Mockito.when(gameService.updatePublishersForGameId(ArgumentMatchers.anyLong(), ArgumentMatchers.anyCollection()))
                .thenReturn(gameDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/1/publishers")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json")
                .content(objectMapper.writeValueAsString(List.of(0L, 1L))));

        // Assert
        ResponseVerifier.verifyGameDto("", resultActions, gameDto);
    }

    @Test
    void findGameUserEntriesByGameId_withNoData_returns200AndEmptyPagedResponse() throws Exception {
        // Arrange
        Mockito.when(gameUserEntryService.findGameUserEntriesByGameId(ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());

        Mockito.when(gameUserEntryService.countGameUserEntriesByGameId(ArgumentMatchers.anyLong()))
                .thenReturn(0L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/1/entries")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.self").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.last").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.next").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.prev").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.size").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.total_elements", Matchers.is(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.total_pages").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.number").exists());
    }

    @Test
    void findGameUserEntriesByGameId_withSmallData_returns200AndValidPagedResponseWithNoPageLinks() throws Exception {
        // Arrange
        GameUserEntryDto gameUserEntryDto1 = new GameUserEntryDto();
        gameUserEntryDto1.setId(1L);
        gameUserEntryDto1.setGameId(1L);
        gameUserEntryDto1.setGameTitle("game-title-1");
        gameUserEntryDto1.setUserId(1L);
        gameUserEntryDto1.setStatus(GameUserEntryStatus.COMPLETED);
        gameUserEntryDto1.setRating((short)4);
        gameUserEntryDto1.setCreatedAt(LocalDateTime.now());
        gameUserEntryDto1.setUpdatedAt(LocalDateTime.now());
        gameUserEntryDto1.setVersion(1L);

        GameUserEntryDto gameUserEntryDto2 = new GameUserEntryDto();
        gameUserEntryDto2.setId(2L);
        gameUserEntryDto2.setGameId(2L);
        gameUserEntryDto2.setGameTitle("game-title-2");
        gameUserEntryDto2.setUserId(2L);
        gameUserEntryDto2.setStatus(GameUserEntryStatus.IN_PROGRESS);
        gameUserEntryDto2.setRating((short)2);
        gameUserEntryDto2.setCreatedAt(LocalDateTime.now());
        gameUserEntryDto2.setUpdatedAt(LocalDateTime.now());
        gameUserEntryDto2.setVersion(2L);

        Mockito.when(gameUserEntryService.findGameUserEntriesByGameId(ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(gameUserEntryDto1, gameUserEntryDto2));

        Mockito.when(gameUserEntryService.countGameUserEntriesByGameId(ArgumentMatchers.anyLong()))
                .thenReturn(2L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/1/entries")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.self").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.last").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.next").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.prev").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.size").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.total_elements", Matchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.total_pages").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.number").exists());

        ResponseVerifier.verifyGameUserEntryDto("._embedded.data[0]", resultActions);
        ResponseVerifier.verifyGameUserEntryDto("._embedded.data[1]", resultActions);
    }

    @Test
    void findGameUserEntriesByGameId_withData_returns200AndValidPagedResponseWithPageLinks() throws Exception {
        // Arrange
        GameUserEntryDto gameUserEntryDto1 = new GameUserEntryDto();
        gameUserEntryDto1.setId(1L);
        gameUserEntryDto1.setGameId(1L);
        gameUserEntryDto1.setGameTitle("game-title-1");
        gameUserEntryDto1.setUserId(1L);
        gameUserEntryDto1.setStatus(GameUserEntryStatus.COMPLETED);
        gameUserEntryDto1.setRating((short)4);
        gameUserEntryDto1.setCreatedAt(LocalDateTime.now());
        gameUserEntryDto1.setUpdatedAt(LocalDateTime.now());
        gameUserEntryDto1.setVersion(1L);

        GameUserEntryDto gameUserEntryDto2 = new GameUserEntryDto();
        gameUserEntryDto2.setId(2L);
        gameUserEntryDto2.setGameId(2L);
        gameUserEntryDto2.setGameTitle("game-title-2");
        gameUserEntryDto2.setUserId(2L);
        gameUserEntryDto2.setStatus(GameUserEntryStatus.IN_PROGRESS);
        gameUserEntryDto2.setRating((short)2);
        gameUserEntryDto2.setCreatedAt(LocalDateTime.now());
        gameUserEntryDto2.setUpdatedAt(LocalDateTime.now());
        gameUserEntryDto2.setVersion(2L);

        Mockito.when(gameUserEntryService.findGameUserEntriesByGameId(ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(gameUserEntryDto1, gameUserEntryDto2));

        Mockito.when(gameUserEntryService.countGameUserEntriesByGameId(ArgumentMatchers.anyLong()))
                .thenReturn(100L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/1/entries?page=2")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.self").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.last").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.next").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.prev").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.size").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.total_elements", Matchers.is(100)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.total_pages").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.number").exists());

        ResponseVerifier.verifyGameUserEntryDto("._embedded.data[0]", resultActions);
        ResponseVerifier.verifyGameUserEntryDto("._embedded.data[1]", resultActions);
    }

    @Test
    void findAll_withNoData_returns200AndEmptyPagedResponse() throws Exception {
        // Arrange
        Mockito.when(gameService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());

        Mockito.when(gameService.count(ArgumentMatchers.any()))
                .thenReturn(0L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.self").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.last").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.next").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.prev").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.size").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.total_elements", Matchers.is(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.total_pages").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.number").exists());
    }

    @Test
    void findAll_withSmallData_returns200AndValidPagedResponseWithNoPageLinks() throws Exception {
        // Arrange
        GameDto gameDto1 = new GameDto();
        gameDto1.setId(1L);
        gameDto1.setTitle("test-title-1");
        gameDto1.setDescription("test-description-1");
        gameDto1.setSlug("test-slug-1");
        gameDto1.setCreatedAt(LocalDateTime.now());
        gameDto1.setUpdatedAt(LocalDateTime.now());
        gameDto1.setVersion(1L);

        GameDto gameDto2 = new GameDto();
        gameDto2.setId(2L);
        gameDto2.setTitle("test-title-2");
        gameDto2.setDescription("test-description-2");
        gameDto2.setSlug("test-slug-2");
        gameDto2.setCreatedAt(LocalDateTime.now());
        gameDto2.setUpdatedAt(LocalDateTime.now());
        gameDto2.setVersion(2L);

        Mockito.when(gameService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(gameDto1, gameDto2));

        Mockito.when(gameService.count(ArgumentMatchers.any()))
                .thenReturn(2L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.self").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.last").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.next").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.prev").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.size").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.total_elements", Matchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.total_pages").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.number").exists());

        ResponseVerifier.verifyGameDto("._embedded.data[0]", resultActions, gameDto1);
        ResponseVerifier.verifyGameDto("._embedded.data[1]", resultActions, gameDto2);
    }

    @Test
    void findAll_withData_returns200AndValidPagedResponseWithPageLinks() throws Exception {
        // Arrange
        GameDto gameDto1 = new GameDto();
        gameDto1.setId(1L);
        gameDto1.setTitle("test-title-1");
        gameDto1.setDescription("test-description-1");
        gameDto1.setSlug("test-slug-1");
        gameDto1.setCreatedAt(LocalDateTime.now());
        gameDto1.setUpdatedAt(LocalDateTime.now());
        gameDto1.setVersion(1L);

        GameDto gameDto2 = new GameDto();
        gameDto2.setId(2L);
        gameDto2.setTitle("test-title-2");
        gameDto2.setDescription("test-description-2");
        gameDto2.setSlug("test-slug-2");
        gameDto2.setCreatedAt(LocalDateTime.now());
        gameDto2.setUpdatedAt(LocalDateTime.now());
        gameDto2.setVersion(2L);

        Mockito.when(gameService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(gameDto1, gameDto2));

        Mockito.when(gameService.count(ArgumentMatchers.any()))
                .thenReturn(100L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/?page=2")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.self").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.last").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.next").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.prev").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.size").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.total_elements", Matchers.is(100)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.total_pages").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.number").exists());

        ResponseVerifier.verifyGameDto("._embedded.data[0]", resultActions, gameDto1);
        ResponseVerifier.verifyGameDto("._embedded.data[1]", resultActions, gameDto2);
    }

    @Test
    void findAllGameDetails_withNoData_returns200AndEmptyPagedResponse() throws Exception {
        // Arrange
        Mockito.when(gameDetailsService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());

        Mockito.when(gameDetailsService.count(ArgumentMatchers.any()))
                .thenReturn(0L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/details")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.self").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.last").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.next").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.prev").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.size").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.total_elements", Matchers.is(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.total_pages").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.number").exists());
    }

    @Test
    void findAllGameDetails_withSmallData_returns200AndValidPagedResponseWithNoPageLinks() throws Exception {
        // Arrange
        GameDetailsDto gameDetailsDto1 = new GameDetailsDto();
        gameDetailsDto1.setId(1L);
        gameDetailsDto1.setTitle("test-title-1");
        gameDetailsDto1.setDescription("test-description-1");
        gameDetailsDto1.setSlug("test-slug-1");
        gameDetailsDto1.setCreatedAt(LocalDateTime.now());
        gameDetailsDto1.setUpdatedAt(LocalDateTime.now());
        gameDetailsDto1.setVersion(1L);

        GameDetailsDto gameDetailsDto2 = new GameDetailsDto();
        gameDetailsDto2.setId(2L);
        gameDetailsDto2.setTitle("test-title-2");
        gameDetailsDto2.setDescription("test-description-2");
        gameDetailsDto2.setSlug("test-slug-2");
        gameDetailsDto2.setCreatedAt(LocalDateTime.now());
        gameDetailsDto2.setUpdatedAt(LocalDateTime.now());
        gameDetailsDto2.setVersion(2L);

        Mockito.when(gameDetailsService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(gameDetailsDto1, gameDetailsDto2));

        Mockito.when(gameService.count(ArgumentMatchers.any()))
                .thenReturn(2L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/details")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.self").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.last").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.next").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.prev").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.size").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.total_elements", Matchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.total_pages").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.number").exists());

        ResponseVerifier.verifyGameDetailsDto("._embedded.data[0]", resultActions, gameDetailsDto1);
        ResponseVerifier.verifyGameDetailsDto("._embedded.data[1]", resultActions, gameDetailsDto2);
    }

    @Test
    void findAllGameDetails_withData_returns200AndValidPagedResponseWithPageLinks() throws Exception {
        // Arrange
        GameDetailsDto gameDetailsDto1 = new GameDetailsDto();
        gameDetailsDto1.setId(1L);
        gameDetailsDto1.setTitle("test-title-1");
        gameDetailsDto1.setDescription("test-description-1");
        gameDetailsDto1.setSlug("test-slug-1");
        gameDetailsDto1.setCreatedAt(LocalDateTime.now());
        gameDetailsDto1.setUpdatedAt(LocalDateTime.now());
        gameDetailsDto1.setVersion(1L);

        GameDetailsDto gameDetailsDto2 = new GameDetailsDto();
        gameDetailsDto2.setId(2L);
        gameDetailsDto2.setTitle("test-title-2");
        gameDetailsDto2.setDescription("test-description-2");
        gameDetailsDto2.setSlug("test-slug-2");
        gameDetailsDto2.setCreatedAt(LocalDateTime.now());
        gameDetailsDto2.setUpdatedAt(LocalDateTime.now());
        gameDetailsDto2.setVersion(2L);

        Mockito.when(gameDetailsService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(gameDetailsDto1, gameDetailsDto2));

        Mockito.when(gameDetailsService.count(ArgumentMatchers.any()))
                .thenReturn(100L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/details?page=2")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.self").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.last").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.next").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.prev").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.size").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.total_elements", Matchers.is(100)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.total_pages").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.number").exists());

        ResponseVerifier.verifyGameDetailsDto("._embedded.data[0]", resultActions, gameDetailsDto1);
        ResponseVerifier.verifyGameDetailsDto("._embedded.data[1]", resultActions, gameDetailsDto2);
    }

    @Test
    void update_withInvalidGameDto_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json")
                .content(objectMapper.writeValueAsString(new GameDto())));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").exists());
    }

    @Test
    void update_withValidGameDto_returns200AndValidResponse() throws Exception {
        // Arrange
        GameDto gameDto = new GameDto();
        gameDto.setId(5L);
        gameDto.setTitle("test-title");
        gameDto.setDescription("test-description");
        gameDto.setSlug("test-slug");
        gameDto.setCreatedAt(LocalDateTime.now());
        gameDto.setUpdatedAt(LocalDateTime.now());
        gameDto.setVersion(1L);

        Mockito.when(gameService.update(ArgumentMatchers.any()))
                .thenReturn(gameDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json")
                .content(objectMapper.writeValueAsString(gameDto)));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyGameDto("", resultActions, gameDto);
    }

    @Test
    void patch_withValidPatch_returns200AndValidResponse() throws Exception {
        // Arrange
        GameDto gameDto = new GameDto();
        gameDto.setId(5L);
        gameDto.setTitle("test-title");
        gameDto.setDescription("test-description");
        gameDto.setSlug("test-slug");
        gameDto.setCreatedAt(LocalDateTime.now());
        gameDto.setUpdatedAt(LocalDateTime.now());
        gameDto.setVersion(1L);

        Mockito.when(gameService.patch(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(gameDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.patch("/1")
                .contentType("application/merge-patch+json")
                .accept("application/vnd.traklibrary.v1.hal+json")
                .content("{ \"title\": \"test-title-2\" }"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyGameDto("", resultActions, gameDto);
    }

    @Test
    void deleteById_withValidId_returns204() throws Exception {
        // Arrange
        Mockito.doNothing()
                .when(gameService).deleteById(ArgumentMatchers.anyLong());

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/1")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
