package com.traklibrary.game.repository;

import com.traklibrary.game.domain.Developer;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DeveloperRepository extends PagingAndSortingRepository<Developer, Long>, JpaSpecificationExecutor<Developer> {
}

