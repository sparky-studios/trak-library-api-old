package com.sparkystudios.traklibrary.game.service.dto;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Relation(collectionRelation = "data", itemRelation = "genre")
public class GenreDto implements Comparable<GenreDto> {

    private long id;

    @NotEmpty(message = "{genre.validation.name.not-empty}")
    private String name;

    @Size(max = 4096, message = "{genre.validation.description.size}")
    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long version;

    /**
     * Used for comparison between two {@link GenreDto} objects. It's used
     * internally whenever a {@link GenreDto} is the type within a list which
     * supported inserted sort, such as a {@link java.util.TreeSet}.
     *
     * The order in which {@link GenreDto} instances are sorted are by {@link GenreDto#name}
     * and {@link GenreDto#id}.
     *
     * @param other The other {@link GenreDto} instance to compare against.
     *
     * @return a negative integer, zero, or a positive integer as this {@link GenreDto}
     *         is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(GenreDto other) {
        return ComparisonChain.start()
                .compare(name, other.name, Ordering.natural().nullsLast())
                .compare(id, other.id)
                .result();
    }
}
