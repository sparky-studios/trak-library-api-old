package com.traklibrary.game.repository;

import com.traklibrary.game.domain.GamePlatformXref;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GamePlatformXrefRepository extends PagingAndSortingRepository<GamePlatformXref, Long>, JpaSpecificationExecutor<GamePlatformXref> {
}
