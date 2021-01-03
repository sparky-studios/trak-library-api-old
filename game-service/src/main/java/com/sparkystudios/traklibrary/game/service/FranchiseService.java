package com.sparkystudios.traklibrary.game.service;

import com.sparkystudios.traklibrary.game.domain.Franchise;
import com.sparkystudios.traklibrary.game.repository.specification.FranchiseSpecification;
import com.sparkystudios.traklibrary.game.service.dto.FranchiseDto;
import org.springframework.data.domain.Pageable;

import javax.json.JsonMergePatch;

/**
 * The {@link FranchiseService} follows the basic CRUD principle for interaction with {@link Franchise} entities on the persistence layer.
 * However, the {@link FranchiseService} builds an additional layer of abstraction, with the primary purpose being checking the validity of
 * {@link Franchise} data being requested from the persistence layer, as well as encapsulating any domain-based objects into {@link FranchiseDto}
 * transfer objects, for additional validation and protection.
 *
 * The {@link FranchiseService} still follows the practise in that it will not catch or handle exceptions thrown by the persistence layer,
 * instead it will wrap them in a more reasonable response and propagate the exception to the callee.
 *
 * @since 0.1.0
 * @author Sparky Studios
 */
public interface FranchiseService {

    /**
     * Given a {@link FranchiseDto} instance, the service will attempt to persist the data with the underlying persistence layer.
     * If the {@link FranchiseDto} supplied contains an Id that matches an existing entity, insertion to the persistence layer will
     * fail and a {@link javax.persistence.EntityExistsException} will be thrown. If persistence succeeds, the data is saved and the
     * saved entity is returned as a {@link FranchiseDto}.
     *
     * The method does not allow <code>null</code> entities to be inserted, if null is provided, a {@link NullPointerException}
     * will be thrown.
     *
     * @param franchiseDto The {@link FranchiseDto} instance to persist.
     *
     * @return The updated persisted entity as a {@link FranchiseDto}.
     *
     * @throws NullPointerException Thrown if the argument provided is null.
     */
    FranchiseDto save(FranchiseDto franchiseDto);

    /**
     * Given an ID of a {@link Franchise} entity, this service method will query the underlying persistence layer and try and
     * retrieve the {@link Franchise} entity that matches the given ID and map it to a {@link FranchiseDto}. If the ID provided does not
     * map to any known {@link Franchise} entity, then an exception will be thrown specifying that it can't be found.
     *
     * @param id The ID of the {@link Franchise} entity to try and retrieve.
     *
     * @return The {@link Franchise} entity matching the ID mapped to a {@link FranchiseDto}.
     *
     * @throws javax.persistence.EntityNotFoundException Thrown if the ID doesn't match an existing {@link Franchise}.
     */
    FranchiseDto findById(long id);

    /**
     * This method will retrieve an {@link Iterable} of {@link FranchiseDto} with a response size specified by the {@link Pageable}. The
     * results can be queried and filtered by utilising the exposed specifications on the {@link FranchiseSpecification} object. If the response
     * from the specifications is that none match, an empty {@link Iterable} will be returned.
     *
     * The {@link FranchiseSpecification} argument can be omitted and is optional, however if the callee provides <code>null</code> for the
     * {@link Pageable}, an exception will be thrown.
     *
     * @param franchiseSpecification The {@link FranchiseSpecification} to filter the query by.
     * @param pageable The size and page of data to return.
     *
     * @return An {@link Iterable} of relevant queried {@link FranchiseDto} instances.
     */
    Iterable<FranchiseDto> findAll(FranchiseSpecification franchiseSpecification, Pageable pageable);

    /**
     * Retrieves the total number of rows that match the criteria specified within the {@link FranchiseSpecification}. The specification
     * provided must be a valid instance, if <code>null</code> is provided, a {@link NullPointerException} will be thrown to the callee.
     *
     * @param franchiseSpecification The {@link FranchiseSpecification} criteria to count the results for.
     *
     * @return The total number of rows that matches the given criteria.
     */
    long count(FranchiseSpecification franchiseSpecification);

    /**
     * Given a {@link FranchiseDto} instance, the service will attempt to the update the persisted data which matches the given {@link FranchiseDto}
     * in the underlying persistence layer. If the {@link FranchiseDto} supplied contains an ID that doesn't match any existing entities, then
     * the update will fail and a {@link javax.persistence.EntityNotFoundException} will be thrown. If persistence succeeds, the relevant
     * record is updated and the updated entity is returned as a {@link FranchiseDto}.
     *
     * The method does not allow <code>null</code> entities to be inserted, if null is provided, a {@link NullPointerException}
     * will be thrown.
     *
     * @param franchiseDto The {@link FranchiseDto} instance to update.
     *
     * @return The updated persisted entity as a {@link FranchiseDto}.
     *
     * @throws NullPointerException Thrown if the {@link FranchiseDto} argument provided is null.
     */
    FranchiseDto update(FranchiseDto franchiseDto);

    /**
     * Given a {@link JsonMergePatch} which will contain JSON information pertaining to a {@link FranchiseDto}, this method will attempt to retrieve
     * the {@link FranchiseDto} that matches the given ID and apply the new JSON on top of it. If the ID provided doesn't match any existing entities,
     * then the patch will fail and a {@link javax.persistence.EntityNotFoundException} will be thrown. If the {@link JsonMergePatch} contains any
     * JSON data not contained on the {@link FranchiseDto}, it'll be ignored. If the patching succeeds, the patched record will be updated and the updated
     * entity returned as a {@link FranchiseDto}.
     *
     * The method does not allow a null {@link JsonMergePatch} to be provided, if null is provided, a {@link NullPointerException} will be thrown.
     *
     * @param id The ID of the {@link FranchiseDto} to patch.
     * @param jsonMergePatch The {@link JsonMergePatch} containing the JSON data to patch.
     *
     * @return The patched persisted entity as a {@link FranchiseDto}.
     *
     * @throws javax.persistence.EntityNotFoundException Thrown if the ID provided doesn't match an existing {@link Franchise} entity.
     * @throws NullPointerException Thrown if the {@link JsonMergePatch} argument is null.
     */
    FranchiseDto patch(long id, JsonMergePatch jsonMergePatch);

    /**
     * Deletes the persisted entity that is mapped to the given ID. If the service cannot find a {@link FranchiseDto} that is mapped to the ID,
     * then deletion will not occur and a {@link javax.persistence.EntityNotFoundException} exception will be thrown. Deletion can be
     * classes as successful if the method completes without throwing additional errors.
     *
     * @param id The ID of the {@link FranchiseDto} to delete.
     *
     * @throws javax.persistence.EntityNotFoundException Thrown if the ID doesn't map to an existing {@link Franchise} entity.
     */
    void deleteById(long id);
}
