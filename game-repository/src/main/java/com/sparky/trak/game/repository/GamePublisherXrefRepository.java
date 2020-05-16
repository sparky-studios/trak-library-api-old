package com.sparky.trak.game.repository;

import com.sparky.trak.game.domain.GamePublisherXref;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GamePublisherXrefRepository extends PagingAndSortingRepository<GamePublisherXref, Long>, JpaSpecificationExecutor<GamePublisherXref> {
}
