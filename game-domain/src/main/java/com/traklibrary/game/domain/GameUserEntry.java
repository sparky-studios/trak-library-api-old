package com.traklibrary.game.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@Entity
@Table(name = "game_user_entry")
public class GameUserEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private long id;

    @Column(name = "game_id", nullable = false, updatable = false)
    private long gameId;

    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", updatable = false, insertable = false)
    private Game game;

    @Column(name = "platform_id", nullable = false, updatable = false)
    private long platformId;

    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_id", updatable = false, insertable = false)
    private Platform platform;

    @Column(name = "user_id", nullable = false, updatable = false)
    private long userId;

    @Column(name = "status", nullable = false)
    private GameUserEntryStatus status;

    @Column(name = "rating", nullable = false)
    private short rating;

    @Version
    @Column(name = "op_lock_version")
    private Long version;
}
