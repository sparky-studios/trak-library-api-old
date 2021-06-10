package com.sparkystudios.traklibrary.authentication.service.dto;

import com.sparkystudios.traklibrary.authentication.service.validation.ValidPassword;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ChangePasswordRequestDto {

    @NotEmpty(message = "{change-password-request.validation.current-password.not-empty}")
    private String currentPassword;

    @ValidPassword(message = "{change-password-request.validation.new-password.invalid}")
    private String newPassword;
}
