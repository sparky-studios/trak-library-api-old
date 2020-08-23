package com.traklibrary.game.service.dto;

import com.traklibrary.game.domain.AgeRating;
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

    private Collection<PlatformDto> platforms;

    private Collection<PublisherDto> publishers;

    private Collection<GenreDto> genres;
}
