package com.sparkystudios.traklibrary.authentication.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparkystudios.traklibrary.authentication.server.exception.GlobalExceptionHandler;
import com.sparkystudios.traklibrary.authentication.service.TwoFactorAuthenticationService;
import com.sparkystudios.traklibrary.authentication.service.dto.*;
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

@Import({TwoFactorAuthenticationController.class, GlobalExceptionHandler.class})
@WebMvcTest(controllers = UserController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class, useDefaultFilters = false)
@AutoConfigureMockMvc(addFilters = false)
class TwoFactorAuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TwoFactorAuthenticationService twoFactorAuthenticationService;

    @Test
    void enable_withNoTwoFactorAuthenticationRequestDtoBody_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/users/1/2fa")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.sparkystudios.traklibrary+json;version=1.0"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").exists());
    }

    @Test
    void enable_withInvalidTwoFactorAuthenticationRequestDto_returns400() throws Exception {
        // Arrange
        TwoFactorAuthenticationRequestDto twoFactorAuthenticationRequestDto = new TwoFactorAuthenticationRequestDto();
        twoFactorAuthenticationRequestDto.setCode("");

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/users/1/2fa")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.sparkystudios.traklibrary+json;version=1.0")
                .content(objectMapper.writeValueAsString(twoFactorAuthenticationRequestDto)));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").exists());
    }

    @Test
    void enable_withValidTwoFactorAuthenticationRequestDto_returns200AndValidResponse() throws Exception {
        // Arrange
        TwoFactorAuthenticationRequestDto twoFactorAuthenticationRequestDto = new TwoFactorAuthenticationRequestDto();
        twoFactorAuthenticationRequestDto.setCode("123");

        var userResponseDto = new UserResponseDto();
        userResponseDto.setId(1L);
        userResponseDto.setUsername("username");
        userResponseDto.setVerified(true);

        Mockito.when(twoFactorAuthenticationService.enable(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(userResponseDto);

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/users/1/2fa")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.sparkystudios.traklibrary+json;version=1.0")
                .content(objectMapper.writeValueAsString(twoFactorAuthenticationRequestDto)));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is((int)userResponseDto.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is(userResponseDto.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.verified", Matchers.is(userResponseDto.isVerified())));
    }

}
