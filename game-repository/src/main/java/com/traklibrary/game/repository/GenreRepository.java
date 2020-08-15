package com.traklibrary.game.repository;

import com.traklibrary.game.domain.Genre;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GenreRepository extends PagingAndSortingRepository<Genre, Long>, JpaSpecificationExecutor<Genre> {
}
