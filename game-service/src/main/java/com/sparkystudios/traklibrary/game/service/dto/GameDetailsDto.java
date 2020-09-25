package com.sparkystudios.traklibrary.game.service.dto;

import com.sparkystudios.traklibrary.game.domain.AgeRating;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

import java.util.Set;
import java.util.TreeSet;

@Data
@Relation(collectionRelation = "data", itemRelation = "gameDetails")
public class GameDetailsDto {

    private long id;

    private String title;

    private String description;

    private AgeRating ageRating;

    private Long version;

    private Set<PlatformDto> platforms = new TreeSet<>();

    private Set<PublisherDto> publishers = new TreeSet<>();

    private Set<GenreDto> genres = new TreeSet<>();

    private Set<GameReleaseDateDto> releaseDates = new TreeSet<>();
}
