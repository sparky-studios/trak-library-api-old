package com.sparkystudios.traklibrary.game.server.controller;

import com.sparkystudios.traklibrary.game.domain.ImageSize;
import com.sparkystudios.traklibrary.game.server.configuration.TrakHalJsonMediaTypeConfiguration;
import com.sparkystudios.traklibrary.game.server.converter.JsonMergePatchHttpMessageConverter;
import com.sparkystudios.traklibrary.game.server.exception.GlobalExceptionHandler;
import com.sparkystudios.traklibrary.game.service.DownloadableContentImageService;
import com.sparkystudios.traklibrary.game.service.dto.ImageDataDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@Import({DownloadableContentImageController.class, TrakHalJsonMediaTypeConfiguration.class, GlobalExceptionHandler.class, JsonMergePatchHttpMessageConverter.class})
@WebMvcTest(controllers = DownloadableContentImageController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class, useDefaultFilters = false)
@AutoConfigureMockMvc(addFilters = false)
class DownloadableContentImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DownloadableContentImageService downloadableContentImageService;

    @Test
    void saveDownloadableContentImageForDownloadableContentIdAndGameImageSize_withInvalidFileData_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/dlc/1/image")
                .param("image-size", ImageSize.SMALL.name())
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
    void saveDownloadableContentImageForDownloadableContentIdAndGameImageSize_withValidFileData_returns204() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "some xml".getBytes());

        Mockito.doNothing()
                .when(downloadableContentImageService).upload(ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.any());

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/dlc/1/image")
                .file(file)
                .param("image-size", ImageSize.SMALL.name())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept("application/vnd.sparkystudios.traklibrary-hal+json;version=1.0"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void findDownloadableContentImageByDownloadableContentIdAndImageSize_withValidId_returns200() throws Exception {
        // Arrange
        ImageDataDto imageDataDto = new ImageDataDto();
        imageDataDto.setContent(new byte[] { 'a', 'b' });
        imageDataDto.setFilename("filename.png");

        Mockito.when(downloadableContentImageService.download(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(ImageSize.SMALL)))
                .thenReturn(imageDataDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/dlc/1/image")
                .param("image-size", ImageSize.SMALL.name())
                .accept(MediaType.APPLICATION_OCTET_STREAM_VALUE));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
