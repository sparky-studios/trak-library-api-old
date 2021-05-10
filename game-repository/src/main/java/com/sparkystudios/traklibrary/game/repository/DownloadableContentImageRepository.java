package com.sparkystudios.traklibrary.game.repository;

import com.sparkystudios.traklibrary.game.domain.DownloadableContentImage;
import com.sparkystudios.traklibrary.game.domain.ImageSize;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DownloadableContentImageRepository extends CrudRepository<DownloadableContentImage, Long> {

    boolean existsByDownloadableContentIdAndImageSize(long downloadableContentId, ImageSize imageSize);

    Optional<DownloadableContentImage> findByDownloadableContentIdAndImageSize(long downloadableContentId, ImageSize imageSize);
}
