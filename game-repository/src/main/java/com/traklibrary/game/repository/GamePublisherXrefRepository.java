package com.traklibrary.game.repository;

import com.traklibrary.game.domain.GamePublisherXref;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GamePublisherXrefRepository extends PagingAndSortingRepository<GamePublisherXref, Long>, JpaSpecificationExecutor<GamePublisherXref> {
}
