package com.sparkystudios.traklibrary.game.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparkystudios.traklibrary.game.server.assembler.DeveloperRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.server.assembler.DownloadableContentRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.server.assembler.GameRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.server.configuration.TrakHalJsonMediaTypeConfiguration;
import com.sparkystudios.traklibrary.game.server.converter.JsonMergePatchHttpMessageConverter;
import com.sparkystudios.traklibrary.game.server.exception.GlobalExceptionHandler;
import com.sparkystudios.traklibrary.game.server.utils.ResponseVerifier;
import com.sparkystudios.traklibrary.game.service.DeveloperService;
import com.sparkystudios.traklibrary.game.service.DownloadableContentService;
import com.sparkystudios.traklibrary.game.service.dto.DeveloperDto;
import com.sparkystudios.traklibrary.game.service.dto.DownloadableContentDto;
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

import java.time.LocalDate;
import java.time.LocalDateTime;

@Import({DownloadableContentController.class, TrakHalJsonMediaTypeConfiguration.class, GlobalExceptionHandler.class, JsonMergePatchHttpMessageConverter.class})
@WebMvcTest(controllers = DownloadableContentController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class, useDefaultFilters = false)
@AutoConfigureMockMvc(addFilters = false)
class DownloadableContentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DownloadableContentService downloadableContentService;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public DownloadableContentRepresentationModelAssembler downloadableContentRepresentationModelAssembler() {
            return new DownloadableContentRepresentationModelAssembler();
        }
    }

    @Test
    void findById_withValidId_return200AndValidResponse() throws Exception {
        // Arrange
        DownloadableContentDto downloadableContentDto = new DownloadableContentDto();
        downloadableContentDto.setId(5L);
        downloadableContentDto.setGameId(6L);
        downloadableContentDto.setName("test-name");
        downloadableContentDto.setDescription("test-description");
        downloadableContentDto.setReleaseDate(LocalDate.now());
        downloadableContentDto.setSlug("test-slug");
        downloadableContentDto.setCreatedAt(LocalDateTime.now());
        downloadableContentDto.setUpdatedAt(LocalDateTime.now());
        downloadableContentDto.setVersion(1L);

        Mockito.when(downloadableContentService.findById(ArgumentMatchers.anyLong()))
                .thenReturn(downloadableContentDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/dlc/1")
                .accept("application/vnd.traklibrary.v1.hal+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());

        ResponseVerifier.verifyDownloadableContentDto("", resultActions, downloadableContentDto);
    }
}
