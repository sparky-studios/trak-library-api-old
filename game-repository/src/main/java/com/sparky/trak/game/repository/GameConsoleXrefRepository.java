package com.sparky.trak.game.repository;

import com.sparky.trak.game.domain.GameConsoleXref;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GameConsoleXrefRepository extends PagingAndSortingRepository<GameConsoleXref, Long>, JpaSpecificationExecutor<GameConsoleXref> {
}
