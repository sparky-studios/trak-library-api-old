package com.sparkystudios.traklibrary.game.service;

import com.sparkystudios.traklibrary.game.domain.Game;
import com.sparkystudios.traklibrary.game.domain.Genre;
import com.sparkystudios.traklibrary.game.repository.specification.GameSpecification;
import com.sparkystudios.traklibrary.game.service.dto.GameInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GameInfoService {

    /**
     * Given an ID of a {@link Game} entity, this service method will query the underlying persistence layer and try and
     * retrieve the {@link Game} entity that matches the given ID and map it to a {@link GameInfoDto}, which contains information
     * about the game as well as a small amount of additional information.. If the Id provided does not
     * map to any known {@link Game} entity, then an exception will be thrown specifying that it can't be found.
     *
     * @param gameId The ID of the {@link Game} entity to try and retrieve.
     *
     * @return The {@link Game} entity matching the ID mapped to a {@link GameInfoDto}.
     */
    GameInfoDto findByGameId(long gameId);

    /**
     * Given the ID of a {@link Genre}, this method will retrieve a {@link Page} of {@link GameInfoDto}s that are associated with
     * the given {@link Genre}. If the ID provided does not map to any {@link Genre}, a {@link javax.persistence.EntityNotFoundException}
     * will be thrown. If there are no {@link GameInfoDto}s associated with the {@link Genre}, an empty {@link Iterable} will be returned.
     *
     * @param genreId The ID of the {@link Genre} to retrieve {@link GameInfoDto} entries for.
     * @param pageable The amount of data and the page to return.
     *
     * @return A page of {@link GameInfoDto}s that are mapped with the given {@link Genre}.
     */
    Iterable<GameInfoDto> findByGenreId(long genreId, Pageable pageable);

    /**
     * Given the ID of a {@link Genre}, this method will retrieve the total count for how many {@link GameInfoDto}'s have an association
     * to the given {@link Genre}. If the ID provided does not map to any {@link Genre}, {@link javax.persistence.EntityNotFoundException}
     * will be thrown.
     *
     * @param genreId The ID of the {@link Genre} to retrieve the total count of {@link GameInfoDto} entries for.
     *
     * @return The total count of {@link GameInfoDto}'s associated with the given {@link Genre}.
     */
    long countByGenreId(long genreId);

    /**
     * This method will retrieve an {@link Iterable} of {@link GameInfoDto} with a response size specified by the {@link Pageable}. The
     * results can be queried and filtered by utilising the exposed specifications on the {@link GameSpecification} object. If the response
     * from the specifications is that none match, an empty {@link Iterable} will be returned.
     *
     * The {@link GameSpecification} argument can be omitted and is optional, however if the callee provides <code>null</code> for the
     * {@link Pageable}, an exception will be thrown.
     *
     * @param gameSpecification The {@link GameSpecification} to filter the query by.
     * @param pageable The size and page of data to return.
     *
     * @return An {@link Iterable} of relevant queried {@link GameInfoDto} instances.
     */
    Iterable<GameInfoDto> findAll(GameSpecification gameSpecification, Pageable pageable);

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
