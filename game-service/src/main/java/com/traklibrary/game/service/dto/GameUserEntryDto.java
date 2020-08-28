package com.traklibrary.game.service.dto;

import com.traklibrary.game.domain.GameUserEntryStatus;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

@Data
@Relation(collectionRelation = "data", itemRelation = "game-user-entry")
public class GameUserEntryDto {

    private long id;

    private long gameId;

    private String gameTitle;

    private LocalDate gameReleaseDate;

    private long platformId;

    private String platformName;

    private long userId;

    @NotNull(message = "{game-user-entry.validation.status.not-null}")
    private GameUserEntryStatus status;

    private Collection<String> publishers = new ArrayList<>();

    @Min(message = "{game-user-entry.validation.rating.min}", value = 0)
    @Max(message = "{game-user-entry.validation.rating.max}", value = 5)
    private short rating;

    private Long version;
}
