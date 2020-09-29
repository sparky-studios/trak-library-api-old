package com.sparkystudios.traklibrary.game.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparkystudios.traklibrary.game.domain.AgeRating;
import com.sparkystudios.traklibrary.game.server.assembler.GameRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.server.assembler.PlatformRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.server.configuration.TrakHalJsonMediaTypeConfiguration;
import com.sparkystudios.traklibrary.game.server.converter.JsonMergePatchHttpMessageConverter;
import com.sparkystudios.traklibrary.game.server.exception.GlobalExceptionHandler;
import com.sparkystudios.traklibrary.game.server.utils.ResponseVerifier;
import com.sparkystudios.traklibrary.game.service.GameService;
import com.sparkystudios.traklibrary.game.service.PlatformService;
import com.sparkystudios.traklibrary.game.service.dto.GameDto;
import com.sparkystudios.traklibrary.game.service.dto.PlatformDto;
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

@Import({PlatformController.class, TrakHalJsonMediaTypeConfiguration.class, GlobalExceptionHandler.class, JsonMergePatchHttpMessageConverter.class})
@WebMvcTest(controllers = PlatformController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class, useDefaultFilters = false)
@AutoConfigureMockMvc(addFilters = false)
public class PlatformControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlatformService platformService;

    @MockBean
    private GameService gameService;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public PlatformRepresentationModelAssembler platformRepresentationModelAssembler() {
            return new PlatformRepresentationModelAssembler(null);
        }

        @Bean
        public GameRepresentationModelAssembler gameRepresentationModelAssembler() {
            return new GameRepresentationModelAssembler(null);
        }
    }

    @Test
    void save_withInvalidPlatformDto_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/platforms")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json")
                .content(objectMapper.writeValueAsString(new PlatformDto())));

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
    void save_withValidPlatformDto_returns201AndValidResponse() throws Exception {
        // Arrange
        PlatformDto platformDto = new PlatformDto();
        platformDto.setId(1L);
        platformDto.setName("test-name-1");
        platformDto.setDescription("test-description-1");
        platformDto.setCreatedAt(LocalDateTime.now());
        platformDto.setUpdatedAt(LocalDateTime.now());
        platformDto.setVersion(1L);

        Mockito.when(platformService.save(ArgumentMatchers.any()))
                .thenReturn(platformDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/platforms")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json")
                .content(objectMapper.writeValueAsString(platformDto)));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isCreated());

        ResponseVerifier.verifyPlatformDto("", resultActions, platformDto);
    }

    @Test
    void findById_withValidId_return200AndValidResponse() throws Exception {
        // Arrange
        PlatformDto platformDto = new PlatformDto();
        platformDto.setId(1L);
        platformDto.setName("test-name-1");
        platformDto.setDescription("test-description-1");
        platformDto.setCreatedAt(LocalDateTime.now());
        platformDto.setUpdatedAt(LocalDateTime.now());
        platformDto.setVersion(1L);

        Mockito.when(platformService.findById(ArgumentMatchers.anyLong()))
                .thenReturn(platformDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/platforms/1")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyPlatformDto("", resultActions, platformDto);
    }

    @Test
    void findGamesByPlatformId_withNoData_returns200AndEmptyPagedResponse() throws Exception {
        // Arrange
        Mockito.when(gameService.findGamesByPlatformId(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());

        Mockito.when(gameService.countGamesByPlatformId(ArgumentMatchers.anyLong()))
                .thenReturn(0L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/platforms/1/games")
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
    void findGamesByPlatformId_withSmallData_returns200AndValidPagedResponseWithNoPageLinks() throws Exception {
        // Arrange
        GameDto gameDto1 = new GameDto();
        gameDto1.setId(1L);
        gameDto1.setTitle("test-title-1");
        gameDto1.setDescription("test-description-1");
        gameDto1.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        gameDto1.setCreatedAt(LocalDateTime.now());
        gameDto1.setUpdatedAt(LocalDateTime.now());
        gameDto1.setVersion(1L);

        GameDto gameDto2 = new GameDto();
        gameDto2.setId(2L);
        gameDto2.setTitle("test-title-2");
        gameDto2.setDescription("test-description-2");
        gameDto2.setAgeRating(AgeRating.ADULTS_ONLY);
        gameDto2.setCreatedAt(LocalDateTime.now());
        gameDto2.setUpdatedAt(LocalDateTime.now());
        gameDto2.setVersion(2L);

        Mockito.when(gameService.findGamesByPlatformId(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(gameDto1, gameDto2));

        Mockito.when(gameService.countGamesByPlatformId(ArgumentMatchers.anyLong()))
                .thenReturn(2L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/platforms/1/games")
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
    void findGamesByPlatformId_withData_returns200AndValidPagedResponseWithPageLinks() throws Exception {
        // Arrange
        GameDto gameDto1 = new GameDto();
        gameDto1.setId(1L);
        gameDto1.setTitle("test-title-1");
        gameDto1.setDescription("test-description-1");
        gameDto1.setAgeRating(AgeRating.EVERYONE_TEN_PLUS);
        gameDto1.setCreatedAt(LocalDateTime.now());
        gameDto1.setUpdatedAt(LocalDateTime.now());
        gameDto1.setVersion(1L);

        GameDto gameDto2 = new GameDto();
        gameDto2.setId(2L);
        gameDto2.setTitle("test-title-2");
        gameDto2.setDescription("test-description-2");
        gameDto2.setAgeRating(AgeRating.ADULTS_ONLY);
        gameDto2.setCreatedAt(LocalDateTime.now());
        gameDto2.setUpdatedAt(LocalDateTime.now());
        gameDto2.setVersion(2L);

        Mockito.when(gameService.findGamesByPlatformId(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(gameDto1, gameDto2));

        Mockito.when(gameService.countGamesByPlatformId(ArgumentMatchers.anyLong()))
                .thenReturn(100L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/platforms/1/games?page=2")
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
    void findAll_withNoData_returns200AndEmptyPagedResponse() throws Exception {
        // Arrange
        Mockito.when(platformService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());

        Mockito.when(platformService.count(ArgumentMatchers.any()))
                .thenReturn(0L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/platforms")
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
        PlatformDto platformDto1 = new PlatformDto();
        platformDto1.setId(1L);
        platformDto1.setName("test-name-1");
        platformDto1.setDescription("test-description-1");
        platformDto1.setCreatedAt(LocalDateTime.now());
        platformDto1.setUpdatedAt(LocalDateTime.now());
        platformDto1.setVersion(1L);

        PlatformDto platformDto2 = new PlatformDto();
        platformDto2.setId(2L);
        platformDto2.setName("test-name-2");
        platformDto2.setDescription("test-description-2");
        platformDto2.setCreatedAt(LocalDateTime.now());
        platformDto2.setUpdatedAt(LocalDateTime.now());
        platformDto2.setVersion(2L);

        Mockito.when(platformService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(platformDto1, platformDto2));

        Mockito.when(platformService.count(ArgumentMatchers.any()))
                .thenReturn(2L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/platforms")
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

        ResponseVerifier.verifyPlatformDto("._embedded.data[0]", resultActions, platformDto1);
        ResponseVerifier.verifyPlatformDto("._embedded.data[1]", resultActions, platformDto2);
    }

    @Test
    void findAll_withData_returns200AndValidPagedResponseWithPageLinks() throws Exception {
        // Arrange
        PlatformDto platformDto1 = new PlatformDto();
        platformDto1.setId(1L);
        platformDto1.setName("test-name-1");
        platformDto1.setDescription("test-description-1");
        platformDto1.setCreatedAt(LocalDateTime.now());
        platformDto1.setUpdatedAt(LocalDateTime.now());
        platformDto1.setVersion(1L);

        PlatformDto platformDto2 = new PlatformDto();
        platformDto2.setId(2L);
        platformDto2.setName("test-name-2");
        platformDto2.setDescription("test-description-2");
        platformDto2.setCreatedAt(LocalDateTime.now());
        platformDto2.setUpdatedAt(LocalDateTime.now());
        platformDto2.setVersion(2L);

        Mockito.when(platformService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(platformDto1, platformDto2));

        Mockito.when(platformService.count(ArgumentMatchers.any()))
                .thenReturn(100L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/platforms?page=2")
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

        ResponseVerifier.verifyPlatformDto("._embedded.data[0]", resultActions, platformDto1);
        ResponseVerifier.verifyPlatformDto("._embedded.data[1]", resultActions, platformDto2);
    }

    @Test
    void update_withInvalidPlatformDto_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/platforms")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json")
                .content(objectMapper.writeValueAsString(new PlatformDto())));

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
    void update_withValidPlatformDto_returns200AndValidResponse() throws Exception {
        // Arrange
        PlatformDto platformDto = new PlatformDto();
        platformDto.setId(1L);
        platformDto.setName("test-name-1");
        platformDto.setDescription("test-description-1");
        platformDto.setCreatedAt(LocalDateTime.now());
        platformDto.setUpdatedAt(LocalDateTime.now());
        platformDto.setVersion(1L);

        Mockito.when(platformService.update(ArgumentMatchers.any()))
                .thenReturn(platformDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/platforms")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json")
                .content(objectMapper.writeValueAsString(platformDto)));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyPlatformDto("", resultActions, platformDto);
    }

    @Test
    void patch_withValidPatch_returns200AndValidResponse() throws Exception {
        // Arrange
        PlatformDto platformDto = new PlatformDto();
        platformDto.setId(1L);
        platformDto.setName("test-name-1");
        platformDto.setDescription("test-description-1");
        platformDto.setCreatedAt(LocalDateTime.now());
        platformDto.setUpdatedAt(LocalDateTime.now());
        platformDto.setVersion(1L);

        Mockito.when(platformService.patch(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(platformDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.patch("/platforms/1")
                .contentType("application/merge-patch+json")
                .accept("application/vnd.traklibrary.v1.hal+json")
                .content("{ \"name\": \"test-name-2\" }"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyPlatformDto("", resultActions, platformDto);
    }

    @Test
    void deleteById_withValidId_returns204() throws Exception {
        // Arrange
        Mockito.doNothing()
                .when(platformService).deleteById(ArgumentMatchers.anyLong());

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/platforms/1")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
