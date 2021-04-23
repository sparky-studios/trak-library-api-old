package com.sparkystudios.traklibrary.game.repository;

import com.sparkystudios.traklibrary.game.domain.Publisher;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PublisherRepository extends PagingAndSortingRepository<Publisher, Long>, JpaSpecificationExecutor<Publisher> {

    Optional<Publisher> findBySlug(String slug);
}

