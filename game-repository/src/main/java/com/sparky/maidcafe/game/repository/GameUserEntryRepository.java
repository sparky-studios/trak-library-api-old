package com.sparky.maidcafe.game.repository;

import com.sparky.maidcafe.game.domain.GameUserEntry;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GameUserEntryRepository extends PagingAndSortingRepository<GameUserEntry, Long>, JpaSpecificationExecutor<GameUserEntry> {
}
