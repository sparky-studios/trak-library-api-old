package com.sparkystudios.traklibrary.game.domain;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
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
import java.time.LocalDateTime;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "age_rating")
public class AgeRating implements Comparable<AgeRating> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private long id;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @Column(name = "classification", nullable = false)
    private AgeRatingClassification classification;

    @Column(name = "rating", nullable = false)
    private short rating;

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
     * Used for comparison between two {@link AgeRating} objects. It's used
     * internally whenever a {@link AgeRating} is the type within a list which
     * supported inserted sort, such as a {@link java.util.TreeSet}.
     *
     * The order in which {@link AgeRating} instances are sorted are by {@link AgeRating#classification}.
     *
     * @param other The other {@link GameReleaseDate} instance to compare against.
     *
     * @return  a negative integer, zero, or a positive integer as this {@link AgeRating}
     *          is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(AgeRating other) {
        return ComparisonChain.start()
                .compare(classification != null ? classification.getId() : null, other.classification != null ? other.classification.getId() : null, Ordering.natural().nullsLast())
                .result();
    }
}
