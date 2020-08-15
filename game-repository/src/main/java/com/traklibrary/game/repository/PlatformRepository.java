package com.traklibrary.game.repository;

import com.traklibrary.game.domain.Platform;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PlatformRepository extends PagingAndSortingRepository<Platform, Long>, JpaSpecificationExecutor<Platform> {
}
