package com.sparky.maidcafe.game.repository;

import com.sparky.maidcafe.game.domain.Game;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GameRepository extends PagingAndSortingRepository<Game, Long>, JpaSpecificationExecutor<Game> {
}
