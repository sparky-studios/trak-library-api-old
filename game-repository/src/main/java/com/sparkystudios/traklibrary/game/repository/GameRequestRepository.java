package com.sparkystudios.traklibrary.game.repository;

import com.sparkystudios.traklibrary.game.domain.GameRequest;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRequestRepository extends PagingAndSortingRepository<GameRequest, Long>, JpaSpecificationExecutor<GameRequest> {
}
