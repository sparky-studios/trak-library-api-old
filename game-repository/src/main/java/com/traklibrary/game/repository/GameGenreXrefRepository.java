package com.traklibrary.game.repository;

import com.traklibrary.game.domain.GameGenreXref;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GameGenreXrefRepository extends PagingAndSortingRepository<GameGenreXref, Long>, JpaSpecificationExecutor<GameGenreXref> {
}
