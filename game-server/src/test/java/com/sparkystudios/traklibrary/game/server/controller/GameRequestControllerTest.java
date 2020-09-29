package com.sparkystudios.traklibrary.game.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparkystudios.traklibrary.game.server.assembler.GameRequestRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.server.configuration.TrakHalJsonMediaTypeConfiguration;
import com.sparkystudios.traklibrary.game.server.converter.JsonMergePatchHttpMessageConverter;
import com.sparkystudios.traklibrary.game.server.exception.GlobalExceptionHandler;
import com.sparkystudios.traklibrary.game.server.utils.ResponseVerifier;
import com.sparkystudios.traklibrary.game.service.GameRequestService;
import com.sparkystudios.traklibrary.game.service.dto.GameRequestDto;
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

@Import({GameRequestController.class, TrakHalJsonMediaTypeConfiguration.class, GlobalExceptionHandler.class, JsonMergePatchHttpMessageConverter.class})
@WebMvcTest(controllers = GameRequestController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class, useDefaultFilters = false)
@AutoConfigureMockMvc(addFilters = false)
class GameRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GameRequestService gameRequestService;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public GameRequestRepresentationModelAssembler gameRequestRepresentationModelAssembler() {
            return new GameRequestRepresentationModelAssembler();
        }
    }

    @Test
    void save_withInvalidGameRequestDto_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json")
                .content(objectMapper.writeValueAsString(new GameRequestDto())));

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
    void save_withValidDeveloperDto_returns201AndValidResponse() throws Exception {
        // Arrange
        GameRequestDto gameRequestDto = new GameRequestDto();
        gameRequestDto.setId(1L);
        gameRequestDto.setTitle("test-title");
        gameRequestDto.setNotes("test-notes");
        gameRequestDto.setCompleted(true);
        gameRequestDto.setCompletedDate(LocalDateTime.now());
        gameRequestDto.setUserId(1L);
        gameRequestDto.setCreatedAt(LocalDateTime.now());
        gameRequestDto.setUpdatedAt(LocalDateTime.now());
        gameRequestDto.setVersion(1L);

        Mockito.when(gameRequestService.save(ArgumentMatchers.any()))
                .thenReturn(gameRequestDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json")
                .content(objectMapper.writeValueAsString(gameRequestDto)));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isCreated());

        ResponseVerifier.verifyGameRequestDto("", resultActions, gameRequestDto);
    }

    @Test
    void findById_withValidId_return200AndValidResponse() throws Exception {
        // Arrange
        GameRequestDto gameRequestDto = new GameRequestDto();
        gameRequestDto.setId(1L);
        gameRequestDto.setTitle("test-title");
        gameRequestDto.setNotes("test-notes");
        gameRequestDto.setCompleted(true);
        gameRequestDto.setCompletedDate(LocalDateTime.now());
        gameRequestDto.setUserId(1L);
        gameRequestDto.setCreatedAt(LocalDateTime.now());
        gameRequestDto.setUpdatedAt(LocalDateTime.now());
        gameRequestDto.setVersion(1L);

        Mockito.when(gameRequestService.findById(ArgumentMatchers.anyLong()))
                .thenReturn(gameRequestDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/requests/1")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyGameRequestDto("", resultActions, gameRequestDto);
    }

    @Test
    void findAll_withNoData_returns200AndEmptyPagedResponse() throws Exception {
        // Arrange
        Mockito.when(gameRequestService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());

        Mockito.when(gameRequestService.count(ArgumentMatchers.any()))
                .thenReturn(0L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/requests")
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
        GameRequestDto gameRequestDto1 = new GameRequestDto();
        gameRequestDto1.setId(1L);
        gameRequestDto1.setTitle("test-title-1");
        gameRequestDto1.setNotes("test-notes-1");
        gameRequestDto1.setCompleted(true);
        gameRequestDto1.setCompletedDate(LocalDateTime.now());
        gameRequestDto1.setUserId(1L);
        gameRequestDto1.setCreatedAt(LocalDateTime.now());
        gameRequestDto1.setUpdatedAt(LocalDateTime.now());
        gameRequestDto1.setVersion(1L);

        GameRequestDto gameRequestDto2 = new GameRequestDto();
        gameRequestDto2.setId(2L);
        gameRequestDto2.setTitle("test-title-2");
        gameRequestDto2.setNotes("test-notes-2");
        gameRequestDto2.setCompleted(true);
        gameRequestDto2.setCompletedDate(LocalDateTime.now());
        gameRequestDto2.setUserId(2L);
        gameRequestDto2.setCreatedAt(LocalDateTime.now());
        gameRequestDto2.setUpdatedAt(LocalDateTime.now());
        gameRequestDto2.setVersion(2L);

        Mockito.when(gameRequestService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(gameRequestDto1, gameRequestDto2));

        Mockito.when(gameRequestService.count(ArgumentMatchers.any()))
                .thenReturn(2L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/requests")
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

        ResponseVerifier.verifyGameRequestDto("._embedded.data[0]", resultActions, gameRequestDto1);
        ResponseVerifier.verifyGameRequestDto("._embedded.data[1]", resultActions, gameRequestDto2);
    }

    @Test
    void findAll_withData_returns200AndValidPagedResponseWithPageLinks() throws Exception {
        // Arrange
        GameRequestDto gameRequestDto1 = new GameRequestDto();
        gameRequestDto1.setId(1L);
        gameRequestDto1.setTitle("test-title-1");
        gameRequestDto1.setNotes("test-notes-1");
        gameRequestDto1.setCompleted(true);
        gameRequestDto1.setCompletedDate(LocalDateTime.now());
        gameRequestDto1.setUserId(1L);
        gameRequestDto1.setCreatedAt(LocalDateTime.now());
        gameRequestDto1.setUpdatedAt(LocalDateTime.now());
        gameRequestDto1.setVersion(1L);

        GameRequestDto gameRequestDto2 = new GameRequestDto();
        gameRequestDto2.setId(2L);
        gameRequestDto2.setTitle("test-title-2");
        gameRequestDto2.setNotes("test-notes-2");
        gameRequestDto2.setCompleted(true);
        gameRequestDto2.setCompletedDate(LocalDateTime.now());
        gameRequestDto2.setUserId(2L);
        gameRequestDto2.setCreatedAt(LocalDateTime.now());
        gameRequestDto2.setUpdatedAt(LocalDateTime.now());
        gameRequestDto2.setVersion(2L);

        Mockito.when(gameRequestService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(gameRequestDto1, gameRequestDto2));

        Mockito.when(gameRequestService.count(ArgumentMatchers.any()))
                .thenReturn(100L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/requests?page=2")
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

        ResponseVerifier.verifyGameRequestDto("._embedded.data[0]", resultActions, gameRequestDto1);
        ResponseVerifier.verifyGameRequestDto("._embedded.data[1]", resultActions, gameRequestDto2);
    }

    @Test
    void update_withInvalidGameRequestDto_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json")
                .content(objectMapper.writeValueAsString(new GameRequestDto())));

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
    void update_withValidGameRequestDto_returns200AndValidResponse() throws Exception {
        // Arrange
        GameRequestDto gameRequestDto = new GameRequestDto();
        gameRequestDto.setId(1L);
        gameRequestDto.setTitle("test-title");
        gameRequestDto.setNotes("test-notes");
        gameRequestDto.setCompleted(true);
        gameRequestDto.setCompletedDate(LocalDateTime.now());
        gameRequestDto.setUserId(1L);
        gameRequestDto.setCreatedAt(LocalDateTime.now());
        gameRequestDto.setUpdatedAt(LocalDateTime.now());
        gameRequestDto.setVersion(1L);

        Mockito.when(gameRequestService.update(ArgumentMatchers.any()))
                .thenReturn(gameRequestDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json")
                .content(objectMapper.writeValueAsString(gameRequestDto)));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyGameRequestDto("", resultActions, gameRequestDto);
    }

    @Test
    void complete_withValidId_return204() throws Exception {
        // Arrange
        GameRequestDto gameRequestDto = new GameRequestDto();
        gameRequestDto.setId(1L);
        gameRequestDto.setTitle("test-title");
        gameRequestDto.setNotes("test-notes");
        gameRequestDto.setCompleted(true);
        gameRequestDto.setCompletedDate(LocalDateTime.now());
        gameRequestDto.setUserId(1L);
        gameRequestDto.setCreatedAt(LocalDateTime.now());
        gameRequestDto.setUpdatedAt(LocalDateTime.now());
        gameRequestDto.setVersion(1L);

        Mockito.when(gameRequestService.findById(ArgumentMatchers.anyLong()))
                .thenReturn(gameRequestDto);

        Mockito.doNothing()
                .when(gameRequestService).complete(ArgumentMatchers.any());

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/requests/1/complete")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void patch_withValidPatch_returns200AndValidResponse() throws Exception {
        // Arrange
        GameRequestDto gameRequestDto = new GameRequestDto();
        gameRequestDto.setId(1L);
        gameRequestDto.setTitle("test-title");
        gameRequestDto.setNotes("test-notes");
        gameRequestDto.setCompleted(true);
        gameRequestDto.setCompletedDate(LocalDateTime.now());
        gameRequestDto.setUserId(1L);
        gameRequestDto.setCreatedAt(LocalDateTime.now());
        gameRequestDto.setUpdatedAt(LocalDateTime.now());
        gameRequestDto.setVersion(1L);

        Mockito.when(gameRequestService.patch(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(gameRequestDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.patch("/requests/1")
                .contentType("application/merge-patch+json")
                .accept("application/vnd.traklibrary.v1.hal+json")
                .content("{ \"title\": \"test-title-2\" }"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyGameRequestDto("", resultActions, gameRequestDto);
    }

    @Test
    void deleteById_withValidId_returns204() throws Exception {
        // Arrange
        Mockito.doNothing()
                .when(gameRequestService).deleteById(ArgumentMatchers.anyLong());

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/requests/1")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
