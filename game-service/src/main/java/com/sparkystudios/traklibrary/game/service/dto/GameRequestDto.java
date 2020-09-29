package com.sparkystudios.traklibrary.game.service.dto;

import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Relation(collectionRelation = "data", itemRelation = "game-request")
public class GameRequestDto {

    private long id;

    @NotEmpty(message = "{game-request.validation.title.not-empty}")
    private String title;

    @Size(max = 1024, message = "{game-request.validation.description.size}")
    private String notes;

    private boolean completed;

    private LocalDateTime completedDate;

    private long userId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long version;
}
