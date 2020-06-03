package com.sparky.trak.game.service;

import com.sparky.trak.game.domain.Game;
import com.sparky.trak.game.domain.GameGenreXref;
import com.sparky.trak.game.domain.Genre;
import com.sparky.trak.game.repository.specification.GenreSpecification;
import com.sparky.trak.game.service.dto.GenreDto;
import org.springframework.data.domain.Pageable;

import javax.json.JsonMergePatch;

/**
 * The {@link GenreService} follows the basic CRUD principle for interaction with {@link Genre} entities on the persistence layer.
 * However, the {@link GenreService} builds an additional layer of abstraction, with the primary purpose being checking the validity of
 * {@link Genre} data being requested from the persistence layer, as well as encapsulating any domain-based objects into {@link GenreDto}
 * transfer objects, for additional validation and protection.
 *
 * The {@link GenreService} still follows the practise in that it will not catch or handle exceptions thrown by the persistence layer,
 * instead it will wrap them in a more reasonable response and propagate the exception to the callee.
 *
 * @since 1.0.0
 * @author Sparky Studios
 */
public interface GenreService {

    /**
     * Given a {@link GenreDto} instance, the service will attempt to persist the data with the underlying persistence layer.
     * If the {@link GenreDto} supplied contains an Id that matches an existing entity, insertion to the persistence layer will
     * fail and a {@link javax.persistence.EntityExistsException} will be thrown. If persistence succeeds, the data is saved and the
     * saved entity is returned as a {@link GenreDto}.
     *
     * The method does not allow <code>null</code> entities to be inserted, if null is provided, a {@link NullPointerException}
     * will be thrown.
     *
     * @param genreDto The {@link GenreDto} instance to persist.
     *
     * @return The updated persisted entity as a {@link GenreDto}.
     *
     * @throws NullPointerException Thrown if the argument provided is null.
     */
    GenreDto save(GenreDto genreDto);

    /**
     * Given an ID of a {@link Genre} entity, this service method will query the underlying persistence layer and try and
     * retrieve the {@link Genre} entity that matches the given ID and map it to a {@link GenreDto}. If the ID provided does not
     * map to any known {@link Genre} entity, then an exception will be thrown specifying that it can't be found.
     *
     * @param id The ID of the {@link Genre} entity to try and retrieve.
     *
     * @return The {@link Genre} entity matching the ID mapped to a {@link GenreDto}.
     *
     * @throws javax.persistence.EntityNotFoundException Thrown if the ID doesn't match an existing {@link Genre}.
     */
    GenreDto findById(long id);

    /**
     * Given an ID of a {@link Game} entity, this service method will retrieve all of the {@link Genre}s entities that are associated
     * with this {@link Game}, which is mapped by {@link GameGenreXref} entities. If no {@link Genre}s are associated with a given
     * {@link Game}, then an empty {@link Iterable} is returned. If a {@link Game} with the specified ID doesn't exist, then a
     * {@link javax.persistence.EntityNotFoundException} exception will be thrown.
     *
     * @param gameId The ID of the {@link Game} to retrieve {@link Genre}s for.
     *
     * @return The {@link Genre} entities mapped to the {@link Game}, converted to {@link GenreDto}'s.
     *
     * @throws javax.persistence.EntityNotFoundException Thrown if the gameId doesn't match any {@link Game} entities.
     */
    Iterable<GenreDto> findGenresByGameId(long gameId);

    /**
     * This method will retrieve an {@link Iterable} of {@link GenreDto} with a response size specified by the {@link Pageable}. The
     * results can be queried and filtered by utilising the exposed specifications on the {@link GenreSpecification} object. If the response
     * from the specifications is that none match, an empty {@link Iterable} will be returned.
     *
     * The {@link GenreSpecification} argument can be omitted and is optional, however if the callee provides <code>null</code> for the
     * {@link Pageable}, an exception will be thrown.
     *
     * @param genreSpecification The {@link GenreSpecification} to filter the query by.
     * @param pageable The size and page of data to return.
     *
     * @return An {@link Iterable} of relevant queried {@link GenreDto} instances.
     */
    Iterable<GenreDto> findAll(GenreSpecification genreSpecification, Pageable pageable);

    /**
     * Retrieves the total number of rows that match the criteria specified within the {@link GenreSpecification}. The specification
     * provided must be a valid instance, if <code>null</code> is provided, a {@link NullPointerException} will be thrown to the callee.
     *
     * @param genreSpecification The {@link GenreSpecification} criteria to count the results for.
     *
     * @return The total number of rows that matches the given criteria.
     */
    long count(GenreSpecification genreSpecification);

    /**
     * Given a {@link GenreDto} instance, the service will attempt to the update the persisted data which matches the given {@link GenreDto}
     * in the underlying persistence layer. If the {@link GenreDto} supplied contains an ID that doesn't match any existing entities, then
     * the update will fail and a {@link javax.persistence.EntityNotFoundException} will be thrown. If persistence succeeds, the relevant
     * record is updated and the updated entity is returned as a {@link GenreDto}.
     *
     * The method does not allow <code>null</code> entities to be inserted, if null is provided, a {@link NullPointerException}
     * will be thrown.
     *
     * @param genreDto The {@link GenreDto} instance to update.
     *
     * @return The updated persisted entity as a {@link GenreDto}.
     *
     * @throws NullPointerException Thrown if the {@link GenreDto} argument provided is null.
     */
    GenreDto update(GenreDto genreDto);

    /**
     * Given a {@link JsonMergePatch} which will contain JSON information pertaining to a {@link GenreDto}, this method will attempt to retrieve
     * the {@link GenreDto} that matches the given ID and apply the new JSON on top of it. If the ID provided doesn't match any existing entities,
     * then the patch will fail and a {@link javax.persistence.EntityNotFoundException} will be thrown. If the {@link JsonMergePatch} contains any
     * JSON data not contained on the {@link GenreDto}, it'll be ignored. If the patching succeeds, the patched record will be updated and the updated
     * entity returned as a {@link GenreDto}.
     *
     * The method does not allow a null {@link JsonMergePatch} to be provided, if null is provided, a {@link NullPointerException} will be thrown.
     *
     * @param id The ID of the {@link GenreDto} to patch.
     * @param jsonMergePatch The {@link JsonMergePatch} containing the JSON data to patch.
     *
     * @return The patched persisted entity as a {@link GenreDto}.
     *
     * @throws javax.persistence.EntityNotFoundException Thrown if the ID provided doesn't match an existing {@link Genre} entity.
     * @throws NullPointerException Thrown if the {@link JsonMergePatch} argument is null.
     */
    GenreDto patch(long id, JsonMergePatch jsonMergePatch);

    /**
     * Deletes the persisted entity that is mapped to the given ID. If the service cannot find a {@link GenreDto} that is mapped to the ID,
     * then deletion will not occur and a {@link javax.persistence.EntityNotFoundException} exception will be thrown. Deletion can be
     * classes as successful if the method completes without throwing additional errors.
     *
     * @param id The ID of the {@link GenreDto} to delete.
     *
     * @throws javax.persistence.EntityNotFoundException Thrown if the ID doesn't map to an existing {@link Genre} entity.
     */
    void deleteById(long id);
}