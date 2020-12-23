package com.sparkystudios.traklibrary.game.repository.specification;

import com.sparkystudios.traklibrary.game.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@RequiredArgsConstructor
public class GameSearchSpecification implements Specification<Game> {

    private transient final Set<Platform> platforms;
    private transient final Set<Genre> genres;
    private final Set<GameMode> gameModes;
    private final Set<AgeRating> ageRatings;

    @Override
    public Predicate toPredicate(@NonNull Root<Game> root, @NonNull CriteriaQuery<?> criteriaQuery, @NonNull CriteriaBuilder criteriaBuilder) {
        Collection<Predicate> predicates = new ArrayList<>();

        if (platforms != null) {
            for (Platform platform : platforms) {
                predicates.add(criteriaBuilder.or(criteriaBuilder.isMember(platform, root.get(Game_.platforms))));
            }
        }

        if (genres != null) {
            for (Genre genre : genres) {
                predicates.add(criteriaBuilder.or(criteriaBuilder.isMember(genre, root.get(Game_.genres))));
            }
        }

        if (gameModes != null) {
            for (GameMode gameMode : gameModes) {
                predicates.add(criteriaBuilder.or(criteriaBuilder.isMember(gameMode, root.get(Game_.gameModes))));
            }
        }

        if (ageRatings != null) {
            CriteriaBuilder.In<AgeRating> ageRatingInClause = criteriaBuilder.in(root.get(Game_.ageRating));
            ageRatings.forEach(ageRatingInClause::value);

            predicates.add(ageRatingInClause);
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
