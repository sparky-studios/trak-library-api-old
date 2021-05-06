package com.sparkystudios.traklibrary.game.service.dto;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.sparkystudios.traklibrary.game.domain.AgeRatingClassification;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Relation(collectionRelation = "data", itemRelation = "ageRatings")
public class AgeRatingDto implements Comparable<AgeRatingDto> {

    private long id;

    @NotNull(message = "{age-rating.validation.classification.not-null}")
    private AgeRatingClassification classification;

    private short rating;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long version;

    /**
     * Used for comparison between two {@link AgeRatingDto} objects. It's used
     * internally whenever a {@link AgeRatingDto} is the type within a list which
     * supported inserted sort, such as a {@link java.util.TreeSet}.
     *
     * The order in which {@link AgeRatingDto} instances are sorted are by {@link AgeRatingDto#classification}.
     *
     * @param other The other {@link AgeRatingDto} instance to compare against.
     *
     * @return  a negative integer, zero, or a positive integer as this {@link AgeRatingDto}
     *          is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(AgeRatingDto other) {
        return ComparisonChain.start()
                .compare(classification != null ? classification.getId() : null, other.classification != null ? other.classification.getId() : null, Ordering.natural().nullsLast())
                .result();
    }
}
