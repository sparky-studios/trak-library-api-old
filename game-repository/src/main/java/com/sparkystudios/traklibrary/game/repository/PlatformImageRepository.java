package com.sparkystudios.traklibrary.game.repository;

import com.sparkystudios.traklibrary.game.domain.PlatformImage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlatformImageRepository extends CrudRepository<PlatformImage, Long> {

    boolean existsByPlatformId(long platformId);

    Optional<PlatformImage> findByPlatformId(long platformId);
}
