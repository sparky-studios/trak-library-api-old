package com.sparky.trak.game.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@Entity
@Table(name = "game")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 4096)
    private String description;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "age_rating", nullable = false)
    private AgeRating ageRating;

    @EqualsAndHashCode.Exclude
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "game", cascade = CascadeType.ALL)
    private Set<GameGenreXref> gameGenreXrefs;

    @EqualsAndHashCode.Exclude
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "game", cascade = CascadeType.ALL)
    private Set<GamePlatformXref> gamePlatformXrefs;

    @EqualsAndHashCode.Exclude
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "game", cascade = CascadeType.ALL)
    private Set<GamePublisherXref> gamePublisherXrefs;

    @EqualsAndHashCode.Exclude
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "game", cascade = CascadeType.ALL)
    private Set<GameDeveloperXref> gameDeveloperXrefs;

    @Version
    @Column(name = "op_lock_version")
    private Long version;
}
