package com.sparkystudios.traklibrary.game.service.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateGameRequest extends NewGameRequest {

    private long id;

    private Long version;
}
