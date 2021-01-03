package com.sparkystudios.traklibrary.game.service.dto;

import com.sparkystudios.traklibrary.game.domain.GameUserEntryStatus;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

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
