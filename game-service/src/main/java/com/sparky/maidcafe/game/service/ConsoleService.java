package com.sparky.maidcafe.game.service;

import com.sparky.maidcafe.game.domain.Console;
import com.sparky.maidcafe.game.repository.specification.ConsoleSpecification;
import com.sparky.maidcafe.game.service.dto.ConsoleDto;
import com.sparky.maidcafe.game.service.dto.GameDto;
import org.springframework.data.domain.Pageable;

import javax.json.JsonMergePatch;

/**
 * The {@link ConsoleService} follows the basic CRUD principle for interaction with {@link Console} entities on the persistence layer.
 * However, the {@link ConsoleService} builds an additional layer of abstraction, with the primary purpose being checking the validity of
 * {@link Console} data being requested from the persistence layer, as well as encapsulating any domain-based objects into {@link ConsoleDto}
 * transfer objects, for additional validation and protection.
 *
 * The {@link ConsoleService} still follows the practise in that it will not catch or handle exceptions thrown by the persistence layer,
 * instead it will wrap them in a more reasonable response and propagate the exception to the callee.
 *
 * @since 1.0.0
 * @author Sparky Studios
 */
public interface ConsoleService {

    /**
     * Given a {@link ConsoleDto} instance, the service will attempt to persist the data with the underlying persistence layer.
     * If the {@link ConsoleDto} supplied contains an Id that matches an existing entity, insertion to the persistence layer will
     * fail and a {@link javax.persistence.EntityExistsException} will be thrown. If persistence succeeds, the data is saved and the
     * saved entity is returned as a {@link ConsoleDto}.
     *
     * The method does not allow <code>null</code> entities to be inserted, if null is provided, a {@link NullPointerException}
     * will be thrown.
     *
     * @param consoleDto The {@link ConsoleDto} instance to persist.
     *
     * @return The updated persisted entity as a {@link ConsoleDto}.
     */
    ConsoleDto save(ConsoleDto consoleDto);

    /**
     * Given an ID of a {@link Console} entity, this service method will query the underlying persistence layer and try and
     * retrieve the {@link Console} entity that matches the given ID and map it to a {@link ConsoleDto}. If the Id provided does not
     * map to any known {@link Console} entity, then an exception will be thrown specifying that it can't be found.
     *
     * @param id The ID of the {@link Console} entity to try and retrieve.
     *
     * @return The {@link Console} entity matching the ID mapped to a {@link ConsoleDto}.
     */
    ConsoleDto findById(long id);

    /**
     * Retrieves all of the {@link Console} entities stored within the persistence layer. This method should not be used within a live
     * environment, as the amount of data may cause buffer overflows and bring down the server. It should only be used within a test
     * environment and even then, with hesitancy.
     *
     * @return All of the {@link Console} entities contained within the persistence layer, wrapped as {@link ConsoleDto}s.
     */
    Iterable<ConsoleDto> findAll();

    /**
     * Retrieve an {@link Iterable} of all {@link ConsoleDto}'s that are associated with the given {@link GameDto}.
     * The {@link Iterable} returned by this method will not contain any paged information, as it is under the impression
     * that no game will be for more {@link ConsoleDto}'s than a single page is expected to contain. If the ID provided
     * does not map onto an existing {@link GameDto}, then a {@link javax.persistence.EntityNotFoundException} will be
     * thrown, else every {@link ConsoleDto} associated with the {@link GameDto} will be returned.
     *
     * @param gameId The ID of the {@link GameDto} to retrieve the {@link ConsoleDto}'s for.
     *
     * @return An {@link Iterable} of every {@link ConsoleDto} the given {@link GameDto} is on.
     */
    Iterable<ConsoleDto> findConsolesFromGameId(long gameId);

    /**
     * This method will retrieve an {@link Iterable} of {@link ConsoleDto} with a response size specified by the {@link Pageable}. The
     * results can be queried and filtered by utilising the exposed specifications on the {@link ConsoleSpecification} object. If the response
     * from the specifications is that none match, an empty {@link Iterable} will be returned.
     *
     * The {@link ConsoleSpecification} argument can be omitted and is optional, however if the callee provides <code>null</code> for the
     * {@link Pageable}, an exception will be thrown.
     *
     * @param consoleSpecification The {@link ConsoleSpecification} to filter the query by.
     * @param pageable The size and page of data to return.
     *
     * @return An {@link Iterable} of relevant queried {@link ConsoleDto} instances.
     */
    Iterable<ConsoleDto> findAll(ConsoleSpecification consoleSpecification, Pageable pageable);

    /**
     * Given a {@link ConsoleDto} instance, the service will attempt to the update the persisted data which matches the given {@link ConsoleDto}
     * in the underlying persistence layer. If the {@link ConsoleDto} supplied contains an ID that doesn't match any existing entities, then
     * the update will fail and a {@link javax.persistence.EntityNotFoundException} will be thrown. If persistence succeeds, the relevant
     * record is updated and the updated entity is returned as a {@link ConsoleDto}.
     *
     * The method does not allow <code>null</code> entities to be inserted, if null is provided, a {@link NullPointerException}
     * will be thrown.
     *
     * @param consoleDto The {@link ConsoleDto} instance to update.
     *
     * @return The updated persisted entity as a {@link ConsoleDto}.
     */
    ConsoleDto update(ConsoleDto consoleDto);

    /**
     * Given a {@link JsonMergePatch} which will contain JSON information pertaining to a {@link ConsoleDto}, this method will attempt to retrieve
     * the {@link ConsoleDto} that matches the given ID and apply the new JSON on top of it. If the ID provided doesn't match any existing entities,
     * then the patch will fail and a {@link javax.persistence.EntityNotFoundException} will be thrown. If the {@link JsonMergePatch} contains any
     * JSON data not contained on the {@link ConsoleDto}, it'll be ignored. If the patching succeeds, the patched record will be updated and the updated
     * entity returned as a {@link ConsoleDto}.
     *
     * The method does not allow a null {@link JsonMergePatch} to be provided, if null is provided, a {@link NullPointerException} will be thrown.
     *
     * @param id The ID of the {@link ConsoleDto} to patch.
     * @param jsonMergePatch The {@link JsonMergePatch} containing the JSON data to patch.
     *
     * @return The patched persisted entity as a {@link ConsoleDto}.
     */
    ConsoleDto patch(long id, JsonMergePatch jsonMergePatch);

    /**
     * Deletes the persisted entity that is mapped to the given ID. If the service cannot find a {@link ConsoleDto} that is mapped to the ID,
     * then deletion will not occur and a {@link javax.persistence.EntityNotFoundException} exception will be thrown. Deletion can be
     * classes as successful if the method completes without throwing additional errors.
     *
     * @param id The ID of the {@link ConsoleDto} to delete.
     */
    void deleteById(long id);
}
