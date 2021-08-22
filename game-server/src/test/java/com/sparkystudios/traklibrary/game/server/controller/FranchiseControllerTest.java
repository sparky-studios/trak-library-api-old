package com.sparkystudios.traklibrary.game.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparkystudios.traklibrary.game.domain.AgeRating;
import com.sparkystudios.traklibrary.game.server.assembler.FranchiseRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.server.assembler.GameRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.server.configuration.TrakHalJsonMediaTypeConfiguration;
import com.sparkystudios.traklibrary.game.server.converter.JsonMergePatchHttpMessageConverter;
import com.sparkystudios.traklibrary.game.server.exception.GlobalExceptionHandler;
import com.sparkystudios.traklibrary.game.server.utils.ResponseVerifier;
import com.sparkystudios.traklibrary.game.service.FranchiseService;
import com.sparkystudios.traklibrary.game.service.GameService;
import com.sparkystudios.traklibrary.game.service.dto.FranchiseDto;
import com.sparkystudios.traklibrary.game.service.dto.GameDto;
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

@Import({FranchiseController.class, TrakHalJsonMediaTypeConfiguration.class, GlobalExceptionHandler.class, JsonMergePatchHttpMessageConverter.class})
@WebMvcTest(controllers = FranchiseController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class, useDefaultFilters = false)
@AutoConfigureMockMvc(addFilters = false)
class FranchiseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FranchiseService franchiseService;

    @MockBean
    private GameService gameService;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public FranchiseRepresentationModelAssembler franchiseRepresentationModelAssembler() {
            return new FranchiseRepresentationModelAssembler(null);
        }

        @Bean
        public GameRepresentationModelAssembler gameRepresentationModelAssembler() {
            return new GameRepresentationModelAssembler(null);
        }
    }

    @Test
    void save_withInvalidFranchiseDto_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.sparkystudios.traklibrary-hal+json;version=1.0")
                .content(objectMapper.writeValueAsString(new FranchiseDto())));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").exists());
    }

    @Test
    void save_withValidFranchiseDto_returns201AndValidResponse() throws Exception {
        // Arrange
        FranchiseDto franchiseDto = new FranchiseDto();
        franchiseDto.setId(1L);
        franchiseDto.setTitle("test-name-1");
        franchiseDto.setDescription("test-description-1");
        franchiseDto.setSlug("test-slug");
        franchiseDto.setCreatedAt(LocalDateTime.now());
        franchiseDto.setUpdatedAt(LocalDateTime.now());
        franchiseDto.setVersion(1L);

        Mockito.when(franchiseService.save(ArgumentMatchers.any()))
                .thenReturn(franchiseDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.sparkystudios.traklibrary-hal+json;version=1.0")
                .content(objectMapper.writeValueAsString(franchiseDto)));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isCreated());

        ResponseVerifier.verifyFranchiseDto("", resultActions, franchiseDto);
    }

    @Test
    void findById_withValidId_return200AndValidResponse() throws Exception {
        // Arrange
        FranchiseDto franchiseDto = new FranchiseDto();
        franchiseDto.setId(1L);
        franchiseDto.setTitle("test-title-1");
        franchiseDto.setDescription("test-description-1");
        franchiseDto.setSlug("test-slug");
        franchiseDto.setCreatedAt(LocalDateTime.now());
        franchiseDto.setUpdatedAt(LocalDateTime.now());
        franchiseDto.setVersion(1L);

        Mockito.when(franchiseService.findById(ArgumentMatchers.anyLong()))
                .thenReturn(franchiseDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/franchises/1")
                .accept("application/vnd.sparkystudios.traklibrary-hal+json;version=1.0"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyFranchiseDto("", resultActions, franchiseDto);
    }

    @Test
    void findBySlug_withValidSlug_return200AndValidResponse() throws Exception {
        // Arrange
        FranchiseDto franchiseDto = new FranchiseDto();
        franchiseDto.setId(1L);
        franchiseDto.setTitle("test-title-1");
        franchiseDto.setDescription("test-description-1");
        franchiseDto.setSlug("test-slug");
        franchiseDto.setCreatedAt(LocalDateTime.now());
        franchiseDto.setUpdatedAt(LocalDateTime.now());
        franchiseDto.setVersion(1L);

        Mockito.when(franchiseService.findBySlug(ArgumentMatchers.anyString()))
                .thenReturn(franchiseDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/franchises/slug/test-slug")
                .accept("application/vnd.sparkystudios.traklibrary-hal+json;version=1.0"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyFranchiseDto("", resultActions, franchiseDto);
    }

    @Test
    void findGamesByFranchiseId_withNoData_returns200AndEmptyPagedResponse() throws Exception {
        // Arrange
        Mockito.when(gameService.findGamesByFranchiseId(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());

        Mockito.when(gameService.countGamesByFranchiseId(ArgumentMatchers.anyLong()))
                .thenReturn(0L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/franchises/1/games")
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
    void findGamesByFranchiseId_withSmallData_returns200AndValidPagedResponseWithNoPageLinks() throws Exception {
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

        Mockito.when(gameService.findGamesByFranchiseId(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(gameDto1, gameDto2));

        Mockito.when(gameService.countGamesByFranchiseId(ArgumentMatchers.anyLong()))
                .thenReturn(2L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/franchises/1/games")
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

        ResponseVerifier.verifyGameDto("._embedded.data[0]", resultActions, gameDto1);
        ResponseVerifier.verifyGameDto("._embedded.data[1]", resultActions, gameDto2);
    }

    @Test
    void findGamesByFranchiseId_withData_returns200AndValidPagedResponseWithPageLinks() throws Exception {
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

        Mockito.when(gameService.findGamesByFranchiseId(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(gameDto1, gameDto2));

        Mockito.when(gameService.countGamesByFranchiseId(ArgumentMatchers.anyLong()))
                .thenReturn(100L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/franchises/1/games?page=2")
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

        ResponseVerifier.verifyGameDto("._embedded.data[0]", resultActions, gameDto1);
        ResponseVerifier.verifyGameDto("._embedded.data[1]", resultActions, gameDto2);
    }

    @Test
    void findAll_withNoData_returns200AndEmptyPagedResponse() throws Exception {
        // Arrange
        Mockito.when(franchiseService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());

        Mockito.when(franchiseService.count(ArgumentMatchers.any()))
                .thenReturn(0L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/franchises")
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
        FranchiseDto franchiseDto1 = new FranchiseDto();
        franchiseDto1.setId(1L);
        franchiseDto1.setTitle("test-title-1");
        franchiseDto1.setDescription("test-description-1");
        franchiseDto1.setSlug("test-slug-1");
        franchiseDto1.setCreatedAt(LocalDateTime.now());
        franchiseDto1.setUpdatedAt(LocalDateTime.now());
        franchiseDto1.setVersion(1L);

        FranchiseDto franchiseDto2 = new FranchiseDto();
        franchiseDto2.setId(2L);
        franchiseDto2.setTitle("test-title-2");
        franchiseDto2.setDescription("test-description-2");
        franchiseDto2.setSlug("test-slug-2");
        franchiseDto2.setCreatedAt(LocalDateTime.now());
        franchiseDto2.setUpdatedAt(LocalDateTime.now());
        franchiseDto2.setVersion(2L);

        Mockito.when(franchiseService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(franchiseDto1, franchiseDto2));

        Mockito.when(franchiseService.count(ArgumentMatchers.any()))
                .thenReturn(2L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/franchises")
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

        ResponseVerifier.verifyFranchiseDto("._embedded.data[0]", resultActions, franchiseDto1);
        ResponseVerifier.verifyFranchiseDto("._embedded.data[1]", resultActions, franchiseDto2);
    }

    @Test
    void findAll_withData_returns200AndValidPagedResponseWithPageLinks() throws Exception {
        // Arrange
        FranchiseDto franchiseDto1 = new FranchiseDto();
        franchiseDto1.setId(1L);
        franchiseDto1.setTitle("test-title-1");
        franchiseDto1.setDescription("test-description-1");
        franchiseDto1.setSlug("test-slug-1");
        franchiseDto1.setCreatedAt(LocalDateTime.now());
        franchiseDto1.setUpdatedAt(LocalDateTime.now());
        franchiseDto1.setVersion(1L);

        FranchiseDto franchiseDto2 = new FranchiseDto();
        franchiseDto2.setId(2L);
        franchiseDto2.setTitle("test-title-2");
        franchiseDto2.setDescription("test-description-2");
        franchiseDto2.setSlug("test-slug-2");
        franchiseDto2.setCreatedAt(LocalDateTime.now());
        franchiseDto2.setUpdatedAt(LocalDateTime.now());
        franchiseDto2.setVersion(2L);

        Mockito.when(franchiseService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(franchiseDto1, franchiseDto2));

        Mockito.when(franchiseService.count(ArgumentMatchers.any()))
                .thenReturn(100L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/franchises?page=2")
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

        ResponseVerifier.verifyFranchiseDto("._embedded.data[0]", resultActions, franchiseDto1);
        ResponseVerifier.verifyFranchiseDto("._embedded.data[1]", resultActions, franchiseDto2);
    }

    @Test
    void update_withInvalidFranchiseDto_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.sparkystudios.traklibrary-hal+json;version=1.0")
                .content(objectMapper.writeValueAsString(new FranchiseDto())));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").exists());
    }

    @Test
    void update_withValidFranchiseDto_returns200AndValidResponse() throws Exception {
        // Arrange
        FranchiseDto franchiseDto = new FranchiseDto();
        franchiseDto.setId(1L);
        franchiseDto.setTitle("test-title-1");
        franchiseDto.setDescription("test-description-1");
        franchiseDto.setSlug("test-slug");
        franchiseDto.setCreatedAt(LocalDateTime.now());
        franchiseDto.setUpdatedAt(LocalDateTime.now());
        franchiseDto.setVersion(1L);

        Mockito.when(franchiseService.update(ArgumentMatchers.any()))
                .thenReturn(franchiseDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.sparkystudios.traklibrary-hal+json;version=1.0")
                .content(objectMapper.writeValueAsString(franchiseDto)));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyFranchiseDto("", resultActions, franchiseDto);
    }

    @Test
    void patch_withValidPatch_returns200AndValidResponse() throws Exception {
        // Arrange
        FranchiseDto franchiseDto = new FranchiseDto();
        franchiseDto.setId(1L);
        franchiseDto.setTitle("test-title-1");
        franchiseDto.setDescription("test-description-1");
        franchiseDto.setSlug("test-slug");
        franchiseDto.setCreatedAt(LocalDateTime.now());
        franchiseDto.setUpdatedAt(LocalDateTime.now());
        franchiseDto.setVersion(1L);

        Mockito.when(franchiseService.patch(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(franchiseDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.patch("/franchises/1")
                .contentType("application/merge-patch+json")
                .accept("application/vnd.sparkystudios.traklibrary-hal+json;version=1.0")
                .content("{ \"name\": \"test-name-2\" }"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyFranchiseDto("", resultActions, franchiseDto);
    }

    @Test
    void deleteById_withValidId_returns204() throws Exception {
        // Arrange
        Mockito.doNothing()
                .when(franchiseService).deleteById(ArgumentMatchers.anyLong());

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/franchises/1")
                .accept("application/vnd.sparkystudios.traklibrary-hal+json;version=1.0"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
