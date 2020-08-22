package com.traklibrary.email.server.controller;

import com.traklibrary.email.service.EmailService;
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
public class EmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailService emailService;

    @Test
    void sendVerificationEmail_withMissingParameters_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/verification")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.0+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void sendVerificationEmail_withParameters_returns200AndValidResponse() throws Exception {
        // Arrange
        Mockito.doNothing().when(emailService)
                .sendVerificationEmail(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/verification")
                .param("email-address", "test@traklibrary.com")
                .param("verification-code", "1234A")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.0+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void sendRecoveryEmail_withMissingParameters_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/recovery")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.0+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void sendRecoveryEmail_withParameters_returns200AndValidResponse() throws Exception {
        // Arrange
        Mockito.doNothing().when(emailService)
                .sendRecoveryEmail(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/recovery")
                .param("email-address", "test@traklibrary.com")
                .param("recovery-token", "1234A")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.0+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
