package com.sparkystudios.traklibrary.game.repository;

import com.sparkystudios.traklibrary.game.domain.Franchise;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FranchiseRepository extends PagingAndSortingRepository<Franchise, Long>, JpaSpecificationExecutor<Franchise> {

    Optional<Franchise> findBySlug(String slug);
}
