package com.sparkystudios.traklibrary.game.service.dto;

import com.sparkystudios.traklibrary.game.domain.AgeRating;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

@Data
@Relation(collectionRelation = "data", itemRelation = "game")
public class GameDto {

    private long id;

    @NotEmpty(message = "{game.validation.title.not-empty}")
    private String title;

    @Size(max = 4096, message = "{game.validation.description.size}")
    private String description;

    @NotNull(message = "{game.validation.age-rating.not-null}")
    private AgeRating ageRating;

    private Long version;

    private TreeSet<GameReleaseDateDto> releaseDates = new TreeSet<>();
}
