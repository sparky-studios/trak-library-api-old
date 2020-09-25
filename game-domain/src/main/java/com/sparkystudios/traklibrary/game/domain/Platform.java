package com.sparkystudios.traklibrary.game.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Data
@Entity
@Table(name = "platform")
public class Platform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = 4096)
    private String description;

    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "platforms", cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.DETACH}, fetch = FetchType.LAZY)
    private Set<Game> games = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "platform", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<PlatformReleaseDate> releaseDates = new TreeSet<>();

    @Version
    @Column(name = "op_lock_version")
    private Long version;

    /**
     * Convenience method that is used to add a {@link Game} to the {@link Platform}. As
     * the relationship between the {@link Platform} and {@link Game} is bi-directional,
     * it needs to be added and associated with on both sides of the relationship, which
     * this method achieved.
     *
     * @param game The {@link Game} to add to the {@link Platform}.
     */
    public void addGame(Game game) {
        games.add(game);
        game.getPlatforms().add(this);
    }

    /**
     * Convenience method that is used to remove a {@link Game} to the {@link Platform}. As
     * the relationship between the {@link Platform} and {@link Game} is bi-directional,
     * it needs to be added and associated with on both sides of the relationship, which
     * this method achieved.
     *
     * @param game The {@link Game} to remove to the {@link Platform}.
     */
    public void removeGame(Game game) {
        games.remove(game);
        game.getPlatforms().remove(this);
    }

    /**
     * Convenience method that is used to add a {@link PlatformReleaseDate} to the {@link Game}. As
     * the relationship between the {@link Game} and {@link PlatformReleaseDate} is bi-directional,
     * it needs to be added and associated with on both sides of the relationship, which
     * this method achieved.
     *
     * @param platformReleaseDate The {@link PlatformReleaseDate} to add to the {@link Game}.
     */
    public void addReleaseDate(PlatformReleaseDate platformReleaseDate) {
        releaseDates.add(platformReleaseDate);
        platformReleaseDate.setPlatform(this);
    }

    /**
     * Convenience method that is used to remove a {@link PlatformReleaseDate} to the {@link Game}. As
     * the relationship between the {@link Game} and {@link PlatformReleaseDate} is bi-directional,
     * it needs to be added and associated with on both sides of the relationship, which
     * this method achieved.
     *
     * @param platformReleaseDate The {@link PlatformReleaseDate} to remove from the {@link Game}.
     */
    public void removeReleaseDate(PlatformReleaseDate platformReleaseDate) {
        releaseDates.remove(platformReleaseDate);
        platformReleaseDate.setPlatform(null);
    }
}
