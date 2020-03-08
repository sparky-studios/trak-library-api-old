package com.sparky.maidcafe.game.repository;

import com.sparky.maidcafe.game.domain.GameGenreXref;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GameGenreXrefRepository extends PagingAndSortingRepository<GameGenreXref, Long>, JpaSpecificationExecutor<GameGenreXref> {
}
