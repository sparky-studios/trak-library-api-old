package com.sparkystudios.traklibrary.game.service.dto;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;

@Data
@Relation(collectionRelation = "data", itemRelation = "game-user-entry-platform")
public class GameUserEntryPlatformDto implements Comparable<GameUserEntryPlatformDto> {

    private long id;

    private long gameUserEntryId;

    private long platformId;

    private String platformName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long version;

    /**
     * Used for comparison between two {@link GameUserEntryPlatformDto} objects. It's used
     * internally whenever a {@link GameUserEntryPlatformDto} is the type within a list which
     * supported inserted sort, such as a {@link java.util.TreeSet}.
     *
     * The order in which {@link GameUserEntryPlatformDto} instances are sorted are by {@link GameUserEntryPlatformDto#platformName},
     * and then {@link GameUserEntryPlatformDto#id}.
     *
     * @param other The other {@link GameUserEntryPlatformDto} instance to compare against.
     *
     * @return  a negative integer, zero, or a positive integer as this {@link GameUserEntryPlatformDto}
     *          is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(GameUserEntryPlatformDto other) {
        return ComparisonChain.start()
                .compare(platformName, other.platformName, Ordering.natural().nullsLast())
                .compare(id, other.id)
                .result();
    }
}
