package com.traklibrary.game.repository;

import com.traklibrary.game.domain.GameImage;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface GameImageRepository extends PagingAndSortingRepository<GameImage, Long> {

    boolean existsByGameId(long gameId);

    Optional<GameImage> findByGameId(long gameId);
}
