package com.sparkystudios.traklibrary.game.repository.specification;

import com.sparkystudios.traklibrary.game.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@RequiredArgsConstructor
public class GameUserEntrySearchSpecification implements Specification<GameUserEntry> {

    private final Set<Platform> platforms;
    private final Set<Genre> genres;
    private final Set<GameMode> gameModes;
    private final Set<AgeRating> ageRatings;
    private final Set<GameUserEntryStatus> statuses;

    @Override
    public Predicate toPredicate(@NonNull Root<GameUserEntry> root, @NonNull CriteriaQuery<?> criteriaQuery, @NonNull CriteriaBuilder criteriaBuilder) {
        Collection<Predicate> predicates = new ArrayList<>();

        if (platforms != null && !platforms.isEmpty()) {
            Subquery<GameUserEntryPlatform> subquery = criteriaQuery.subquery(GameUserEntryPlatform.class);
            Root<GameUserEntryPlatform> subqueryRoot = subquery.from(GameUserEntryPlatform.class);

            Predicate idPredicate = criteriaBuilder.equal(subqueryRoot.get(GameUserEntryPlatform_.gameUserEntryId), root.get(GameUserEntry_.id));
            CriteriaBuilder.In<Platform> inPredicate = criteriaBuilder.in(subqueryRoot.get(GameUserEntryPlatform_.platform));

            for (Platform platform : platforms) {
                inPredicate.value(platform);
            }

            subquery
                    .select(subqueryRoot)
                    .where(idPredicate, inPredicate);

            predicates.add(criteriaBuilder.exists(subquery));
        }

        if (genres != null && !genres.isEmpty()) {
            Subquery<Game> subquery = criteriaQuery.subquery(Game.class);
            Root<Game> subqueryRoot = subquery.from(Game.class);

            Predicate idPredicate = criteriaBuilder.equal(subqueryRoot.get(Game_.id), root.get(GameUserEntry_.gameId));
            Collection<Predicate> genrePredicates = new ArrayList<>();
            genrePredicates.add(idPredicate);

            for (Genre genre : genres) {
                genrePredicates.add(criteriaBuilder.or(criteriaBuilder.isMember(genre, subqueryRoot.get(Game_.genres))));
            }

            subquery
                    .select(subqueryRoot)
                    .where(genrePredicates.toArray(new Predicate[0]));

            predicates.add(criteriaBuilder.exists(subquery));
        }

        if (gameModes != null && !gameModes.isEmpty()) {
            Subquery<Game> subquery = criteriaQuery.subquery(Game.class);
            Root<Game> subqueryRoot = subquery.from(Game.class);

            Predicate idPredicate = criteriaBuilder.equal(subqueryRoot.get(Game_.id), root.get(GameUserEntry_.gameId));
            Collection<Predicate> gameModePredicates = new ArrayList<>();
            gameModePredicates.add(idPredicate);

            for (GameMode gameMode : gameModes) {
                gameModePredicates.add(criteriaBuilder.or(criteriaBuilder.isMember(gameMode, subqueryRoot.get(Game_.gameModes))));
            }

            subquery
                    .select(subqueryRoot)
                    .where(gameModePredicates.toArray(new Predicate[0]));

            predicates.add(criteriaBuilder.exists(subquery));
        }

        if (ageRatings != null && !ageRatings.isEmpty()) {
            Subquery<Game> subquery = criteriaQuery.subquery(Game.class);
            Root<Game> subqueryRoot = subquery.from(Game.class);

            CriteriaBuilder.In<AgeRating> ageRatingInClause = criteriaBuilder.in(subqueryRoot.get(Game_.ageRating));
            ageRatings.forEach(ageRatingInClause::value);

            Predicate idPredicate = criteriaBuilder.equal(subqueryRoot.get(Game_.id), root.get(GameUserEntry_.gameId));

            subquery
                    .select(subqueryRoot)
                    .where(idPredicate, ageRatingInClause);

            predicates.add(criteriaBuilder.exists(subquery));
        }

        if (statuses != null && !statuses.isEmpty()) {
            CriteriaBuilder.In<GameUserEntryStatus> statusInClause = criteriaBuilder.in(root.get(GameUserEntry_.status));
            statuses.forEach(statusInClause::value);

            predicates.add(statusInClause);
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
