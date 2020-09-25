package com.sparkystudios.traklibrary.game.service.dto;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.sparkystudios.traklibrary.game.domain.GameRegion;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class PlatformReleaseDateDto implements Comparable<PlatformReleaseDateDto> {

    private long id;

    @NotNull(message = "{platform-release-date.validation.region.not-null}")
    private GameRegion region;

    private LocalDate releaseDate;

    private Long version;

    /**
     * Used for comparison between two {@link PlatformReleaseDateDto} objects. It's used
     * internally whenever a {@link PlatformReleaseDateDto} is the type within a list which
     * supported inserted sort, such as a {@link java.util.TreeSet}.
     *
     * The order in which {@link PlatformReleaseDateDto} instances are sorted are by {@link PlatformReleaseDateDto#region}.
     *
     * @param other The other {@link PlatformReleaseDateDto} instance to compare against.
     *
     * @return  a negative integer, zero, or a positive integer as this {@link PlatformReleaseDateDto}
     *          is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(PlatformReleaseDateDto other) {
        return ComparisonChain.start()
                .compare(region != null ? region.getId() : null, other.region != null ? other.region.getId() : null, Ordering.natural().nullsLast())
                .result();
    }
}
