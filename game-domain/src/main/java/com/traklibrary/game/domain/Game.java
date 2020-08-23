package com.traklibrary.game.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
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
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "game_genre_xref",
            joinColumns = {@JoinColumn(name = "game_id")},
            inverseJoinColumns = {@JoinColumn(name = "genre_id")}
    )
    private Set<Genre> genres = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "game_platform_xref",
            joinColumns = {@JoinColumn(name = "game_id")},
            inverseJoinColumns = {@JoinColumn(name = "platform_id")}
    )
    private Set<Platform> platforms = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "game_publisher_xref",
            joinColumns = {@JoinColumn(name = "game_id")},
            inverseJoinColumns = {@JoinColumn(name = "publisher_id")}
    )
    private Set<Publisher> publishers = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "game_developer_xref",
            joinColumns = {@JoinColumn(name = "game_id")},
            inverseJoinColumns = {@JoinColumn(name = "developer_id")}
    )
    private Set<Developer> developers = new HashSet<>();

    @Version
    @Column(name = "op_lock_version")
    private Long version;

    /**
     * Convenience method that is used to add a {@link Genre} to the {@link Game}. As
     * the relationship between the {@link Game} and {@link Genre} is bi-directional,
     * it needs to be added and associated with on both sides of the relationship, which
     * this method achieved.
     *
     * @param genre The {@link Genre} to add to the {@link Game}.
     */
    public void addGenre(Genre genre) {
        genres.add(genre);
        genre.getGames().add(this);
    }

    /**
     * Convenience method that is used to remove a {@link Genre} to the {@link Game}. As
     * the relationship between the {@link Game} and {@link Genre} is bi-directional,
     * it needs to be added and associated with on both sides of the relationship, which
     * this method achieved.
     *
     * @param genre The {@link Genre} to remove to the {@link Game}.
     */
    public void removeGenre(Genre genre) {
        genres.remove(genre);
        genre.getGames().remove(this);
    }

    /**
     * Convenience method that is used to add a {@link Platform} to the {@link Game}. As
     * the relationship between the {@link Game} and {@link Platform} is bi-directional,
     * it needs to be added and associated with on both sides of the relationship, which
     * this method achieved.
     *
     * @param platform The {@link Platform} to add to the {@link Game}.
     */
    public void addPlatform(Platform platform) {
        platforms.add(platform);
        platform.getGames().add(this);
    }

    /**
     * Convenience method that is used to remove a {@link Platform} to the {@link Game}. As
     * the relationship between the {@link Game} and {@link Platform} is bi-directional,
     * it needs to be added and associated with on both sides of the relationship, which
     * this method achieved.
     *
     * @param platform The {@link Platform} to remove to the {@link Game}.
     */
    public void removePlatform(Platform platform) {
        platforms.remove(platform);
        platform.getGames().remove(this);
    }

    /**
     * Convenience method that is used to add a {@link Publisher} to the {@link Game}. As
     * the relationship between the {@link Game} and {@link Publisher} is bi-directional,
     * it needs to be added and associated with on both sides of the relationship, which
     * this method achieved.
     *
     * @param publisher The {@link Publisher} to add to the {@link Game}.
     */
    public void addPublisher(Publisher publisher) {
        publishers.add(publisher);
        publisher.getGames().add(this);
    }

    /**
     * Convenience method that is used to remove a {@link Publisher} to the {@link Game}. As
     * the relationship between the {@link Game} and {@link Publisher} is bi-directional,
     * it needs to be added and associated with on both sides of the relationship, which
     * this method achieved.
     *
     * @param publisher The {@link Publisher} to remove to the {@link Game}.
     */
    public void removeDeveloper(Publisher publisher) {
        publishers.remove(publisher);
        publisher.getGames().remove(this);
    }

    /**
     * Convenience method that is used to add a {@link Developer} to the {@link Game}. As
     * the relationship between the {@link Game} and {@link Developer} is bi-directional,
     * it needs to be added and associated with on both sides of the relationship, which
     * this method achieved.
     *
     * @param developer The {@link Developer} to add to the {@link Game}.
     */
    public void addDeveloper(Developer developer) {
        developers.add(developer);
        developer.getGames().add(this);
    }

    /**
     * Convenience method that is used to remove a {@link Developer} to the {@link Game}. As
     * the relationship between the {@link Game} and {@link Developer} is bi-directional,
     * it needs to be added and associated with on both sides of the relationship, which
     * this method achieved.
     *
     * @param developer The {@link Developer} to remove to the {@link Game}.
     */
    public void removeDeveloper(Developer developer) {
        developers.remove(developer);
        developer.getGames().remove(this);
    }
}
