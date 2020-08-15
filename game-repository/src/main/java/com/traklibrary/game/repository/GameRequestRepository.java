package com.traklibrary.game.repository;

import com.traklibrary.game.domain.GameRequest;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GameRequestRepository extends PagingAndSortingRepository<GameRequest, Long>, JpaSpecificationExecutor<GameRequest> {
}
