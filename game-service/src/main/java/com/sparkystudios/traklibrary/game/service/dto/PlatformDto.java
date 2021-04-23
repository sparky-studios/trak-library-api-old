package com.sparkystudios.traklibrary.game.service.dto;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

@Data
@Relation(collectionRelation = "data", itemRelation = "platform")
public class PlatformDto implements Comparable<PlatformDto> {

    private long id;

    @NotEmpty(message = "{platform.validation.name.not-empty}")
    private String name;

    @Size(max = 4096, message = "{platform.validation.description.size}")
    private String description;

    private String slug;

    private Long version;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Set<PlatformReleaseDateDto> releaseDates = new TreeSet<>();

    /**
     * Used for comparison between two {@link PlatformDto} objects. It's used
     * internally whenever a {@link PlatformDto} is the type within a list which
     * supported inserted sort, such as a {@link java.util.TreeSet}.
     *
     * The order in which {@link PlatformDto} instances are sorted are by {@link PlatformDto#name},
     * and the {@link PlatformDto#id}.
     *
     * @param other The other {@link PlatformDto} instance to compare against.
     *
     * @return  a negative integer, zero, or a positive integer as this {@link PlatformDto}
     *          is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(PlatformDto other) {
        return ComparisonChain.start()
                .compare(name, other.name, Ordering.natural().nullsLast())
                .compare(id, other.id)
                .result();
    }
}
