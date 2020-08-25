package com.traklibrary.game.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "publisher")
@PrimaryKeyJoinColumn(name="id")
public class Publisher extends Company {

    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "publishers", cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.DETACH}, fetch = FetchType.LAZY)
    private Set<Game> games = new HashSet<>();

    /**
     * Convenience method that is used to add a {@link Game} to the {@link Publisher}. As
     * the relationship between the {@link Publisher} and {@link Game} is bi-directional,
     * it needs to be added and associated with on both sides of the relationship, which
     * this method achieved.
     *
     * @param game The {@link Game} to add to the {@link Publisher}.
     */
    public void addGame(Game game) {
        games.add(game);
        game.getPublishers().add(this);
    }

    /**
     * Convenience method that is used to remove a {@link Game} to the {@link Publisher}. As
     * the relationship between the {@link Publisher} and {@link Game} is bi-directional,
     * it needs to be added and associated with on both sides of the relationship, which
     * this method achieved.
     *
     * @param game The {@link Game} to remove to the {@link Publisher}.
     */
    public void removeGame(Game game) {
        games.remove(game);
        game.getPublishers().remove(this);
    }
}
