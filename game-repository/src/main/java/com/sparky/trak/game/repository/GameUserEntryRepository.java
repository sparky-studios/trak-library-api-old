package com.sparky.trak.game.repository;

import com.sparky.trak.game.domain.GameUserEntry;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GameUserEntryRepository extends PagingAndSortingRepository<GameUserEntry, Long>, JpaSpecificationExecutor<GameUserEntry> {
}
