package com.sparkystudios.traklibrary.game.service.dto;

import com.google.common.collect.ComparisonChain;
import lombok.Data;

@Data
public class GameFilterDto implements Comparable<GameFilterDto> {

    private String name;

    private long id;

    /**
     * Used for comparison between two {@link GameFilterDto} objects. It's used
     * internally whenever a {@link GameFilterDto} is the type within a list which
     * supported inserted sort, such as a {@link java.util.TreeSet}.
     *
     * The order in which {@link GameUserEntryPlatformDto} instances are sorted are by {@link GameFilterDto#name},
     * and then {@link GameFilterDto#id}.
     *
     * @param other The other {@link GameFilterDto} instance to compare against.
     *
     * @return  a negative integer, zero, or a positive integer as this {@link GameFilterDto}
     *          is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(GameFilterDto other) {
        return ComparisonChain.start()
                .compare(name, other.name)
                .compare(id, other.id)
                .result();
    }
}
