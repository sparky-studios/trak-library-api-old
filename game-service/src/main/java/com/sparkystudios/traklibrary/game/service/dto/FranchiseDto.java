package com.sparkystudios.traklibrary.game.service.dto;

import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Relation(collectionRelation = "data", itemRelation = "franchise")
public class FranchiseDto {

    private long id;

    @NotEmpty(message = "{franchise.validation.title.not-empty}")
    @Size(max = 255, message = "{franchise.validation.title.size}")
    private String title;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long version;
}
