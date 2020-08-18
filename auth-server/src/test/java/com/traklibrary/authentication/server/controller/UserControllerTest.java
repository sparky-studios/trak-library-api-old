package com.traklibrary.authentication.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.traklibrary.authentication.service.UserService;
import com.traklibrary.authentication.service.dto.CheckedResponse;
import com.traklibrary.authentication.service.dto.RecoveryRequestDto;
import com.traklibrary.authentication.service.dto.RegistrationRequestDto;
import com.traklibrary.authentication.service.dto.UserResponseDto;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@Import(UserController.class)
@WebMvcTest(controllers = UserController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class, useDefaultFilters = false)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void save_withInvalidRegistrationRequest_returns400() throws Exception {
        // Arrange
        RegistrationRequestDto registrationRequestDto = new RegistrationRequestDto();

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.0+json")
                .content(objectMapper.writeValueAsString(registrationRequestDto)));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void save_withValidRegistrationRequest_returns201AndValidResponse() throws Exception {
        // Arrange
        RegistrationRequestDto registrationRequestDto = new RegistrationRequestDto();
        registrationRequestDto.setUsername("username");
        registrationRequestDto.setPassword("Password123");
        registrationRequestDto.setEmailAddress("test@traklibrary.com");

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(1L);
        userResponseDto.setUsername("username");
        userResponseDto.setVerified(true);

        Mockito.when(userService.save(ArgumentMatchers.any()))
                .thenReturn(new CheckedResponse<>(userResponseDto));

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.0+json")
                .content(objectMapper.writeValueAsString(registrationRequestDto)));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id", Matchers.is((int)userResponseDto.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username", Matchers.is(userResponseDto.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.verified", Matchers.is(userResponseDto.isVerified())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage", Matchers.emptyOrNullString()));
    }

    @Test
    void update_withInvalidRegistrationRequest_returns400() throws Exception {
        // Arrange
        RecoveryRequestDto recoveryRequestDto = new RecoveryRequestDto();

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.0+json")
                .content(objectMapper.writeValueAsString(recoveryRequestDto)));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void update_withValidRecoveryRequest_returns200AndValidResponse() throws Exception {
        // Arrange
        RecoveryRequestDto recoveryRequestDto = new RecoveryRequestDto();
        recoveryRequestDto.setUsername("username");
        recoveryRequestDto.setRecoveryToken("aaaaaaaaaabbbbbbbbbbcccccccccc");
        recoveryRequestDto.setPassword("Password123");

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(1L);
        userResponseDto.setUsername("username");
        userResponseDto.setVerified(true);

        Mockito.when(userService.update(ArgumentMatchers.any()))
                .thenReturn(new CheckedResponse<>(userResponseDto));

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.0+json")
                .content(objectMapper.writeValueAsString(recoveryRequestDto)));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id", Matchers.is((int)userResponseDto.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username", Matchers.is(userResponseDto.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.verified", Matchers.is(userResponseDto.isVerified())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage", Matchers.emptyOrNullString()));
    }

    @Test
    void verify_withMissingParameters_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/users/username/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.0+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void verify_withParameters_returns200AndValidResponse() throws Exception {
        // Arrange
        Mockito.when(userService.verify(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(new CheckedResponse<>(true));

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/users/username/verify")
                .param("verification-code", "12345")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.0+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.is(true)));
    }

    @Test
    void reverify_withParameters_returns204() throws Exception {
        // Arrange
        Mockito.doNothing().when(userService)
                .reverify(ArgumentMatchers.anyString());

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/users/username/reverify")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.0+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void recover_withMissingParameters_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/users/recover")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.0+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void recover_withParameters_returns204() throws Exception {
        // Arrange
        Mockito.doNothing().when(userService)
                .requestRecovery(ArgumentMatchers.anyString());

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/users/recover")
                .param("email-address", "test@traklibrary.com")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1.0+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
