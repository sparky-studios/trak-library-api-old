package com.sparkystudios.traklibrary.game.domain;

import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "dlc")
public class DownloadableContent implements Comparable<DownloadableContent> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private long id;

    @Column(name = "game_id", insertable = false, updatable = false)
    private long gameId;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false, length = 4096)
    private String description;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "op_lock_version")
    private Long version;

    /**
     * Used for comparison between two {@link DownloadableContent} objects. It's used
     * internally whenever a {@link DownloadableContent} is the type within a list which
     * supported inserted sort, such as a {@link java.util.TreeSet}.
     *
     * The order in which {@link DownloadableContent} instances are sorted are by {@link DownloadableContent#name}
     * and then {@link DownloadableContent#id}.
     *
     * @param other The other {@link DownloadableContent} instance to compare against.
     *
     * @return  a negative integer, zero, or a positive integer as this {@link DownloadableContent}
     *          is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(DownloadableContent other) {
        return ComparisonChain.start()
                .compare(Strings.nullToEmpty(name), Strings.nullToEmpty(other.name))
                .compare(id, other.id)
                .result();
    }
}
