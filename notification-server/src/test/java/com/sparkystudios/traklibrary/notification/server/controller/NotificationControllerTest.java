package com.sparkystudios.traklibrary.notification.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparkystudios.traklibrary.notification.server.exception.GlobalExceptionHandler;
import com.sparkystudios.traklibrary.notification.service.MobileDeviceLinkService;
import com.sparkystudios.traklibrary.notification.service.NotificationService;
import com.sparkystudios.traklibrary.notification.service.dto.MobileDeviceLinkRegistrationRequestDto;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

@Import({NotificationController.class, GlobalExceptionHandler.class})
@WebMvcTest(controllers = NotificationController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class, useDefaultFilters = false)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MobileDeviceLinkService mobileDeviceLinkService;

    @Test
    void register_withInvalidMobileDeviceLinkRegistrationRequestDto_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1+json")
                .content(objectMapper.writeValueAsString(new MobileDeviceLinkRegistrationRequestDto())));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").exists());
    }

    @Test
    void register_withValidMobileDeviceLinkRegistrationRequestDto_returns204() throws Exception {
        // Arrange
        MobileDeviceLinkRegistrationRequestDto mobileDeviceLinkRegistrationRequestDto = new MobileDeviceLinkRegistrationRequestDto();
        mobileDeviceLinkRegistrationRequestDto.setUserId(1L);
        mobileDeviceLinkRegistrationRequestDto.setDeviceGuid(String.join("", Collections.nCopies(36, "t")));
        mobileDeviceLinkRegistrationRequestDto.setToken("token");

        Mockito.doNothing()
                .when(mobileDeviceLinkService).register(ArgumentMatchers.any());

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1+json")
                .content(objectMapper.writeValueAsString(mobileDeviceLinkRegistrationRequestDto)));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void unregister_withMissingUserIdParameter_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/unregister")
                .param("device-guid", "device-guid")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").exists());
    }

    @Test
    void unregister_withMissingDeviceGuidParameter_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/unregister")
                .param("user-id", "1")
                .accept("application/vnd.traklibrary.v1+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").exists());
    }

    @Test
    void unregister_withValidParameters_returns204() throws Exception {
        // Arrange
        Mockito.doNothing()
                .when(mobileDeviceLinkService).unregister(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString());

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/unregister")
                .param("user-id", "1")
                .param("device-guid", "device-guid")
                .accept("application/vnd.traklibrary.v1+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
