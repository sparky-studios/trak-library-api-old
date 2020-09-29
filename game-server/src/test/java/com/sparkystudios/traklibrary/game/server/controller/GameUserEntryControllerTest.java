package com.sparkystudios.traklibrary.game.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparkystudios.traklibrary.game.domain.GameUserEntryStatus;
import com.sparkystudios.traklibrary.game.server.assembler.GameUserEntryRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.server.configuration.TrakHalJsonMediaTypeConfiguration;
import com.sparkystudios.traklibrary.game.server.converter.JsonMergePatchHttpMessageConverter;
import com.sparkystudios.traklibrary.game.server.exception.GlobalExceptionHandler;
import com.sparkystudios.traklibrary.game.server.utils.ResponseVerifier;
import com.sparkystudios.traklibrary.game.service.GameUserEntryService;
import com.sparkystudios.traklibrary.game.service.dto.GameUserEntryDto;
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

@Import({GameUserEntryController.class, TrakHalJsonMediaTypeConfiguration.class, GlobalExceptionHandler.class, JsonMergePatchHttpMessageConverter.class})
@WebMvcTest(controllers = GameUserEntryController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class, useDefaultFilters = false)
@AutoConfigureMockMvc(addFilters = false)
public class GameUserEntryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GameUserEntryService gameUserEntryService;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public GameUserEntryRepresentationModelAssembler gameUserEntryRepresentationModelAssembler() {
            return new GameUserEntryRepresentationModelAssembler();
        }
    }

    @Test
    void save_withInvalidGameUserEntryDto_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/entries")
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
    void save_withValidGameDto_returns201AndValidResponse() throws Exception {
        // Arrange
        GameUserEntryDto gameUserEntryDto = new GameUserEntryDto();
        gameUserEntryDto.setId(1L);
        gameUserEntryDto.setGameId(1L);
        gameUserEntryDto.setGameTitle("game-title");
        gameUserEntryDto.setPlatformId(1L);
        gameUserEntryDto.setPlatformName("platform-name");
        gameUserEntryDto.setUserId(1L);
        gameUserEntryDto.setStatus(GameUserEntryStatus.COMPLETED);
        gameUserEntryDto.setRating((short)4);
        gameUserEntryDto.setCreatedAt(LocalDateTime.now());
        gameUserEntryDto.setUpdatedAt(LocalDateTime.now());
        gameUserEntryDto.setVersion(1L);

        Mockito.when(gameUserEntryService.save(ArgumentMatchers.any()))
                .thenReturn(gameUserEntryDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/entries")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json")
                .content(objectMapper.writeValueAsString(gameUserEntryDto)));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isCreated());

        ResponseVerifier.verifyGameUserEntryDto("", resultActions, gameUserEntryDto);
    }

    @Test
    void findById_withValidId_return200AndValidResponse() throws Exception {
        // Arrange
        GameUserEntryDto gameUserEntryDto = new GameUserEntryDto();
        gameUserEntryDto.setId(1L);
        gameUserEntryDto.setGameId(1L);
        gameUserEntryDto.setGameTitle("game-title");
        gameUserEntryDto.setPlatformId(1L);
        gameUserEntryDto.setPlatformName("platform-name");
        gameUserEntryDto.setUserId(1L);
        gameUserEntryDto.setStatus(GameUserEntryStatus.COMPLETED);
        gameUserEntryDto.setRating((short)4);
        gameUserEntryDto.setCreatedAt(LocalDateTime.now());
        gameUserEntryDto.setUpdatedAt(LocalDateTime.now());
        gameUserEntryDto.setVersion(1L);

        Mockito.when(gameUserEntryService.findById(ArgumentMatchers.anyLong()))
                .thenReturn(gameUserEntryDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/entries/1")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyGameUserEntryDto("", resultActions, gameUserEntryDto);
    }

    @Test
    void findAll_withNoData_returns200AndEmptyPagedResponse() throws Exception {
        // Arrange
        Mockito.when(gameUserEntryService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());

        Mockito.when(gameUserEntryService.count(ArgumentMatchers.any()))
                .thenReturn(0L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/entries")
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
        GameUserEntryDto gameUserEntryDto1 = new GameUserEntryDto();
        gameUserEntryDto1.setId(1L);
        gameUserEntryDto1.setGameId(1L);
        gameUserEntryDto1.setGameTitle("game-title-1");
        gameUserEntryDto1.setPlatformId(1L);
        gameUserEntryDto1.setPlatformName("platform-name-1");
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
        gameUserEntryDto2.setPlatformId(2L);
        gameUserEntryDto2.setPlatformName("platform-name-2");
        gameUserEntryDto2.setUserId(2L);
        gameUserEntryDto2.setStatus(GameUserEntryStatus.IN_PROGRESS);
        gameUserEntryDto2.setRating((short)2);
        gameUserEntryDto2.setCreatedAt(LocalDateTime.now());
        gameUserEntryDto2.setUpdatedAt(LocalDateTime.now());
        gameUserEntryDto2.setVersion(2L);

        Mockito.when(gameUserEntryService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(gameUserEntryDto1, gameUserEntryDto2));

        Mockito.when(gameUserEntryService.count(ArgumentMatchers.any()))
                .thenReturn(2L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/entries")
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
    void findAll_withData_returns200AndValidPagedResponseWithPageLinks() throws Exception {
        // Arrange
        GameUserEntryDto gameUserEntryDto1 = new GameUserEntryDto();
        gameUserEntryDto1.setId(1L);
        gameUserEntryDto1.setGameId(1L);
        gameUserEntryDto1.setGameTitle("game-title-1");
        gameUserEntryDto1.setPlatformId(1L);
        gameUserEntryDto1.setPlatformName("platform-name-1");
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
        gameUserEntryDto2.setPlatformId(2L);
        gameUserEntryDto2.setPlatformName("platform-name-2");
        gameUserEntryDto2.setUserId(2L);
        gameUserEntryDto2.setStatus(GameUserEntryStatus.IN_PROGRESS);
        gameUserEntryDto2.setRating((short)2);
        gameUserEntryDto2.setCreatedAt(LocalDateTime.now());
        gameUserEntryDto2.setUpdatedAt(LocalDateTime.now());
        gameUserEntryDto2.setVersion(2L);

        Mockito.when(gameUserEntryService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(gameUserEntryDto1, gameUserEntryDto2));

        Mockito.when(gameUserEntryService.count(ArgumentMatchers.any()))
                .thenReturn(100L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/entries?page=2")
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
    void update_withInvalidGameDto_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/entries")
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
    void update_withValidGameUserEntryDto_returns200AndValidResponse() throws Exception {
        // Arrange
        GameUserEntryDto gameUserEntryDto = new GameUserEntryDto();
        gameUserEntryDto.setId(1L);
        gameUserEntryDto.setGameId(1L);
        gameUserEntryDto.setGameTitle("game-title");
        gameUserEntryDto.setPlatformId(1L);
        gameUserEntryDto.setPlatformName("platform-name");
        gameUserEntryDto.setUserId(1L);
        gameUserEntryDto.setStatus(GameUserEntryStatus.COMPLETED);
        gameUserEntryDto.setRating((short)4);
        gameUserEntryDto.setCreatedAt(LocalDateTime.now());
        gameUserEntryDto.setUpdatedAt(LocalDateTime.now());
        gameUserEntryDto.setVersion(1L);

        Mockito.when(gameUserEntryService.update(ArgumentMatchers.any()))
                .thenReturn(gameUserEntryDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/entries")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json")
                .content(objectMapper.writeValueAsString(gameUserEntryDto)));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyGameUserEntryDto("", resultActions, gameUserEntryDto);
    }

    @Test
    void patch_withValidPatch_returns200AndValidResponse() throws Exception {
        // Arrange
        GameUserEntryDto gameUserEntryDto = new GameUserEntryDto();
        gameUserEntryDto.setId(1L);
        gameUserEntryDto.setGameId(1L);
        gameUserEntryDto.setGameTitle("game-title");
        gameUserEntryDto.setPlatformId(1L);
        gameUserEntryDto.setPlatformName("platform-name");
        gameUserEntryDto.setUserId(1L);
        gameUserEntryDto.setStatus(GameUserEntryStatus.COMPLETED);
        gameUserEntryDto.setRating((short)4);
        gameUserEntryDto.setCreatedAt(LocalDateTime.now());
        gameUserEntryDto.setUpdatedAt(LocalDateTime.now());
        gameUserEntryDto.setVersion(1L);

        Mockito.when(gameUserEntryService.patch(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(gameUserEntryDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.patch("/entries/1")
                .contentType("application/merge-patch+json")
                .accept("application/vnd.traklibrary.v1.hal+json")
                .content("{ \"gameTitle\": \"test-title-2\" }"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyGameUserEntryDto("", resultActions, gameUserEntryDto);
    }

    @Test
    void deleteById_withValidId_returns204() throws Exception {
        // Arrange
        Mockito.doNothing()
                .when(gameUserEntryService).deleteById(ArgumentMatchers.anyLong());

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/entries/1")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
