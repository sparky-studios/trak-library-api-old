package com.sparkystudios.traklibrary.game.service.dto;

import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;

@Data
@Relation(collectionRelation = "data", itemRelation = "game-user-entry-dlc")
public class GameUserEntryDownloadableContentDto implements Comparable<GameUserEntryDownloadableContentDto> {

    private long id;

    private long gameUserEntryId;

    private long downloadableContentId;

    private String downloadableContentName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long version;

    /**
     * Used for comparison between two {@link GameUserEntryDownloadableContentDto} objects. It's used
     * internally whenever a {@link GameUserEntryDownloadableContentDto} is the type within a list which
     * supported inserted sort, such as a {@link java.util.TreeSet}.
     * <p>
     * The order in which {@link GameUserEntryDownloadableContentDto} instances are sorted are by {@link GameUserEntryDownloadableContentDto#downloadableContentName},
     * and then {@link GameUserEntryDownloadableContentDto#id}.
     *
     * @param other The other {@link GameUserEntryDownloadableContentDto} instance to compare against.
     * @return a negative integer, zero, or a positive integer as this {@link GameUserEntryDownloadableContentDto}
     * is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(GameUserEntryDownloadableContentDto other) {
        return ComparisonChain.start()
                .compare(Strings.nullToEmpty(downloadableContentName), Strings.nullToEmpty(other.downloadableContentName))
                .compare(id, other.id)
                .result();
    }
}