package com.traklibrary.game.repository;

import com.traklibrary.game.domain.GameDeveloperXref;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GameDeveloperXrefRepository extends PagingAndSortingRepository<GameDeveloperXref, Long>, JpaSpecificationExecutor<GameDeveloperXref> {
}
