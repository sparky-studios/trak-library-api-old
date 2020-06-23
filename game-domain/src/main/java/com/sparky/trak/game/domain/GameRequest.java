package com.sparky.trak.game.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "game_request")
public class GameRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "completed", nullable = false)
    private boolean completed;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @Column(name = "user_id", nullable = false, updatable = false)
    private long userId;

    @Version
    @Column(name = "op_lock_version")
    private Long version;
}
