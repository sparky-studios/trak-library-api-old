package com.sparkystudios.traklibrary.game.repository;

import com.sparkystudios.traklibrary.game.domain.GameReleaseDate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameReleaseDateRepository extends CrudRepository<GameReleaseDate, Long> {
}
