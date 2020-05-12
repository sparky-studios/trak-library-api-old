package com.sparky.trak.game.repository;

import com.sparky.trak.game.domain.Platform;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PlatformRepository extends PagingAndSortingRepository<Platform, Long>, JpaSpecificationExecutor<Platform> {
}
