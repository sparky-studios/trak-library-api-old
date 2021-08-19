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
import com.sparkystudios.traklibrary.game.service.dto.request.GameUserEntryRequest;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

@Import({GameUserEntryController.class, TrakHalJsonMediaTypeConfiguration.class, GlobalExceptionHandler.class, JsonMergePatchHttpMessageConverter.class})
@WebMvcTest(controllers = GameUserEntryController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class, useDefaultFilters = false)
@AutoConfigureMockMvc(addFilters = false)
class GameUserEntryControllerTest {

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
    void save_withInvalidGameUserEntryRequest_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/entries")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.sparkystudios.traklibrary-hal+json;version=1.0")
                .content(objectMapper.writeValueAsString(new GameUserEntryRequest())));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").exists());
    }

    @Test
    void save_withValidGameUserEntryRequest_returns201AndValidResponse() throws Exception {
        // Arrange
        GameUserEntryRequest gameUserEntryRequest = new GameUserEntryRequest();
        gameUserEntryRequest.setUserId(1L);
        gameUserEntryRequest.setGameId(1L);
        gameUserEntryRequest.setRating((short)1);
        gameUserEntryRequest.setStatus(GameUserEntryStatus.COMPLETED);
        gameUserEntryRequest.setPlatformIds(Collections.singletonList(1L));
        gameUserEntryRequest.setDownloadableContentIds(Collections.singletonList(1L));

        Mockito.when(gameUserEntryService.save(ArgumentMatchers.any()))
                .thenReturn(new GameUserEntryDto());

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/entries")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.sparkystudios.traklibrary-hal+json;version=1.0")
                .content(objectMapper.writeValueAsString(gameUserEntryRequest)));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isCreated());

        ResponseVerifier
                .verifyGameUserEntryDto("", resultActions);
    }

    @Test
    void findById_withValidId_return200AndValidResponse() throws Exception {
        // Arrange
        GameUserEntryDto gameUserEntryDto = new GameUserEntryDto();
        gameUserEntryDto.setId(1L);
        gameUserEntryDto.setGameId(1L);
        gameUserEntryDto.setGameTitle("game-title");
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
                .accept("application/vnd.sparkystudios.traklibrary-hal+json;version=1.0"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyGameUserEntryDto("", resultActions);
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
                .accept("application/vnd.sparkystudios.traklibrary-hal+json;version=1.0"));

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

        Mockito.when(gameUserEntryService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(gameUserEntryDto1, gameUserEntryDto2));

        Mockito.when(gameUserEntryService.count(ArgumentMatchers.any()))
                .thenReturn(2L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/entries")
                .accept("application/vnd.sparkystudios.traklibrary-hal+json;version=1.0"));

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
    void findAll_withData_returns200AndValidPagedResponseWithPageLinks() throws Exception {
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

        Mockito.when(gameUserEntryService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(gameUserEntryDto1, gameUserEntryDto2));

        Mockito.when(gameUserEntryService.count(ArgumentMatchers.any()))
                .thenReturn(100L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/entries?page=2")
                .accept("application/vnd.sparkystudios.traklibrary-hal+json;version=1.0"));

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
    void update_withInvalidGameUserEntryRequest_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/entries")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.sparkystudios.traklibrary-hal+json;version=1.0")
                .content(objectMapper.writeValueAsString(new GameUserEntryRequest())));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").exists());
    }

    @Test
    void update_withValidGameUserEntryDto_returns200AndValidResponse() throws Exception {
        // Arrange
        GameUserEntryRequest gameUserEntryRequest = new GameUserEntryRequest();
        gameUserEntryRequest.setUserId(1L);
        gameUserEntryRequest.setGameId(1L);
        gameUserEntryRequest.setRating((short)1);
        gameUserEntryRequest.setStatus(GameUserEntryStatus.COMPLETED);
        gameUserEntryRequest.setPlatformIds(Collections.singletonList(1L));
        gameUserEntryRequest.setDownloadableContentIds(Collections.singletonList(1L));

        Mockito.when(gameUserEntryService.update(ArgumentMatchers.any()))
                .thenReturn(new GameUserEntryDto());

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/entries")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.sparkystudios.traklibrary-hal+json;version=1.0")
                .content(objectMapper.writeValueAsString(gameUserEntryRequest)));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier
                .verifyGameUserEntryDto("", resultActions);
    }

    @Test
    void deleteById_withValidId_returns204() throws Exception {
        // Arrange
        Mockito.doNothing()
                .when(gameUserEntryService).deleteById(ArgumentMatchers.anyLong());

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/entries/1")
                .accept("application/vnd.sparkystudios.traklibrary-hal+json;version=1.0"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
