package com.sparky.trak.game.repository;

import com.sparky.trak.game.domain.Developer;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DeveloperRepository extends PagingAndSortingRepository<Developer, Long>, JpaSpecificationExecutor<Developer> {
}

