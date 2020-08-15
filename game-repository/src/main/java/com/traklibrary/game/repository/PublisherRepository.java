package com.traklibrary.game.repository;

import com.traklibrary.game.domain.Publisher;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PublisherRepository extends PagingAndSortingRepository<Publisher, Long>, JpaSpecificationExecutor<Publisher> {
}

