package com.sparkystudios.traklibrary.game.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "developer")
@PrimaryKeyJoinColumn(name = "id")
public class Developer extends Company {

    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "developers", cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.DETACH}, fetch = FetchType.LAZY)
    private Set<Game> games = new HashSet<>();

    /**
     * Convenience method that is used to add a {@link Game} to the {@link Developer}. As
     * the relationship between the {@link Developer} and {@link Game} is bi-directional,
     * it needs to be added and associated with on both sides of the relationship, which
     * this method achieved.
     *
     * @param game The {@link Game} to add to the {@link Developer}.
     */
    public void addGame(Game game) {
        games.add(game);
        game.getDevelopers().add(this);
    }

    /**
     * Convenience method that is used to remove a {@link Game} to the {@link Developer}. As
     * the relationship between the {@link Developer} and {@link Game} is bi-directional,
     * it needs to be added and associated with on both sides of the relationship, which
     * this method achieved.
     *
     * @param game The {@link Game} to remove to the {@link Developer}.
     */
    public void removeGame(Game game) {
        games.remove(game);
        game.getDevelopers().remove(this);
    }
}
