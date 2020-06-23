package com.sparky.trak.game.service;

import com.sparky.trak.game.domain.GameRequest;
import com.sparky.trak.game.repository.specification.GameRequestSpecification;
import com.sparky.trak.game.service.dto.GameRequestDto;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

import javax.json.JsonMergePatch;

/**
 * The {@link GameRequestService} follows the basic CRUD principle for interaction with {@link GameRequest} entities on the persistence layer.
 * However, the {@link GameRequestService} builds an additional layer of abstraction, with the primary purpose being checking the validity of
 * {@link GameRequest} data being requested from the persistence layer, as well as encapsulating any domain-based objects into {@link GameRequestDto}
 * transfer objects, for additional validation and protection.
 *
 * The {@link GameRequestService} still follows the practise in that it will not catch or handle exceptions thrown by the persistence layer,
 * instead it will wrap them in a more reasonable response and propagate the exception to the callee.
 *
 * @since 1.0.0
 * @author Sparky Studios
 */
public interface GameRequestService {

    /**
     * Given a {@link GameRequestDto} instance, the service will attempt to persist the data with the underlying persistence layer.
     * If the {@link GameRequestDto} supplied contains an ID that matches an existing entity, insertion to the persistence layer will
     * fail and a {@link javax.persistence.EntityExistsException} will be thrown. If persistence succeeds, the data is saved and the
     * saved entity is returned as a {@link GameRequestDto}.
     *
     * The method does not allow <code>null</code> entities to be inserted, if null is provided, a {@link NullPointerException}
     * will be thrown.
     *
     * @param gameRequestDto The {@link GameRequestDto} instance to persist.
     *
     * @return The updated persisted entity as a {@link GameRequestDto}.
     *
     * @throws NullPointerException Thrown if the argument provided is null.
     */
    GameRequestDto save(GameRequestDto gameRequestDto);

    /**
     * Given an ID of a {@link GameRequestDto} object, this service method will query the underlying persistence layer and try and
     * retrieve the {@link GameRequest} entity that matches the given ID and map it to a {@link GameRequestDto}. If the ID provided does not
     * map to any known {@link GameRequest} entity, then an exception will be thrown specifying that it can't be found.
     *
     * @param id The ID of the {@link GameRequestDto} object to try and retrieve.
     *
     * @return The {@link GameRequest} entity matching the ID mapped to a {@link GameRequestDto}.
     *
     * @throws javax.persistence.EntityNotFoundException Thrown if the ID doesn't match an existing {@link GameRequest}.
     */
    GameRequestDto findById(long id);

    /**
     * This method will retrieve an {@link Iterable} of {@link GameRequestDto} with a response size specified by the {@link Pageable}. The
     * results can be queried and filtered by utilising the exposed specifications on the {@link GameRequestSpecification} object. If the response
     * from the specifications is that none match, an empty {@link Iterable} will be returned.
     *
     * The {@link GameRequestSpecification} argument can be omitted and is optional, however if the callee provides <code>null</code> for the
     * {@link Pageable}, an exception will be thrown.
     *
     * @param gameRequestSpecification The {@link GameRequestSpecification} to filter the query by.
     * @param pageable The size and page of data to return.
     *
     * @return An {@link Iterable} of relevant queried {@link GameRequestDto} instances.
     */
    Iterable<GameRequestDto> findAll(GameRequestSpecification gameRequestSpecification, Pageable pageable);

    /**
     * Retrieves the total number of rows that match the criteria specified within the {@link GameRequestSpecification}. The specification
     * provided must be a valid instance, if <code>null</code> is provided, a {@link NullPointerException} will be thrown to the callee.
     *
     * @param gameRequestSpecification The {@link GameRequestSpecification} criteria to count the results for.
     *
     * @return The total number of rows that matches the given criteria.
     */
    long count(GameRequestSpecification gameRequestSpecification);

    /**
     * Given a {@link GameRequestDto} instance, the service will attempt to the update the persisted data which matches the given {@link GameRequestDto}
     * in the underlying persistence layer. If the {@link GameRequestDto} supplied contains an ID that doesn't match any existing entities, then
     * the update will fail and a {@link javax.persistence.EntityNotFoundException} will be thrown. If persistence succeeds, the relevant
     * record is updated and the updated entity is returned as a {@link GameRequestDto}.
     *
     * The method does not allow <code>null</code> entities to be inserted, if null is provided, a {@link NullPointerException}
     * will be thrown.
     *
     * @param gameRequestDto The {@link GameRequestDto} instance to update.
     *
     * @return The updated persisted entity as a {@link GameRequestDto}.
     *
     * @throws NullPointerException Thrown if the {@link GameRequestDto} argument provided is null.
     */
    GameRequestDto update(GameRequestDto gameRequestDto);

    /**
     * Given a {@link GameRequestDto} instance, the service will attempt to complete the associated {@link GameRequestDto} and send a notification
     * to the user that initially sent the request. Completion and notifications will only occur for a {@link GameRequestDto} that isn't already
     * marked as completed.
     *
     * It is assumed that the {@link GameRequestDto} instance being passed in is valid and not equal to <code>null</code>.
     *
     * @param gameRequestDto The {@link GameRequestDto} to complete and send a notification against.
     */
    void complete(@NonNull GameRequestDto gameRequestDto);

    /**
     * Given a {@link JsonMergePatch} which will contain JSON information pertaining to a {@link GameRequestDto}, this method will attempt to retrieve
     * the {@link GameRequestDto} that matches the given ID and apply the new JSON on top of it. If the ID provided doesn't match any existing entities,
     * then the patch will fail and a {@link javax.persistence.EntityNotFoundException} will be thrown. If the {@link JsonMergePatch} contains any
     * JSON data not contained on the {@link GameRequestDto}, it'll be ignored. If the patching succeeds, the patched record will be updated and the updated
     * entity returned as a {@link GameRequestDto}.
     *
     * The method does not allow a null {@link JsonMergePatch} to be provided, if null is provided, a {@link NullPointerException} will be thrown.
     *
     * @param id The ID of the {@link GameRequestDto} to patch.
     * @param jsonMergePatch The {@link JsonMergePatch} containing the JSON data to patch.
     *
     * @return The patched persisted entity as a {@link GameRequestDto}.
     *
     * @throws javax.persistence.EntityNotFoundException Thrown if the ID provided doesn't match an existing {@link GameRequestDto} entity.
     * @throws NullPointerException Thrown if the {@link JsonMergePatch} argument is null.
     */
    GameRequestDto patch(long id, JsonMergePatch jsonMergePatch);

    /**
     * Deletes the persisted entity that is mapped to the given ID. If the service cannot find a {@link GameRequestDto} that is mapped to the ID,
     * then deletion will not occur and a {@link javax.persistence.EntityNotFoundException} exception will be thrown. Deletion can be
     * classes as successful if the method completes without throwing additional errors.
     *
     * @param id The ID of the {@link GameRequestDto} to delete.
     *
     * @throws javax.persistence.EntityNotFoundException Thrown if the ID doesn't map to an existing {@link GameRequestDto} entity.
     */
    void deleteById(long id);
}
