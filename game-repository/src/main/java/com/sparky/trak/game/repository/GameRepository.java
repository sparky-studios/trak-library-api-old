package com.sparky.trak.game.repository;

import com.sparky.trak.game.domain.Game;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GameRepository extends PagingAndSortingRepository<Game, Long>, JpaSpecificationExecutor<Game> {
}
