package com.sparkystudios.traklibrary.game.service;

import com.sparkystudios.traklibrary.game.domain.Game;
import com.sparkystudios.traklibrary.game.domain.GameUserEntry;
import com.sparkystudios.traklibrary.game.repository.specification.GameUserEntrySpecification;
import com.sparkystudios.traklibrary.game.service.dto.GameUserEntryDto;
import com.sparkystudios.traklibrary.game.service.dto.request.GameUserEntryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GameUserEntryService {

    /**
     * Given a {@link GameUserEntryRequest} instance, the service will attempt to persist the data with the underlying persistence layer.
     * If the {@link GameUserEntryRequest} supplied contains an Id that matches an existing entity, insertion to the persistence layer will
     * fail and a {@link javax.persistence.EntityExistsException} will be thrown. If persistence succeeds, the data is saved and the
     * saved entity is returned as a {@link GameUserEntryDto}.
     *
     * The method does not allow <code>null</code> entities to be inserted, if null is provided, a {@link NullPointerException}
     * will be thrown.
     *
     * @param gameUserEntryRequest The {@link GameUserEntryRequest} instance to persist.
     *
     * @return The updated persisted entity as a {@link GameUserEntryDto}.
     *
     * @throws NullPointerException Thrown if the argument provided is null.
     */
    GameUserEntryDto save(GameUserEntryRequest gameUserEntryRequest);

    /**
     * Given an ID of a {@link GameUserEntry} entity, this service method will query the underlying persistence layer and try and
     * retrieve the {@link GameUserEntry} entity that matches the given ID and map it to a {@link GameUserEntryDto}. If the ID
     * provided does not map to any known {@link GameUserEntry} entity, then an exception will be thrown specifying that it can't be
     * found.
     *
     * @param id The ID of the {@link GameUserEntry} entity to try and retrieve.
     *
     * @return The {@link GameUserEntry} entity matching the ID mapped to a {@link GameUserEntryDto}.
     *
     * @throws javax.persistence.EntityNotFoundException Thrown if the ID doesn't match an existing {@link GameUserEntry}.
     */
    GameUserEntryDto findById(long id);

    /**
     * Given the ID of a {@link Game}, this method will retrieve a {@link Page} of {@link GameUserEntry}'s that are associated with
     * the given {@link Game}. If the ID provided does not map to any {@link Game}, a {@link javax.persistence.EntityNotFoundException}
     * will be thrown. If there are no {@link GameUserEntry}'s associated with the {@link Game}, an empty {@link Iterable} will be returned.
     * The query can be additionally filtered by providing arguments to the {@link GameUserEntrySpecification} specification object.
     *
     * @param gameId The ID of the {@link Game} to retrieve {@link GameUserEntry} entries for.
     * @param gameUserEntrySpecification A {@link GameUserEntrySpecification} used to additionally filter the results.
     * @param pageable The amount of data and the page to return.
     *
     * @return A page of {@link GameUserEntryDto}s that are mapped with the given {@link Game}.
     */
    Iterable<GameUserEntryDto> findGameUserEntriesByGameId(long gameId, GameUserEntrySpecification gameUserEntrySpecification, Pageable pageable);

    /**
     * Given the ID of a {@link Game}, this method will retrieve the total count for how many {@link GameUserEntryDto}'s have an association
     * to the given {@link Game}. If the ID provided does not map to any {@link Game}, {@link javax.persistence.EntityNotFoundException}
     * will be thrown.
     *
     * @param gameId The ID of the {@link Game} to retrieve the total count of {@link GameUserEntryDto} entries for.
     *
     * @return The total count of {@link GameUserEntryDto}'s associated with the given {@link Game}.
     */
    long countGameUserEntriesByGameId(long gameId);

    /**
     * This method will retrieve an {@link Iterable} of {@link GameUserEntryDto} with a response size specified by the {@link Pageable}. The
     * results can be queried and filtered by utilising the exposed specifications on the {@link GameUserEntrySpecification} object. If the
     * response from the specifications is that none match, an empty {@link Iterable} will be returned.
     *
     * The {@link GameUserEntrySpecification} argument can be omitted and is optional, however if the callee provides <code>null</code> for the
     * {@link Pageable}, an exception will be thrown.
     *
     * @param gameUserEntrySpecification The {@link GameUserEntrySpecification} to filter the query by.
     * @param pageable The size and page of data to return.
     *
     * @return An {@link Iterable} of relevant queried {@link GameUserEntryDto} instances.
     */
    Iterable<GameUserEntryDto> findAll(GameUserEntrySpecification gameUserEntrySpecification, Pageable pageable);

    /**
     * Retrieves the total number of rows that match the criteria specified within the {@link GameUserEntrySpecification}. The specification
     * provided must be a valid instance, if <code>null</code> is provided, a {@link NullPointerException} will be thrown to the callee.
     *
     * @param gameUserEntrySpecification The {@link GameUserEntrySpecification} criteria to count the results for.
     *
     * @return The total number of rows that matches the given criteria.
     */
    long count(GameUserEntrySpecification gameUserEntrySpecification);

    /**
     * Given a {@link GameUserEntryRequest} instance, the service will attempt to the update the persisted data which matches the given {@link GameUserEntryRequest}
     * in the underlying persistence layer. If the {@link GameUserEntryDto} supplied contains an ID that doesn't match any existing entities, then
     * the update will fail and a {@link javax.persistence.EntityNotFoundException} will be thrown. If persistence succeeds, the relevant
     * record is updated and the updated entity is returned as a {@link GameUserEntryDto}.
     *
     * The method does not allow <code>null</code> entities to be inserted, if null is provided, a {@link NullPointerException}
     * will be thrown.
     *
     * @param gameUserEntryRequest The {@link GameUserEntryRequest} instance to update.
     *
     * @return The updated persisted entity as a {@link GameUserEntryDto}.
     *
     * @throws NullPointerException Thrown if the {@link GameUserEntryRequest} argument provided is null.
     */
    GameUserEntryDto update(GameUserEntryRequest gameUserEntryRequest);

    /**
     * Deletes the persisted entity that is mapped to the given ID. If the service cannot find a {@link GameUserEntryDto} that is mapped to the ID,
     * then deletion will not occur and a {@link javax.persistence.EntityNotFoundException} exception will be thrown. Deletion can be
     * classes as successful if the method completes without throwing additional errors.
     *
     * @param id The ID of the {@link GameUserEntryDto} to delete.
     *
     * @throws javax.persistence.EntityNotFoundException Thrown if the ID doesn't map to an existing {@link GameUserEntry} entity.
     */
    void deleteById(long id);
}
