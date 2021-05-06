package com.sparkystudios.traklibrary.game.service.dto;

import com.sparkystudios.traklibrary.game.domain.GameMode;
import lombok.Data;

import java.util.EnumSet;
import java.util.Set;

@Data
public class GameFiltersDto {

    private Set<GameFilterDto> platforms;

    private Set<GameFilterDto> genres;

    private EnumSet<GameMode> gameModes = EnumSet.allOf(GameMode.class);
}
