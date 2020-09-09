package com.traklibrary.game.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.traklibrary.game.domain.AgeRating;
import com.traklibrary.game.server.assembler.GameInfoRepresentationModelAssembler;
import com.traklibrary.game.server.assembler.GameRepresentationModelAssembler;
import com.traklibrary.game.server.assembler.GenreRepresentationModelAssembler;
import com.traklibrary.game.server.configuration.TrakHalJsonMediaTypeConfiguration;
import com.traklibrary.game.server.converter.JsonMergePatchHttpMessageConverter;
import com.traklibrary.game.server.exception.GlobalExceptionHandler;
import com.traklibrary.game.server.utils.ResponseVerifier;
import com.traklibrary.game.service.GameInfoService;
import com.traklibrary.game.service.GameService;
import com.traklibrary.game.service.GenreService;
import com.traklibrary.game.service.dto.GameDto;
import com.traklibrary.game.service.dto.GameInfoDto;
import com.traklibrary.game.service.dto.GameUserEntryDto;
import com.traklibrary.game.service.dto.GenreDto;
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
import java.util.Arrays;
import java.util.Collections;

@Import({GenreController.class, TrakHalJsonMediaTypeConfiguration.class, GlobalExceptionHandler.class, JsonMergePatchHttpMessageConverter.class})
@WebMvcTest(controllers = GenreController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class, useDefaultFilters = false)
@AutoConfigureMockMvc(addFilters = false)
public class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GenreService genreService;

    @MockBean
    private GameService gameService;

    @MockBean
    private GameInfoService gameInfoService;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public GenreRepresentationModelAssembler genreRepresentationModelAssembler() {
            return new GenreRepresentationModelAssembler(null, null);
        }

        @Bean
        public GameRepresentationModelAssembler gameRepresentationModelAssembler() {
            return new GameRepresentationModelAssembler(null);
        }

        @Bean
        public GameInfoRepresentationModelAssembler gameInfoRepresentationModelAssembler() {
            return new GameInfoRepresentationModelAssembler(null);
        }
    }

    @Test
    void save_withInvalidGenreDto_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json")
                .content(objectMapper.writeValueAsString(new GameUserEntryDto())));

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
    void save_withValidGenreDto_returns201AndValidResponse() throws Exception {
        // Arrange
        GenreDto genreDto = new GenreDto();
        genreDto.setId(1L);
        genreDto.setName("test-name-1");
        genreDto.setDescription("test-description-1");
        genreDto.setVersion(1L);

        Mockito.when(genreService.save(ArgumentMatchers.any()))
                .thenReturn(genreDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json")
                .content(objectMapper.writeValueAsString(genreDto)));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isCreated());

        ResponseVerifier.verifyGenreDto("", resultActions, genreDto);
    }

    @Test
    void findById_withValidId_return200AndValidResponse() throws Exception {
        // Arrange
        GenreDto genreDto = new GenreDto();
        genreDto.setId(1L);
        genreDto.setName("test-name-1");
        genreDto.setDescription("test-description-1");
        genreDto.setVersion(1L);

        Mockito.when(genreService.findById(ArgumentMatchers.anyLong()))
                .thenReturn(genreDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/genres/1")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyGenreDto("", resultActions, genreDto);
    }

    @Test
    void findGamesByGenreId_withNoData_returns200AndEmptyPagedResponse() throws Exception {
        // Arrange
        Mockito.when(gameService.findGamesByGenreId(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());

        Mockito.when(gameService.countGamesByGenreId(ArgumentMatchers.anyLong()))
                .thenReturn(0L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/genres/1/games")
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
    void findGamesByGenreId_withSmallData_returns200AndValidPagedResponseWithNoPageLinks() throws Exception {
        // Arrange
        GameDto gameDto1 = new GameDto();
        gameDto1.setId(1L);
        gameDto1.setTitle("test-title-1");
        gameDto1.setDescription("test-description-1");
        gameDto1.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        gameDto1.setReleaseDate(LocalDate.now());
        gameDto1.setVersion(1L);

        GameDto gameDto2 = new GameDto();
        gameDto2.setId(2L);
        gameDto2.setTitle("test-title-2");
        gameDto2.setDescription("test-description-2");
        gameDto2.setAgeRating(AgeRating.ADULTS_ONLY);
        gameDto2.setReleaseDate(LocalDate.now());
        gameDto2.setVersion(2L);

        Mockito.when(gameService.findGamesByGenreId(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(gameDto1, gameDto2));

        Mockito.when(gameService.countGamesByGenreId(ArgumentMatchers.anyLong()))
                .thenReturn(2L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/genres/1/games")
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
    void findGamesByGenreId_withData_returns200AndValidPagedResponseWithPageLinks() throws Exception {
        // Arrange
        GameDto gameDto1 = new GameDto();
        gameDto1.setId(1L);
        gameDto1.setTitle("test-title-1");
        gameDto1.setDescription("test-description-1");
        gameDto1.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        gameDto1.setReleaseDate(LocalDate.now());
        gameDto1.setVersion(1L);

        GameDto gameDto2 = new GameDto();
        gameDto2.setId(2L);
        gameDto2.setTitle("test-title-2");
        gameDto2.setDescription("test-description-2");
        gameDto2.setAgeRating(AgeRating.ADULTS_ONLY);
        gameDto2.setReleaseDate(LocalDate.now());
        gameDto2.setVersion(2L);

        Mockito.when(gameService.findGamesByGenreId(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(gameDto1, gameDto2));

        Mockito.when(gameService.countGamesByGenreId(ArgumentMatchers.anyLong()))
                .thenReturn(100L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/genres/1/games?page=2")
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
    void findGameInfosByGenreId_withNoData_returns200AndEmptyPagedResponse() throws Exception {
        // Arrange
        Mockito.when(gameInfoService.findByGenreId(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());

        Mockito.when(gameInfoService.countByGenreId(ArgumentMatchers.anyLong()))
                .thenReturn(0L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/genres/1/game-infos")
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
    void findGameInfosByGenreId_withSmallData_returns200AndValidPagedResponseWithNoPageLinks() throws Exception {
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

        Mockito.when(gameInfoService.findByGenreId(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(gameInfoDto1, gameInfoDto2));

        Mockito.when(gameInfoService.countByGenreId(ArgumentMatchers.anyLong()))
                .thenReturn(2L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/genres/1/game-infos")
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
    void findGameInfosByGenreId_withData_returns200AndValidPagedResponseWithPageLinks() throws Exception {
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

        Mockito.when(gameInfoService.findByGenreId(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(gameInfoDto1, gameInfoDto2));

        Mockito.when(gameInfoService.countByGenreId(ArgumentMatchers.anyLong()))
                .thenReturn(100L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/genres/1/game-infos?page=2")
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
    void findAll_withNoData_returns200AndEmptyPagedResponse() throws Exception {
        // Arrange
        Mockito.when(genreService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());

        Mockito.when(genreService.count(ArgumentMatchers.any()))
                .thenReturn(0L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/genres")
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

        Mockito.when(genreService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(genreDto1, genreDto2));

        Mockito.when(genreService.count(ArgumentMatchers.any()))
                .thenReturn(2L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/genres")
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

        ResponseVerifier.verifyGenreDto("._embedded.data[0]", resultActions, genreDto1);
        ResponseVerifier.verifyGenreDto("._embedded.data[1]", resultActions, genreDto2);
    }

    @Test
    void findAll_withData_returns200AndValidPagedResponseWithPageLinks() throws Exception {
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

        Mockito.when(genreService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(genreDto1, genreDto2));

        Mockito.when(genreService.count(ArgumentMatchers.any()))
                .thenReturn(100L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/genres?page=2")
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

        ResponseVerifier.verifyGenreDto("._embedded.data[0]", resultActions, genreDto1);
        ResponseVerifier.verifyGenreDto("._embedded.data[1]", resultActions, genreDto2);
    }

    @Test
    void update_withInvalidGenreDto_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json")
                .content(objectMapper.writeValueAsString(new GenreDto())));

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
    void update_withValidGenreDto_returns200AndValidResponse() throws Exception {
        // Arrange
        GenreDto genreDto = new GenreDto();
        genreDto.setId(1L);
        genreDto.setName("test-name-1");
        genreDto.setDescription("test-description-1");
        genreDto.setVersion(1L);

        Mockito.when(genreService.update(ArgumentMatchers.any()))
                .thenReturn(genreDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json")
                .content(objectMapper.writeValueAsString(genreDto)));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyGenreDto("", resultActions, genreDto);
    }

    @Test
    void patch_withValidPatch_returns200AndValidResponse() throws Exception {
        // Arrange
        GenreDto genreDto = new GenreDto();
        genreDto.setId(1L);
        genreDto.setName("test-name-1");
        genreDto.setDescription("test-description-1");
        genreDto.setVersion(1L);

        Mockito.when(genreService.patch(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(genreDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.patch("/genres/1")
                .contentType("application/merge-patch+json")
                .accept("application/vnd.traklibrary.v1.hal+json")
                .content("{ \"name\": \"test-name-2\" }"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyGenreDto("", resultActions, genreDto);
    }

    @Test
    void deleteById_withValidId_returns204() throws Exception {
        // Arrange
        Mockito.doNothing()
                .when(genreService).deleteById(ArgumentMatchers.anyLong());

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/genres/1")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
