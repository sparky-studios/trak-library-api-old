package com.sparkystudios.traklibrary.game.repository;

import com.sparkystudios.traklibrary.game.domain.Developer;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeveloperRepository extends PagingAndSortingRepository<Developer, Long>, JpaSpecificationExecutor<Developer> {

    Optional<Developer> findBySlug(String slug);
}

