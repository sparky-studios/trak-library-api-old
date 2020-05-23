package com.sparky.trak.game.repository;

import com.sparky.trak.game.domain.GameImage;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface GameImageRepository extends PagingAndSortingRepository<GameImage, Long> {

    boolean existsByGameId(long gameId);

    Optional<GameImage> findByGameId(long gameId);
}
