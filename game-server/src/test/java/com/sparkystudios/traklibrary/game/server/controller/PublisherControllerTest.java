package com.sparkystudios.traklibrary.game.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparkystudios.traklibrary.game.server.assembler.GameRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.server.assembler.PublisherRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.server.configuration.TrakHalJsonMediaTypeConfiguration;
import com.sparkystudios.traklibrary.game.server.converter.JsonMergePatchHttpMessageConverter;
import com.sparkystudios.traklibrary.game.server.exception.GlobalExceptionHandler;
import com.sparkystudios.traklibrary.game.server.utils.ResponseVerifier;
import com.sparkystudios.traklibrary.game.service.CompanyImageService;
import com.sparkystudios.traklibrary.game.service.GameService;
import com.sparkystudios.traklibrary.game.service.PublisherService;
import com.sparkystudios.traklibrary.game.service.dto.GameDto;
import com.sparkystudios.traklibrary.game.service.dto.ImageDataDto;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

@Import({PublisherController.class, TrakHalJsonMediaTypeConfiguration.class, GlobalExceptionHandler.class, JsonMergePatchHttpMessageConverter.class})
@WebMvcTest(controllers = PublisherController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class, useDefaultFilters = false)
@AutoConfigureMockMvc(addFilters = false)
class PublisherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PublisherService publisherService;

    @MockBean
    private CompanyImageService companyImageService;

    @MockBean
    private GameService gameService;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public PublisherRepresentationModelAssembler publisherRepresentationModelAssembler() {
            return new PublisherRepresentationModelAssembler(null);
        }

        @Bean
        public GameRepresentationModelAssembler gameRepresentationModelAssembler() {
            return new GameRepresentationModelAssembler(null);
        }
    }

    @Test
    void save_withInvalidPublisherDto_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/publishers")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.sparkystudios.traklibrary-hal+json;version=1.0")
                .content(objectMapper.writeValueAsString(new PublisherDto())));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").exists());
    }

    @Test
    void save_withValidPublisherDto_returns201AndValidResponse() throws Exception {
        // Arrange
        PublisherDto publisherDto = new PublisherDto();
        publisherDto.setId(5L);
        publisherDto.setName("test-name");
        publisherDto.setDescription("test-description");
        publisherDto.setSlug("test-slug");
        publisherDto.setFoundedDate(LocalDate.now());
        publisherDto.setCreatedAt(LocalDateTime.now());
        publisherDto.setUpdatedAt(LocalDateTime.now());
        publisherDto.setVersion(1L);

        Mockito.when(publisherService.save(ArgumentMatchers.any()))
                .thenReturn(publisherDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/publishers")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.sparkystudios.traklibrary-hal+json;version=1.0")
                .content(objectMapper.writeValueAsString(publisherDto)));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isCreated());

        ResponseVerifier.verifyPublisherDto("", resultActions, publisherDto);
    }

    @Test
    void findById_withValidId_return200AndValidResponse() throws Exception {
        // Arrange
        PublisherDto publisherDto = new PublisherDto();
        publisherDto.setId(5L);
        publisherDto.setName("test-name");
        publisherDto.setDescription("test-description");
        publisherDto.setSlug("test-slug");
        publisherDto.setFoundedDate(LocalDate.now());
        publisherDto.setCreatedAt(LocalDateTime.now());
        publisherDto.setUpdatedAt(LocalDateTime.now());
        publisherDto.setVersion(1L);

        Mockito.when(publisherService.findById(ArgumentMatchers.anyLong()))
            .thenReturn(publisherDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/publishers/1")
                .accept("application/vnd.sparkystudios.traklibrary-hal+json;version=1.0"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyPublisherDto("", resultActions, publisherDto);
    }

    @Test
    void findBySlug_withValidSlug_return200AndValidResponse() throws Exception {
        // Arrange
        PublisherDto publisherDto = new PublisherDto();
        publisherDto.setId(5L);
        publisherDto.setName("test-name");
        publisherDto.setDescription("test-description");
        publisherDto.setSlug("test-slug");
        publisherDto.setFoundedDate(LocalDate.now());
        publisherDto.setCreatedAt(LocalDateTime.now());
        publisherDto.setUpdatedAt(LocalDateTime.now());
        publisherDto.setVersion(1L);

        Mockito.when(publisherService.findBySlug(ArgumentMatchers.anyString()))
                .thenReturn(publisherDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/publishers/slug/test-slug")
                .accept("application/vnd.sparkystudios.traklibrary-hal+json;version=1.0"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyPublisherDto("", resultActions, publisherDto);
    }

    @Test
    void findCompanyImageByCompanyId_withValidId_returns200() throws Exception {
        // Arrange
        ImageDataDto imageDataDto = new ImageDataDto();
        imageDataDto.setContent(new byte[] { 'a', 'b' });
        imageDataDto.setFilename("filename.png");

        Mockito.when(companyImageService.download(ArgumentMatchers.anyLong()))
                .thenReturn(imageDataDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/publishers/1/image")
                .accept(MediaType.APPLICATION_OCTET_STREAM_VALUE));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void saveCompanyImageForCompanyId_withInvalidFileData_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/publishers/1/image")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept("application/vnd.sparkystudios.traklibrary-hal+json;version=1.0"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").exists());
    }

    @Test
    void saveCompanyImageForCompanyId_withValidFileData_returns204() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "some xml".getBytes());

        Mockito.doNothing()
                .when(companyImageService).upload(ArgumentMatchers.anyLong(), ArgumentMatchers.any());

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/publishers/1/image")
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept("application/vnd.sparkystudios.traklibrary-hal+json;version=1.0"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void findGamesByPublisherId_withNoData_returns200AndEmptyPagedResponse() throws Exception {
        // Arrange
        Mockito.when(gameService.findGamesByPublisherId(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());

        Mockito.when(gameService.countGamesByPublisherId(ArgumentMatchers.anyLong()))
                .thenReturn(0L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/publishers/1/games")
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
    void findGamesByPublisherId_withSmallData_returns200AndValidPagedResponseWithNoPageLinks() throws Exception {
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

        Mockito.when(gameService.findGamesByPublisherId(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(gameDto1, gameDto2));

        Mockito.when(gameService.countGamesByPublisherId(ArgumentMatchers.anyLong()))
            .thenReturn(2L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/publishers/1/games")
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
    void findGamesByPublisherId_withData_returns200AndValidPagedResponseWithPageLinks() throws Exception {
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

        Mockito.when(gameService.findGamesByPublisherId(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(gameDto1, gameDto2));

        Mockito.when(gameService.countGamesByPublisherId(ArgumentMatchers.anyLong()))
                .thenReturn(100L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/publishers/1/games?page=2")
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
        Mockito.when(publisherService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());

        Mockito.when(publisherService.count(ArgumentMatchers.any()))
                .thenReturn(0L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/publishers")
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
        PublisherDto publisherDto1 = new PublisherDto();
        publisherDto1.setId(1L);
        publisherDto1.setName("test-publisher-1");
        publisherDto1.setDescription("test-description-1");
        publisherDto1.setFoundedDate(LocalDate.now());
        publisherDto1.setSlug("test-slug-1");
        publisherDto1.setCreatedAt(LocalDateTime.now());
        publisherDto1.setUpdatedAt(LocalDateTime.now());
        publisherDto1.setVersion(1L);

        PublisherDto publisherDto2 = new PublisherDto();
        publisherDto2.setId(2L);
        publisherDto2.setName("test-publisher-2");
        publisherDto2.setDescription("test-description-2");
        publisherDto2.setFoundedDate(LocalDate.now());
        publisherDto2.setSlug("test-slug-2");
        publisherDto2.setCreatedAt(LocalDateTime.now());
        publisherDto2.setUpdatedAt(LocalDateTime.now());
        publisherDto2.setVersion(2L);

        Mockito.when(publisherService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(publisherDto1, publisherDto2));

        Mockito.when(publisherService.count(ArgumentMatchers.any()))
                .thenReturn(2L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/publishers")
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

        ResponseVerifier.verifyPublisherDto("._embedded.data[0]", resultActions, publisherDto1);
        ResponseVerifier.verifyPublisherDto("._embedded.data[1]", resultActions, publisherDto2);
    }

    @Test
    void findAll_withData_returns200AndValidPagedResponseWithPageLinks() throws Exception {
        // Arrange
        PublisherDto publisherDto1 = new PublisherDto();
        publisherDto1.setId(1L);
        publisherDto1.setName("test-publisher-1");
        publisherDto1.setDescription("test-description-1");
        publisherDto1.setFoundedDate(LocalDate.now());
        publisherDto1.setSlug("test-slug-1");
        publisherDto1.setCreatedAt(LocalDateTime.now());
        publisherDto1.setUpdatedAt(LocalDateTime.now());
        publisherDto1.setVersion(1L);

        PublisherDto publisherDto2 = new PublisherDto();
        publisherDto2.setId(2L);
        publisherDto2.setName("test-publisher-2");
        publisherDto2.setDescription("test-description-2");
        publisherDto2.setFoundedDate(LocalDate.now());
        publisherDto2.setSlug("test-slug-2");
        publisherDto2.setCreatedAt(LocalDateTime.now());
        publisherDto2.setUpdatedAt(LocalDateTime.now());
        publisherDto2.setVersion(2L);

        Mockito.when(publisherService.findAll(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Arrays.asList(publisherDto1, publisherDto2));

        Mockito.when(publisherService.count(ArgumentMatchers.any()))
                .thenReturn(100L);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/publishers?page=2")
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

        ResponseVerifier.verifyPublisherDto("._embedded.data[0]", resultActions, publisherDto1);
        ResponseVerifier.verifyPublisherDto("._embedded.data[1]", resultActions, publisherDto2);
    }

    @Test
    void update_withInvalidPublisherDto_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/publishers")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.sparkystudios.traklibrary-hal+json;version=1.0")
                .content(objectMapper.writeValueAsString(new PublisherDto())));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").exists());
    }

    @Test
    void update_withValidPublisherDto_returns200AndValidResponse() throws Exception {
        // Arrange
        PublisherDto publisherDto = new PublisherDto();
        publisherDto.setId(5L);
        publisherDto.setName("test-name");
        publisherDto.setDescription("test-description");
        publisherDto.setSlug("test-slug");
        publisherDto.setFoundedDate(LocalDate.now());
        publisherDto.setCreatedAt(LocalDateTime.now());
        publisherDto.setUpdatedAt(LocalDateTime.now());
        publisherDto.setVersion(1L);

        Mockito.when(publisherService.update(ArgumentMatchers.any()))
                .thenReturn(publisherDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/publishers")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.sparkystudios.traklibrary-hal+json;version=1.0")
                .content(objectMapper.writeValueAsString(publisherDto)));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyPublisherDto("", resultActions, publisherDto);
    }

    @Test
    void patch_withValidPatch_returns200AndValidResponse() throws Exception {
        // Arrange
        PublisherDto publisherDto = new PublisherDto();
        publisherDto.setId(5L);
        publisherDto.setName("test-name");
        publisherDto.setDescription("test-description");
        publisherDto.setSlug("test-slug");
        publisherDto.setFoundedDate(LocalDate.now());
        publisherDto.setCreatedAt(LocalDateTime.now());
        publisherDto.setUpdatedAt(LocalDateTime.now());
        publisherDto.setVersion(1L);

        Mockito.when(publisherService.patch(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(publisherDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.patch("/publishers/1")
                .contentType("application/merge-patch+json")
                .accept("application/vnd.sparkystudios.traklibrary-hal+json;version=1.0")
                .content("{ \"name\": \"test-name\" }"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyPublisherDto("", resultActions, publisherDto);
    }

    @Test
    void deleteById_withValidId_returns204() throws Exception {
        // Arrange
        Mockito.doNothing()
                .when(publisherService).deleteById(ArgumentMatchers.anyLong());

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/publishers/1")
                .accept("application/vnd.sparkystudios.traklibrary-hal+json;version=1.0"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
