package com.sparkystudios.traklibrary.game.domain;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "game_release_date")
public class GameReleaseDate implements Comparable<GameReleaseDate> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private long id;

    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @Column(name = "region", nullable = false)
    private GameRegion region;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Version
    @Column(name = "op_lock_version")
    private Long version;

    /**
     * Used for comparison between two {@link GameReleaseDate} objects. It's used
     * internally whenever a {@link GameReleaseDate} is the type within a list which
     * supported inserted sort, such as a {@link java.util.TreeSet}.
     *
     * The order in which {@link GameReleaseDate} instances are sorted are by {@link GameReleaseDate#region}.
     *
     * @param other The other {@link GameReleaseDate} instance to compare against.
     *
     * @return  a negative integer, zero, or a positive integer as this {@link GameReleaseDate}
     *          is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(GameReleaseDate other) {
        return ComparisonChain.start()
                .compare(region != null ? region.getId() : null, other.region != null ? other.region.getId() : null, Ordering.natural().nullsLast())
                .result();
    }
}
