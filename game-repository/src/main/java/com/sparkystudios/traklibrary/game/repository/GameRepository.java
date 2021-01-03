package com.sparkystudios.traklibrary.game.repository;

import com.sparkystudios.traklibrary.game.domain.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends PagingAndSortingRepository<Game, Long>, JpaSpecificationExecutor<Game> {

    Page<Game> findByDevelopersId(long developerId, Pageable pageable);

    long countByDevelopersId(long developerId);

    Page<Game> findByFranchiseId(long franchise, Pageable pageable);

    long countByFranchiseId(long franchiseId);

    Page<Game> findByPublishersId(long publisherId, Pageable pageable);

    long countByPublishersId(long publisherId);

    Page<Game> findByGenresId(long genreId, Pageable pageable);

    long countByGenresId(long genreId);

    Page<Game> findByPlatformsId(long platformId, Pageable pageable);

    long countByPlatformsId(long platformId);
}
