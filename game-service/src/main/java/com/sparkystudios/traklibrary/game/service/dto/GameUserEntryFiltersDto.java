package com.sparkystudios.traklibrary.game.service.dto;

import com.sparkystudios.traklibrary.game.domain.GameUserEntryStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.EnumSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class GameUserEntryFiltersDto extends GameFiltersDto {

    private Set<GameUserEntryStatus> statuses = EnumSet.allOf(GameUserEntryStatus.class);
}
