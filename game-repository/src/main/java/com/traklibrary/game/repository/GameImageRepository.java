package com.traklibrary.game.repository;

import com.traklibrary.game.domain.GameImage;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameImageRepository extends PagingAndSortingRepository<GameImage, Long> {

    boolean existsByGameId(long gameId);

    Optional<GameImage> findByGameId(long gameId);
}
