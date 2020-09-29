package com.sparkystudios.traklibrary.game.domain;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "platform_release_date")
public class PlatformReleaseDate implements Comparable<PlatformReleaseDate> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private long id;

    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_id")
    private Platform platform;

    @Column(name = "region", nullable = false)
    private GameRegion region;

    @Column(name = "release_date")
    private LocalDate releaseDate;

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
     * Used for comparison between two {@link PlatformReleaseDate} objects. It's used
     * internally whenever a {@link PlatformReleaseDate} is the type within a list which
     * supported inserted sort, such as a {@link java.util.TreeSet}.
     *
     * The order in which {@link PlatformReleaseDate} instances are sorted are by {@link PlatformReleaseDate#region}.
     *
     * @param other The other {@link PlatformReleaseDate} instance to compare against.
     *
     * @return  a negative integer, zero, or a positive integer as this {@link PlatformReleaseDate}
     *          is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(PlatformReleaseDate other) {
        return ComparisonChain.start()
                .compare(region != null ? region.getId() : null, other.region != null ? other.region.getId() : null, Ordering.natural().nullsLast())
                .result();
    }
}
