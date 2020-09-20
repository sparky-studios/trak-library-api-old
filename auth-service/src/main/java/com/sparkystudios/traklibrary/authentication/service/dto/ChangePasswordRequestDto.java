package com.sparkystudios.traklibrary.authentication.service.dto;

import com.sparkystudios.traklibrary.authentication.service.validation.ValidPassword;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class ChangePasswordRequestDto {

    @NotEmpty(message = "{change-password-request.validation.recovery-token.not-empty}")
    @Size(min = 30, max = 30, message = "{change-password-request.validation.recovery-token.size}")
    private String recoveryToken;

    @ValidPassword(message = "{change-password-request.validation.new-password.invalid}")
    private String newPassword;
}
