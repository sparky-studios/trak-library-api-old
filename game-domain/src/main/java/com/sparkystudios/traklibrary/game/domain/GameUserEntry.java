package com.sparkystudios.traklibrary.game.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "game_user_entry")
public class GameUserEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private long id;

    @Column(name = "game_id", nullable = false, updatable = false)
    private long gameId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", updatable = false, insertable = false)
    private Game game;

    @Column(name = "user_id", nullable = false, updatable = false)
    private long userId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "gameUserEntry", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<GameUserEntryPlatform> gameUserEntryPlatforms = new ArrayList<>();

    @Column(name = "status", nullable = false)
    private GameUserEntryStatus status;

    @Column(name = "rating", nullable = false)
    private short rating;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "op_lock_version")
    private Long version;

    /**
     * Convenience method that is used to add a {@link GameUserEntryPlatform} to the {@link Game}. As
     * the relationship between the {@link Game} and {@link GameUserEntryPlatform} is bi-directional,
     * it needs to be added and associated with on both sides of the relationship, which
     * this method achieved.
     *
     * @param gameUserEntryPlatform The {@link GameUserEntryPlatform} to add to the {@link Game}.
     */
    public void addGameUserEntryPlatform(GameUserEntryPlatform gameUserEntryPlatform) {
        gameUserEntryPlatforms.add(gameUserEntryPlatform);
        gameUserEntryPlatform.setGameUserEntry(this);
    }

    /**
     * Convenience method that is used to remove a {@link GameUserEntryPlatform} to the {@link Game}. As
     * the relationship between the {@link Game} and {@link GameUserEntryPlatform} is bi-directional,
     * it needs to be added and associated with on both sides of the relationship, which
     * this method achieved.
     *
     * @param gameUserEntryPlatform The {@link Genre} to remove to the {@link Game}.
     */
    public void removeGameUserEntryPlatform(GameUserEntryPlatform gameUserEntryPlatform) {
        gameUserEntryPlatforms.remove(gameUserEntryPlatform);
        gameUserEntryPlatform.setGameUserEntry(null);
    }
}
