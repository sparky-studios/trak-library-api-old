package com.sparky.trak.game.service;

import com.sparky.trak.game.domain.Developer;
import com.sparky.trak.game.repository.specification.DeveloperSpecification;
import com.sparky.trak.game.service.dto.DeveloperDto;
import com.sparky.trak.game.service.dto.GameDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.json.JsonMergePatch;

/**
 * The {@link DeveloperService} follows the basic CRUD principle for interaction with {@link Developer} entities on the persistence layer.
 * However, the {@link DeveloperService} builds an additional layer of abstraction, with the primary purpose being checking the validity of
 * {@link Developer} data being requested from the persistence layer, as well as encapsulating any domain-based objects into {@link DeveloperDto}
 * transfer objects, for additional validation and protection.
 *
 * The {@link DeveloperService} still follows the practise in that it will not catch or handle exceptions thrown by the persistence layer,
 * instead it will wrap them in a more reasonable response and propagate the exception to the callee.
 *
 * @since 1.0.0
 * @author Sparky Studios
 */
public interface DeveloperService {

    /**
     * Given a {@link DeveloperDto} instance, the service will attempt to persist the data with the underlying persistence layer.
     * If the {@link DeveloperDto} supplied contains an Id that matches an existing entity, insertion to the persistence layer will
     * fail and a {@link javax.persistence.EntityExistsException} will be thrown. If persistence succeeds, the data is saved and the
     * saved entity is returned as a {@link DeveloperDto}.
     *
     * The method does not allow <code>null</code> entities to be inserted, if null is provided, a {@link NullPointerException}
     * will be thrown.
     *
     * @param developerDto The {@link DeveloperDto} instance to persist.
     *
     * @return The updated persisted entity as a {@link DeveloperDto}.
     */
    DeveloperDto save(DeveloperDto developerDto);

    /**
     * Given an ID of a {@link Developer} entity, this service method will query the underlying persistence layer and try and
     * retrieve the {@link Developer} entity that matches the given ID and map it to a {@link DeveloperDto}. If the Id provided does not
     * map to any known {@link Developer} entity, then an exception will be thrown specifying that it can't be found.
     *
     * @param id The ID of the {@link Developer} entity to try and retrieve.
     *
     * @return The {@link Developer} entity matching the ID mapped to a {@link DeveloperDto}.
     */
    DeveloperDto findById(long id);

    /**
     * Given the ID of a {@link GameDto}, this method will retrieve a {@link Page} of {@link DeveloperDto}s that are associated with
     * the given {@link GameDto}. If the ID provided does not map to any {@link GameDto}, a {@link javax.persistence.EntityNotFoundException}
     * will be thrown. If there are no {@link DeveloperDto}s associated with the {@link GameDto}, an empty {@link Iterable} will be returned.
     *
     * @param gameId The ID of the {@link GameDto} to retrieve {@link DeveloperDto} entries for.
     * @param pageable The amount of data and the page to return.
     *
     * @return A page of {@link DeveloperDto}s that are mapped with the given {@link GameDto} entity.
     */
    Iterable<DeveloperDto> findDevelopersByGameId(long gameId, Pageable pageable);

    /**
     * Given the ID of a {@link GameDto}, this method will retrieve the total count for how many {@link DeveloperDto}'s have an association
     * to the given {@link GameDto}. If the ID provided does not map to any {@link GameDto}, {@link javax.persistence.EntityNotFoundException}
     * will be thrown.
     *
     * @param gameId The ID of the {@link GameDto} to retrieve the total count of {@link DeveloperDto} entries for.
     *
     * @return The total count of {@link DeveloperDto}'s associated with the given {@link GameDto}.
     */
    long countDevelopersByGameId(long gameId);

    /**
     * This method will retrieve an {@link Iterable} of {@link DeveloperDto} with a response size specified by the {@link Pageable}. The
     * results can be queried and filtered by utilising the exposed specifications on the {@link DeveloperSpecification} object. If the response
     * from the specifications is that none match, an empty {@link Iterable} will be returned.
     *
     * The {@link DeveloperSpecification} argument can be omitted and is optional, however if the callee provides <code>null</code> for the
     * {@link Pageable}, an exception will be thrown.
     *
     * @param developerSpecification The {@link DeveloperSpecification} to filter the query by.
     * @param pageable The size and page of data to return.
     *
     * @return An {@link Iterable} of relevant queried {@link DeveloperDto} instances.
     */
    Iterable<DeveloperDto> findAll(DeveloperSpecification developerSpecification, Pageable pageable);

    /**
     * Retrieves the total number of rows that match the criteria specified within the {@link DeveloperSpecification}. The specification
     * provided must be a valid instance, if <code>null</code> is provided, a {@link NullPointerException} will be thrown to the callee.
     *
     * @param developerSpecification The {@link DeveloperSpecification} criteria to count the results for.
     *
     * @return The total number of rows that matches the given criteria.
     */
    long count(DeveloperSpecification developerSpecification);

    /**
     * Given a {@link DeveloperDto} instance, the service will attempt to the update the persisted data which matches the given {@link DeveloperDto}
     * in the underlying persistence layer. If the {@link DeveloperDto} supplied contains an ID that doesn't match any existing entities, then
     * the update will fail and a {@link javax.persistence.EntityNotFoundException} will be thrown. If persistence succeeds, the relevant
     * record is updated and the updated entity is returned as a {@link DeveloperDto}.
     *
     * The method does not allow <code>null</code> entities to be inserted, if null is provided, a {@link NullPointerException}
     * will be thrown.
     *
     * @param developerDto The {@link DeveloperDto} instance to update.
     *
     * @return The updated persisted entity as a {@link DeveloperDto}.
     */
    DeveloperDto update(DeveloperDto developerDto);

    /**
     * Given a {@link JsonMergePatch} which will contain JSON information pertaining to a {@link DeveloperDto}, this method will attempt to retrieve
     * the {@link DeveloperDto} that matches the given ID and apply the new JSON on top of it. If the ID provided doesn't match any existing entities,
     * then the patch will fail and a {@link javax.persistence.EntityNotFoundException} will be thrown. If the {@link JsonMergePatch} contains any
     * JSON data not contained on the {@link DeveloperDto}, it'll be ignored. If the patching succeeds, the patched record will be updated and the updated
     * entity returned as a {@link DeveloperDto}.
     *
     * The method does not allow a null {@link JsonMergePatch} to be provided, if null is provided, a {@link NullPointerException} will be thrown.
     *
     * @param id The ID of the {@link DeveloperDto} to patch.
     * @param jsonMergePatch The {@link JsonMergePatch} containing the JSON data to patch.
     *
     * @return The patched persisted entity as a {@link DeveloperDto}.
     */
    DeveloperDto patch(long id, JsonMergePatch jsonMergePatch);

    /**
     * Deletes the persisted entity that is mapped to the given ID. If the service cannot find a {@link DeveloperDto} that is mapped to the ID,
     * then deletion will not occur and a {@link javax.persistence.EntityNotFoundException} exception will be thrown. Deletion can be
     * classes as successful if the method completes without throwing additional errors.
     *
     * @param id The ID of the {@link DeveloperDto} to delete.
     */
    void deleteById(long id);
}