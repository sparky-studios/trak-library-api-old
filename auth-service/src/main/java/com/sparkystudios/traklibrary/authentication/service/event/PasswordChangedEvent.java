package com.sparkystudios.traklibrary.authentication.service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangedEvent {

    private String username;

    private String emailAddress;
}
