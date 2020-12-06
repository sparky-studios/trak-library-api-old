package com.sparkystudios.traklibrary.email.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.sparkystudios.traklibrary.email.service.EmailService;
import com.sparkystudios.traklibrary.email.service.dto.EmailRecoveryRequestDto;
import com.sparkystudios.traklibrary.email.service.dto.EmailVerificationRequestDto;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@Import(EmailController.class)
@WebMvcTest(controllers = EmailController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class, useDefaultFilters = false)
@AutoConfigureMockMvc(addFilters = false)
class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmailService emailService;

    @Test
    void sendVerificationEmail_withNoBody_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/verification")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void sendVerificationEmail_withInvalidBody_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/verification")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1+json")
                .content(objectMapper.writeValueAsString(new EmailVerificationRequestDto())));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void sendVerificationEmail_withValidBody_returns200AndValidResponse() throws Exception {
        // Arrange
        EmailVerificationRequestDto emailVerificationRequestDto = new EmailVerificationRequestDto();
        emailVerificationRequestDto.setEmailAddress("test@traklibrary.com");
        emailVerificationRequestDto.setVerificationCode("12345");

        Mockito.doNothing().when(emailService)
                .sendVerificationEmail(ArgumentMatchers.any());

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/verification")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1+json")
                .content(objectMapper.writeValueAsString(emailVerificationRequestDto)));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void sendRecoveryEmail_withNoBody_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/recovery")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void sendRecoveryEmail_withInvalidBody_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/recovery")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1+json")
                .content(objectMapper.writeValueAsString(new EmailVerificationRequestDto())));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void sendRecoveryEmail_withValidBody_returns200AndValidResponse() throws Exception {
        // Arrange
        EmailRecoveryRequestDto emailRecoveryRequestDto = new EmailRecoveryRequestDto();
        emailRecoveryRequestDto.setEmailAddress("test@traklibrary.com");
        emailRecoveryRequestDto.setRecoveryToken(Strings.repeat("a", 30));

        Mockito.doNothing().when(emailService)
                .sendRecoveryEmail(ArgumentMatchers.any());

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/recovery")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1+json")
                .content(objectMapper.writeValueAsString(emailRecoveryRequestDto)));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void sendChangePasswordEmail_withNoBody_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void sendChangePasswordEmail_withInvalidBody_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1+json")
                .content(objectMapper.writeValueAsString(new EmailRecoveryRequestDto())));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void sendChangePasswordEmail_withValidBody_returns200AndValidResponse() throws Exception {
        // Arrange
        EmailRecoveryRequestDto emailRecoveryRequestDto = new EmailRecoveryRequestDto();
        emailRecoveryRequestDto.setEmailAddress("test@traklibrary.com");
        emailRecoveryRequestDto.setRecoveryToken(Strings.repeat("a", 30));

        Mockito.doNothing().when(emailService)
                .sendChangePasswordEmail(ArgumentMatchers.any());

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1+json")
                .content(objectMapper.writeValueAsString(emailRecoveryRequestDto)));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
