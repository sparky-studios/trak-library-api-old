package com.sparky.trak.game.service.dto;

import com.sparky.trak.game.domain.GameUserEntryStatus;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Relation(collectionRelation = "data", itemRelation = "game-user-entry")
public class GameUserEntryDto {

    private long id;

    private long gameId;

    private String gameTitle;

    private long consoleId;

    private String consoleName;

    private long userId;

    @NotNull(message = "{game-user-entry.validation.status.not-null}")
    private GameUserEntryStatus status;

    @Min(message = "{game-user-entry.validation.rating.min}", value = 0)
    @Max(message = "{game-user-entry.validation.rating.max}", value = 5)
    private short rating;

    private Long version;
}
