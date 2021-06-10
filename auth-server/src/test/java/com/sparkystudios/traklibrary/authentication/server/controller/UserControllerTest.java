package com.sparkystudios.traklibrary.authentication.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparkystudios.traklibrary.authentication.server.exception.GlobalExceptionHandler;
import com.sparkystudios.traklibrary.authentication.service.UserService;
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

import java.util.Collections;

@Import({UserController.class, GlobalExceptionHandler.class})
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
                .accept("application/vnd.traklibrary.v1+json")
                .content(objectMapper.writeValueAsString(registrationRequestDto)));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").exists());
    }

    @Test
    void save_withValidRegistrationRequest_returns201AndValidResponse() throws Exception {
        // Arrange
        RegistrationRequestDto registrationRequestDto = new RegistrationRequestDto();
        registrationRequestDto.setUsername("username");
        registrationRequestDto.setPassword("Password123");
        registrationRequestDto.setEmailAddress("test@traklibrary.com");

        var registrationResponseDto = new RegistrationResponseDto();
        registrationResponseDto.setUserId(1L);
        registrationResponseDto.setQrData(new byte[] { 'p' });

        Mockito.when(userService.save(ArgumentMatchers.any()))
                .thenReturn(new CheckedResponse<>(registrationResponseDto));

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1+json")
                .content(objectMapper.writeValueAsString(registrationRequestDto)));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.userId", Matchers.is((int)registrationResponseDto.getUserId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.qrData", Matchers.anything()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage", Matchers.emptyOrNullString()));
    }

    @Test
    void update_withInvalidRegistrationRequest_returns400() throws Exception {
        // Arrange
        RecoveryRequestDto recoveryRequestDto = new RecoveryRequestDto();

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1+json")
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
                .accept("application/vnd.traklibrary.v1+json")
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
    void deleteById_withValidData_returns204() throws Exception {
        // Arrange
        Mockito.doNothing()
                .when(userService).deleteById(ArgumentMatchers.anyLong());

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/users/1")
                .accept("application/vnd.traklibrary.v1+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void verify_withMissingParameters_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/users/1/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void verify_withParameters_returns200AndValidResponse() throws Exception {
        // Arrange
        Mockito.when(userService.verify(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
                .thenReturn(new CheckedResponse<>(true));

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/users/1/verify")
                .param("verification-code", "12345")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.is(true)));
    }

    @Test
    void reverify_withParameters_returns204() throws Exception {
        // Arrange
        Mockito.doNothing().when(userService)
                .reverify(ArgumentMatchers.anyLong());

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/users/1/reverify")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void recover_withMissingParameters_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/users/recover")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1+json"));

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
                .accept("application/vnd.traklibrary.v1+json"));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void changePassword_withInvalidChangePasswordRequestDto_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/users/1/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1+json")
                .content(objectMapper.writeValueAsString(new ChangePasswordRequestDto())));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").exists());
    }

    @Test
    void changePassword_withValidChangePasswordRequest_returns200AndValidResponse() throws Exception {
        // Arrange
        ChangePasswordRequestDto changePasswordRequestDto = new ChangePasswordRequestDto();
        changePasswordRequestDto.setCurrentPassword("Password321");
        changePasswordRequestDto.setNewPassword("Password123");

        Mockito.when(userService.changePassword(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(new CheckedResponse<>(true));

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/users/1/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1+json")
                .content(objectMapper.writeValueAsString(changePasswordRequestDto)));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.is(true)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage", Matchers.emptyOrNullString()));
    }

    @Test
    void changeEmailAddress_withInvalidChangeEmailAddressRequestDto_returns400() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/users/1/change-email-address")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1+json")
                .content(objectMapper.writeValueAsString(new ChangeEmailAddressRequestDto())));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").exists());
    }

    @Test
    void changeEmailAddress_withValidChangeEmailAddressRequest_returns200AndValidResponse() throws Exception {
        // Arrange
        ChangeEmailAddressRequestDto changeEmailAddressRequestDto = new ChangeEmailAddressRequestDto();
        changeEmailAddressRequestDto.setEmailAddress("test@traklibrary.com");

        Mockito.when(userService.changeEmailAddress(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(new CheckedResponse<>(true));

        // Act
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/users/1/change-email-address")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/vnd.traklibrary.v1+json")
                .content(objectMapper.writeValueAsString(changeEmailAddressRequestDto)));

        // Assert
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.is(true)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage", Matchers.emptyOrNullString()));
    }
}
