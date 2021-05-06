package com.sparkystudios.traklibrary.game.service.dto;

import com.sparkystudios.traklibrary.game.domain.AgeRating;
import com.sparkystudios.traklibrary.game.domain.GameMode;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

@Data
@Relation(collectionRelation = "data", itemRelation = "gameDetails")
public class GameDetailsDto {

    private long id;

    private String title;

    private String description;

    private Set<AgeRatingDto> ageRatings = new TreeSet<>();

    private AgeRating ageRating;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Set<GameMode> gameModes = EnumSet.noneOf(GameMode.class);

    private Long franchiseId;

    private String slug;

    private Long version;

    private Set<PlatformDto> platforms;

    private Set<PublisherDto> publishers;

    private Set<GenreDto> genres;

    private Set<GameReleaseDateDto> releaseDates;

    private Set<DownloadableContentDto> downloadableContents;

    private FranchiseDto franchise;
}
