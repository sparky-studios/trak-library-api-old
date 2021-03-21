package com.sparkystudios.traklibrary.game.repository;

import com.sparkystudios.traklibrary.game.domain.GameImage;
import com.sparkystudios.traklibrary.game.domain.GameImageSize;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameImageRepository extends PagingAndSortingRepository<GameImage, Long> {

    boolean existsByGameIdAndImageSize(long gameId, GameImageSize gameImageSize);

    Optional<GameImage> findByGameIdAndImageSize(long gameId, GameImageSize gameImageSize);
}
