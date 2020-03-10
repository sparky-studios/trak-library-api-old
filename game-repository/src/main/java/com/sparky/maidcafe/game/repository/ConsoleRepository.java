package com.sparky.maidcafe.game.repository;

import com.sparky.maidcafe.game.domain.Console;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ConsoleRepository extends PagingAndSortingRepository<Console, Long>, JpaSpecificationExecutor<Console> {
}
