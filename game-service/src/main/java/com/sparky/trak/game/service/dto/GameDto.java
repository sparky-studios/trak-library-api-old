package com.sparky.trak.game.service.dto;

import com.sparky.trak.game.domain.AgeRating;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Relation(collectionRelation = "data", itemRelation = "game")
public class GameDto {

    private long id;

    @NotEmpty(message = "{game.validation.title.not-empty}")
    private String title;

    @Size(max = 4096, message = "{game.validation.description.size}")
    private String description;

    private LocalDate releaseDate;

    @NotNull(message = "{game.validation.age-rating.not-null}")
    private AgeRating ageRating;

    private Long version;
}
