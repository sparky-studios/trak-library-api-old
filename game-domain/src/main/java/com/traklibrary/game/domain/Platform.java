package com.traklibrary.game.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "platforms", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<Game> games = new HashSet<>();

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
}
