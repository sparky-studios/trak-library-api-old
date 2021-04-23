package com.sparkystudios.traklibrary.game.service;

import com.sparkystudios.traklibrary.game.domain.Game;
import com.sparkystudios.traklibrary.game.domain.Genre;
import com.sparkystudios.traklibrary.game.repository.specification.GameSpecification;
import com.sparkystudios.traklibrary.game.service.dto.GameDetailsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GameDetailsService {

    /**
     * Given an ID of a {@link Game} entity, this service method will query the underlying persistence layer and try and
     * retrieve the {@link Game} entity that matches the given ID and map it to a {@link GameDetailsDto}, which contains information
     * about the game as well as a small amount of additional information.. If the Id provided does not
     * map to any known {@link Game} entity, then an exception will be thrown specifying that it can't be found.
     *
     * @param gameId The ID of the {@link Game} entity to try and retrieve.
     *
     * @return The {@link Game} entity matching the ID mapped to a {@link GameDetailsDto}.
     */
    GameDetailsDto findByGameId(long gameId);

    /**
     * Given an slug of a {@link Game} entity, this service method will query the underlying persistence layer and try and
     * retrieve the {@link Game} entity that matches the given slug and map it to a {@link GameDetailsDto}, which contains information
     * about the game as well as a small amount of additional information.. If the slug provided does not
     * map to any known {@link Game} entity, then an exception will be thrown specifying that it can't be found.
     *
     * @param slug The slug of the {@link Game} entity to try and retrieve.
     *
     * @return The {@link Game} entity matching the slug mapped to a {@link GameDetailsDto}.
     */
    GameDetailsDto findByGameSlug(String slug);

    /**
     * Given the ID of a {@link Genre}, this method will retrieve a {@link Page} of {@link GameDetailsDto}s that are associated with
     * the given {@link Genre}. If the ID provided does not map to any {@link Genre}, a {@link javax.persistence.EntityNotFoundException}
     * will be thrown. If there are no {@link GameDetailsDto}s associated with the {@link Genre}, an empty {@link Iterable} will be returned.
     *
     * @param genreId The ID of the {@link Genre} to retrieve {@link GameDetailsDto} entries for.
     * @param pageable The amount of data and the page to return.
     *
     * @return A page of {@link GameDetailsDto}s that are mapped with the given {@link Genre}.
     */
    Iterable<GameDetailsDto> findByGenreId(long genreId, Pageable pageable);

    /**
     * Given the ID of a {@link Genre}, this method will retrieve the total count for how many {@link GameDetailsDto}'s have an association
     * to the given {@link Genre}. If the ID provided does not map to any {@link Genre}, {@link javax.persistence.EntityNotFoundException}
     * will be thrown.
     *
     * @param genreId The ID of the {@link Genre} to retrieve the total count of {@link GameDetailsDto} entries for.
     *
     * @return The total count of {@link GameDetailsDto}'s associated with the given {@link Genre}.
     */
    long countByGenreId(long genreId);

    /**
     * This method will retrieve an {@link Iterable} of {@link GameDetailsDto} with a response size specified by the {@link Pageable}. The
     * results can be queried and filtered by utilising the exposed specifications on the {@link GameSpecification} object. If the response
     * from the specifications is that none match, an empty {@link Iterable} will be returned.
     *
     * The {@link GameSpecification} argument can be omitted and is optional, however if the callee provides <code>null</code> for the
     * {@link Pageable}, an exception will be thrown.
     *
     * @param gameSpecification The {@link GameSpecification} to filter the query by.
     * @param pageable The size and page of data to return.
     *
     * @return An {@link Iterable} of relevant queried {@link GameDetailsDto} instances.
     */
    Iterable<GameDetailsDto> findAll(GameSpecification gameSpecification, Pageable pageable);

    /**
     * Retrieves the total number of rows that match the criteria specified within the {@link GameSpecification}. The specification
     * provided must be a valid instance, if <code>null</code> is provided, a {@link NullPointerException} will be thrown to the callee.
     *
     * @param gameSpecification The {@link GameSpecification} criteria to count the results for.
     *
     * @return The total number of rows that matches the given criteria.
     */
    long count(GameSpecification gameSpecification);
}
