package com.sparky.trak.game.service.dto;

import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Relation(collectionRelation = "data", itemRelation = "game-request")
public class GameRequestDto {

    private long id;

    @NotEmpty(message = "{game-request.validation.title.not-empty}")
    private String title;

    private boolean completed;

    private LocalDateTime completedDate;

    private long userId;

    private Long version;
}
