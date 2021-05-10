package com.sparkystudios.traklibrary.game.repository;

import com.sparkystudios.traklibrary.game.domain.GameImage;
import com.sparkystudios.traklibrary.game.domain.ImageSize;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameImageRepository extends PagingAndSortingRepository<GameImage, Long> {

    boolean existsByGameIdAndImageSize(long gameId, ImageSize imageSize);

    Optional<GameImage> findByGameIdAndImageSize(long gameId, ImageSize imageSize);
}
