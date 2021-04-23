package com.sparkystudios.traklibrary.game.repository;

import com.sparkystudios.traklibrary.game.domain.DownloadableContent;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface DownloadableContentRepository extends PagingAndSortingRepository<DownloadableContent, Long>, JpaSpecificationExecutor<DownloadableContent> {

    Optional<DownloadableContent> findBySlug(String slug);
}
