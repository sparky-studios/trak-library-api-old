package com.sparkystudios.traklibrary.game.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparkystudios.traklibrary.game.domain.AgeRating;
import com.sparkystudios.traklibrary.game.domain.GameUserEntryStatus;
import com.sparkystudios.traklibrary.game.server.assembler.*;
import com.sparkystudios.traklibrary.game.server.configuration.TrakHalJsonMediaTypeConfiguration;
import com.sparkystudios.traklibrary.game.server.converter.JsonMergePatchHttpMessageConverter;
import com.sparkystudios.traklibrary.game.server.exception.GlobalExceptionHandler;
import com.sparkystudios.traklibrary.game.server.utils.ResponseVerifier;
import com.sparkystudios.traklibrary.game.service.*;
import com.sparkystudios.traklibrary.game.service.dto.*;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

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
    private GameInfoService gameInfoService;

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

    @MockBean
    private GameImageService gameImageService;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public GameRepresentationModelAssembler gameRepresentationModelAssembler() {
            return new GameRepresentationModelAssembler(null);
        }

        @Bean
        public GameInfoRepresentationModelAssembler gameInfoRepresentationModelAssembler() {
            return new GameInfoRepresentationModelAssembler(null);
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.debugMessage").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.subErrors").exists());
    }

    @Test
    void save_withValidGameDto_returns201AndValidResponse() throws Exception {
        // Arrange
        GameDto gameDto = new GameDto();
        gameDto.setId(5L);
        gameDto.setTitle("test-title");
        gameDto.setDescription("test-description");
        gameDto.setReleaseDate(LocalDate.now());
        gameDto.setAgeRating(AgeRating.MATURE);
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
    void saveGameImageForGameId_withInvalidFileData_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/1/image")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.debugMessage").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.subErrors").exists());
    }

    @Test
    void saveGameImageForGameId_withValidFileData_returns204() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "some xml".getBytes());

        Mockito.doNothing()
                .when(gameImageService).upload(ArgumentMatchers.anyLong(), ArgumentMatchers.any());

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/1/image")
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void findById_withValidId_return200AndValidResponse() throws Exception {
        // Arrange
        GameDto gameDto = new GameDto();
        gameDto.setId(5L);
        gameDto.setTitle("test-title");
        gameDto.setDescription("test-description");
        gameDto.setReleaseDate(LocalDate.now());
        gameDto.setAgeRating(AgeRating.MATURE);
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
    void findGameInfoById_withValidId_return200AndValidResponse() throws Exception {
        // Arrange
        GameInfoDto gameInfoDto = new GameInfoDto();
        gameInfoDto.setId(1L);
        gameInfoDto.setTitle("test-title-1");
        gameInfoDto.setDescription("test-description-1");
        gameInfoDto.setReleaseDate(LocalDate.now());
        gameInfoDto.setAgeRating(AgeRating.MATURE);
        gameInfoDto.setVersion(1L);

        Mockito.when(gameInfoService.findByGameId(ArgumentMatchers.anyLong()))
                .thenReturn(gameInfoDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/1/info")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyGameInfoDto("", resultActions, gameInfoDto);
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
        genreDto1.setVersion(1L);

        GenreDto genreDto2 = new GenreDto();
        genreDto2.setId(2L);
        genreDto2.setName("test-name-2");
        genreDto2.setDescription("test-description-2");
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
        platformDto1.setReleaseDate(LocalDate.now());
        platformDto1.setVersion(1L);

        PlatformDto platformDto2 = new PlatformDto();
        platformDto2.setId(2L);
        platformDto2.setName("test-name-2");
        platformDto2.setDescription("test-description-2");
        platformDto2.setReleaseDate(LocalDate.now());
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
        developerDto1.setVersion(1L);

        DeveloperDto developerDto2 = new DeveloperDto();
        developerDto2.setId(2L);
        developerDto2.setName("test-name-2");
        developerDto2.setDescription("test-description-2");
        developerDto2.setFoundedDate(LocalDate.now());
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
        publisherDto1.setVersion(1L);

        PublisherDto publisherDto2 = new PublisherDto();
        publisherDto2.setId(2L);
        publisherDto2.setName("test-name-2");
        publisherDto2.setDescription("test-description-2");
        publisherDto2.setFoundedDate(LocalDate.now());
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
    void findGameUserEntriesByGameId_withNoData_returns200AndEmptyPagedResponse() throws Exception {
        // Arrange
        Mockito.when(gameUserEntryService.findGameUserEntriesByGameId(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.totalElements", Matchers.is(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.totalPages").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.number").exists());
    }

    @Test
    void findGameUserEntriesByGameId_withSmallData_returns200AndValidPagedResponseWithNoPageLinks() throws Exception {
        // Arrange
        GameUserEntryDto gameUserEntryDto1 = new GameUserEntryDto();
        gameUserEntryDto1.setId(1L);
        gameUserEntryDto1.setGameId(1L);
        gameUserEntryDto1.setGameTitle("game-title-1");
        gameUserEntryDto1.setGameReleaseDate(LocalDate.now());
        gameUserEntryDto1.setPlatformId(1L);
        gameUserEntryDto1.setPlatformName("platform-name-1");
        gameUserEntryDto1.setUserId(1L);
        gameUserEntryDto1.setStatus(GameUserEntryStatus.COMPLETED);
        gameUserEntryDto1.setRating((short)4);
        gameUserEntryDto1.setVersion(1L);

        GameUserEntryDto gameUserEntryDto2 = new GameUserEntryDto();
        gameUserEntryDto2.setId(2L);
        gameUserEntryDto2.setGameId(2L);
        gameUserEntryDto2.setGameTitle("game-title-2");
        gameUserEntryDto2.setGameReleaseDate(LocalDate.now());
        gameUserEntryDto2.setPlatformId(2L);
        gameUserEntryDto2.setPlatformName("platform-name-2");
        gameUserEntryDto2.setUserId(2L);
        gameUserEntryDto2.setStatus(GameUserEntryStatus.IN_PROGRESS);
        gameUserEntryDto2.setRating((short)2);
        gameUserEntryDto2.setVersion(2L);

        Mockito.when(gameUserEntryService.findGameUserEntriesByGameId(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.totalElements", Matchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.totalPages").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.number").exists());

        ResponseVerifier.verifyGameUserEntryDto("._embedded.data[0]", resultActions, gameUserEntryDto1);
        ResponseVerifier.verifyGameUserEntryDto("._embedded.data[1]", resultActions, gameUserEntryDto2);
    }

    @Test
    void findGameUserEntriesByGameId_withData_returns200AndValidPagedResponseWithPageLinks() throws Exception {
        // Arrange
        GameUserEntryDto gameUserEntryDto1 = new GameUserEntryDto();
        gameUserEntryDto1.setId(1L);
        gameUserEntryDto1.setGameId(1L);
        gameUserEntryDto1.setGameTitle("game-title-1");
        gameUserEntryDto1.setGameReleaseDate(LocalDate.now());
        gameUserEntryDto1.setPlatformId(1L);
        gameUserEntryDto1.setPlatformName("platform-name-1");
        gameUserEntryDto1.setUserId(1L);
        gameUserEntryDto1.setStatus(GameUserEntryStatus.COMPLETED);
        gameUserEntryDto1.setRating((short)4);
        gameUserEntryDto1.setVersion(1L);

        GameUserEntryDto gameUserEntryDto2 = new GameUserEntryDto();
        gameUserEntryDto2.setId(2L);
        gameUserEntryDto2.setGameId(2L);
        gameUserEntryDto2.setGameTitle("game-title-2");
        gameUserEntryDto2.setGameReleaseDate(LocalDate.now());
        gameUserEntryDto2.setPlatformId(2L);
        gameUserEntryDto2.setPlatformName("platform-name-2");
        gameUserEntryDto2.setUserId(2L);
        gameUserEntryDto2.setStatus(GameUserEntryStatus.IN_PROGRESS);
        gameUserEntryDto2.setRating((short)2);
        gameUserEntryDto2.setVersion(2L);

        Mockito.when(gameUserEntryService.findGameUserEntriesByGameId(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.totalElements", Matchers.is(100)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.totalPages").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.number").exists());

        ResponseVerifier.verifyGameUserEntryDto("._embedded.data[0]", resultActions, gameUserEntryDto1);
        ResponseVerifier.verifyGameUserEntryDto("._embedded.data[1]", resultActions, gameUserEntryDto2);
    }

    @Test
    void findGameImageByGameId_withValidId_returns200() throws Exception {
        // Arrange
        ImageDataDto imageDataDto = new ImageDataDto();
        imageDataDto.setContent(new byte[] { 'a', 'b' });
        imageDataDto.setFilename("filename.png");

        Mockito.when(gameImageService.download(ArgumentMatchers.anyLong()))
                .thenReturn(imageDataDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/1/image")
                .accept(MediaType.APPLICATION_OCTET_STREAM_VALUE));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.totalElements", Matchers.is(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.totalPages").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.number").exists());
    }

    @Test
    void findAll_withSmallData_returns200AndValidPagedResponseWithNoPageLinks() throws Exception {
        // Arrange
        GameDto gameDto1 = new GameDto();
        gameDto1.setId(1L);
        gameDto1.setTitle("test-title-1");
        gameDto1.setDescription("test-description-1");
        gameDto1.setReleaseDate(LocalDate.now());
        gameDto1.setAgeRating(AgeRating.MATURE);
        gameDto1.setVersion(1L);

        GameDto gameDto2 = new GameDto();
        gameDto2.setId(2L);
        gameDto2.setTitle("test-title-2");
        gameDto2.setDescription("test-description-2");
        gameDto2.setReleaseDate(LocalDate.now());
        gameDto2.setAgeRating(AgeRating.MATURE);
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.totalElements", Matchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.totalPages").exists())
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
        gameDto1.setReleaseDate(LocalDate.now());
        gameDto1.setAgeRating(AgeRating.MATURE);
        gameDto1.setVersion(1L);

        GameDto gameDto2 = new GameDto();
        gameDto2.setId(2L);
        gameDto2.setTitle("test-title-2");
        gameDto2.setDescription("test-description-2");
        gameDto2.setReleaseDate(LocalDate.now());
        gameDto2.setAgeRating(AgeRating.MATURE);
        gameDto2.setVersion(2L);

        Mockito.when(gameService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(gameDto1, gameDto2));

        Mockito.when(gameService.count(ArgumentMatchers.any()))
                .thenReturn(100L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("?page=2")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.self").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.last").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.next").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.prev").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.size").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.totalElements", Matchers.is(100)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.totalPages").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.number").exists());

        ResponseVerifier.verifyGameDto("._embedded.data[0]", resultActions, gameDto1);
        ResponseVerifier.verifyGameDto("._embedded.data[1]", resultActions, gameDto2);
    }

    @Test
    void findAllGameInfo_withNoData_returns200AndEmptyPagedResponse() throws Exception {
        // Arrange
        Mockito.when(gameInfoService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());

        Mockito.when(gameInfoService.count(ArgumentMatchers.any()))
                .thenReturn(0L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/info")
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.totalElements", Matchers.is(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.totalPages").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.number").exists());
    }

    @Test
    void findAllGameInfo_withSmallData_returns200AndValidPagedResponseWithNoPageLinks() throws Exception {
        // Arrange
        GameInfoDto gameInfoDto1 = new GameInfoDto();
        gameInfoDto1.setId(1L);
        gameInfoDto1.setTitle("test-title-1");
        gameInfoDto1.setDescription("test-description-1");
        gameInfoDto1.setReleaseDate(LocalDate.now());
        gameInfoDto1.setAgeRating(AgeRating.MATURE);
        gameInfoDto1.setVersion(1L);

        GameInfoDto gameInfoDto2 = new GameInfoDto();
        gameInfoDto2.setId(2L);
        gameInfoDto2.setTitle("test-title-2");
        gameInfoDto2.setDescription("test-description-2");
        gameInfoDto2.setReleaseDate(LocalDate.now());
        gameInfoDto2.setAgeRating(AgeRating.MATURE);
        gameInfoDto2.setVersion(2L);

        Mockito.when(gameInfoService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(gameInfoDto1, gameInfoDto2));

        Mockito.when(gameService.count(ArgumentMatchers.any()))
                .thenReturn(2L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/info")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.self").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.last").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.next").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.prev").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.size").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.totalElements", Matchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.totalPages").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.number").exists());

        ResponseVerifier.verifyGameInfoDto("._embedded.data[0]", resultActions, gameInfoDto1);
        ResponseVerifier.verifyGameInfoDto("._embedded.data[1]", resultActions, gameInfoDto2);
    }

    @Test
    void findAllGameInfo_withData_returns200AndValidPagedResponseWithPageLinks() throws Exception {
        // Arrange
        GameInfoDto gameInfoDto1 = new GameInfoDto();
        gameInfoDto1.setId(1L);
        gameInfoDto1.setTitle("test-title-1");
        gameInfoDto1.setDescription("test-description-1");
        gameInfoDto1.setReleaseDate(LocalDate.now());
        gameInfoDto1.setAgeRating(AgeRating.MATURE);
        gameInfoDto1.setVersion(1L);

        GameInfoDto gameInfoDto2 = new GameInfoDto();
        gameInfoDto2.setId(2L);
        gameInfoDto2.setTitle("test-title-2");
        gameInfoDto2.setDescription("test-description-2");
        gameInfoDto2.setReleaseDate(LocalDate.now());
        gameInfoDto2.setAgeRating(AgeRating.MATURE);
        gameInfoDto2.setVersion(2L);

        Mockito.when(gameInfoService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(gameInfoDto1, gameInfoDto2));

        Mockito.when(gameInfoService.count(ArgumentMatchers.any()))
                .thenReturn(100L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/info?page=2")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.self").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.last").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.next").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.prev").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.size").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.totalElements", Matchers.is(100)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.totalPages").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.page.number").exists());

        ResponseVerifier.verifyGameInfoDto("._embedded.data[0]", resultActions, gameInfoDto1);
        ResponseVerifier.verifyGameInfoDto("._embedded.data[1]", resultActions, gameInfoDto2);
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.debugMessage").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.subErrors").exists());
    }

    @Test
    void update_withValidGameDto_returns200AndValidResponse() throws Exception {
        // Arrange
        GameDto gameDto = new GameDto();
        gameDto.setId(5L);
        gameDto.setTitle("test-title");
        gameDto.setDescription("test-description");
        gameDto.setReleaseDate(LocalDate.now());
        gameDto.setAgeRating(AgeRating.MATURE);
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
        gameDto.setReleaseDate(LocalDate.now());
        gameDto.setAgeRating(AgeRating.MATURE);
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
