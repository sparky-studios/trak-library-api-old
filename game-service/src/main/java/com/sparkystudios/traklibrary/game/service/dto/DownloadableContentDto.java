package com.sparkystudios.traklibrary.game.service.dto;

import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Relation(collectionRelation = "data", itemRelation = "dlc")
public class DownloadableContentDto implements Comparable<DownloadableContentDto> {

    private long id;

    private long gameId;

    @NotEmpty(message = "{downloadable-content.validation.title.not-empty}")
    @Size(max = 255, message = "{downloadable-content.validation.title.size}")
    private String name;

    @Size(max = 4096, message = "{downloadable-content.validation.description.size}")
    private String description;

    private LocalDate releaseDate;

    private String slug;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long version;

    /**
     * Used for comparison between two {@link DownloadableContentDto} objects. It's used
     * internally whenever a {@link DownloadableContentDto} is the type within a list which
     * supported inserted sort, such as a {@link java.util.TreeSet}.
     *
     * The order in which {@link DownloadableContentDto} instances are sorted are by {@link DownloadableContentDto#name}
     * and {@link DownloadableContentDto#id}.
     *
     * @param other The other {@link DownloadableContentDto} instance to compare against.
     *
     * @return a negative integer, zero, or a positive integer as this {@link DownloadableContentDto}
     *         is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(DownloadableContentDto other) {
        return ComparisonChain.start()
                .compare(Strings.nullToEmpty(name), Strings.nullToEmpty(other.name))
                .compare(id, other.id)
                .result();
    }
}
