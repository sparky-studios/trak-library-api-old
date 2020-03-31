package com.sparky.trak.game.repository;

import com.sparky.trak.game.domain.Console;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ConsoleRepository extends PagingAndSortingRepository<Console, Long>, JpaSpecificationExecutor<Console> {
}
