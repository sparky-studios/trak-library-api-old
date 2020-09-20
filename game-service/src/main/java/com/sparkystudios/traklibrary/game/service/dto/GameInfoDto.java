package com.sparkystudios.traklibrary.game.service.dto;

import com.sparkystudios.traklibrary.game.domain.AgeRating;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

@Data
@Relation(collectionRelation = "data", itemRelation = "gameInfo")
public class GameInfoDto {

    private long id;

    private String title;

    private String description;

    private LocalDate releaseDate;

    private AgeRating ageRating;

    private Long version;

    private Set<PlatformDto> platforms = new TreeSet<>();

    private Set<PublisherDto> publishers = new TreeSet<>();

    private Set<GenreDto> genres = new TreeSet<>();
}
