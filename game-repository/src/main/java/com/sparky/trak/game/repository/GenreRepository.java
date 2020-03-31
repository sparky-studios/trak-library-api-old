package com.sparky.trak.game.repository;

import com.sparky.trak.game.domain.Genre;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GenreRepository extends PagingAndSortingRepository<Genre, Long>, JpaSpecificationExecutor<Genre> {
}
