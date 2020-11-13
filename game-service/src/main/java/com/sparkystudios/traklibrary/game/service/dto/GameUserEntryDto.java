package com.sparkystudios.traklibrary.game.service.dto;

import com.sparkystudios.traklibrary.game.domain.GameUserEntryStatus;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;

@Data
@Relation(collectionRelation = "data", itemRelation = "game-user-entry")
public class GameUserEntryDto {

    private long id;

    private long gameId;

    private String gameTitle;

    private long userId;

    private GameUserEntryStatus status;

    private Collection<String> publishers = new ArrayList<>();

    private short rating;

    private Set<GameUserEntryPlatformDto> gameUserEntryPlatforms = new TreeSet<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long version;
}
