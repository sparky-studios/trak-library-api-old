package com.sparky.trak.game.service.dto;

import com.sparky.trak.game.domain.AgeRating;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDate;
import java.util.Collection;

@Data
@Relation(collectionRelation = "data", itemRelation = "gameInfo")
public class GameInfoDto {

    private long id;

    private String title;

    private String description;

    private LocalDate releaseDate;

    private AgeRating ageRating;

    private Long version;

    private Collection<String> platforms;

    private Collection<String> publishers;

    private Collection<String> genres;
}
