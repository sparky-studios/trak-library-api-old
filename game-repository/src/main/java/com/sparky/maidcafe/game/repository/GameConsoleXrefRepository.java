package com.sparky.maidcafe.game.repository;

import com.sparky.maidcafe.game.domain.GameConsoleXref;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GameConsoleXrefRepository extends PagingAndSortingRepository<GameConsoleXref, Long>, JpaSpecificationExecutor<GameConsoleXref> {
}
