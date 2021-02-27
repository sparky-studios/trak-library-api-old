package com.sparkystudios.traklibrary.game.service.dto.request;

import com.sparkystudios.traklibrary.game.domain.GameUserEntryStatus;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Data
public class GameUserEntryRequest {

    private long gameUserEntryId;

    private long userId;

    private long gameId;

    @Min(message = "{game-user-entry-request.validation.rating.min}", value = 0)
    @Max(message = "{game-user-entry-request.validation.rating.max}", value = 5)
    private short rating;

    @NotNull(message = "{game-user-entry-request.validation.status.not-null}")
    private GameUserEntryStatus status;

    @NotNull(message = "{game-user-entry-request.validation.platforms.not-null}")
    @NotEmpty(message = "{game-user-entry-request.validation.platforms.not-empty}")
    private Collection<Long> platformIds;

    @NotNull(message = "{game-user-entry-request.validation.downloadable-contents.not-null}")
    private Collection<Long> downloadableContentIds;
}
