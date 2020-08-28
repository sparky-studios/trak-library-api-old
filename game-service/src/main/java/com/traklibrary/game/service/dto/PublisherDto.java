package com.traklibrary.game.service.dto;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.server.core.Relation;

@EqualsAndHashCode(callSuper = true)
@Data
@Relation(collectionRelation = "data", itemRelation = "publisher")
public class PublisherDto extends CompanyDto implements Comparable<PublisherDto> {

    /**
     * Used for comparison between two {@link PublisherDto} objects. It's used
     * internally whenever a {@link PublisherDto} is the type within a list which
     * supported inserted sort, such as a {@link java.util.TreeSet}.
     *
     * The order in which {@link PlatformDto} instances are sorted are by {@link PublisherDto#getName()},
     * {@link PublisherDto#getFoundedDate()} and finally {@link PublisherDto#getId()}.
     *
     * @param other The other {@link PublisherDto} instance to compare against.
     *
     * @return A negative integer, zero, or a positive integer as this {@link PublisherDto}
     *         is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(PublisherDto other) {
        return ComparisonChain.start()
                .compare(getName(), other.getName(), Ordering.natural().nullsLast())
                .compare(getFoundedDate(), other.getFoundedDate(), Ordering.natural().nullsLast())
                .compare(getId(), other.getId())
                .result();
    }
}
