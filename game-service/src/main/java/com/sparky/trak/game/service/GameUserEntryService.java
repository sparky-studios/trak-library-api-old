package com.sparky.trak.game.service;

import com.sparky.trak.game.domain.GameUserEntry;
import com.sparky.trak.game.repository.specification.GameUserEntrySpecification;
import com.sparky.trak.game.service.dto.GameUserEntryDto;
import org.springframework.data.domain.Pageable;

import javax.json.JsonMergePatch;

public interface GameUserEntryService {

    /**
     * Given a {@link GameUserEntryDto} instance, the service will attempt to persist the data with the underlying persistence layer.
     * If the {@link GameUserEntryDto} supplied contains an Id that matches an existing entity, insertion to the persistence layer will
     * fail and a {@link javax.persistence.EntityExistsException} will be thrown. If persistence succeeds, the data is saved and the
     * saved entity is returned as a {@link GameUserEntryDto}.
     *
     * The method does not allow <code>null</code> entities to be inserted, if null is provided, a {@link NullPointerException}
     * will be thrown.
     *
     * @param gameUserEntryDto The {@link GameUserEntryDto} instance to persist.
     *
     * @return The updated persisted entity as a {@link GameUserEntryDto}.
     *
     * @throws NullPointerException Thrown if the argument provided is null.
     */
    GameUserEntryDto save(GameUserEntryDto gameUserEntryDto);

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
     * Given a {@link GameUserEntryDto} instance, the service will attempt to the update the persisted data which matches the given {@link GameUserEntryDto}
     * in the underlying persistence layer. If the {@link GameUserEntryDto} supplied contains an ID that doesn't match any existing entities, then
     * the update will fail and a {@link javax.persistence.EntityNotFoundException} will be thrown. If persistence succeeds, the relevant
     * record is updated and the updated entity is returned as a {@link GameUserEntryDto}.
     *
     * The method does not allow <code>null</code> entities to be inserted, if null is provided, a {@link NullPointerException}
     * will be thrown.
     *
     * @param gameUserEntryDto The {@link GameUserEntryDto} instance to update.
     *
     * @return The updated persisted entity as a {@link GameUserEntryDto}.
     *
     * @throws NullPointerException Thrown if the {@link GameUserEntryDto} argument provided is null.
     */
    GameUserEntryDto update(GameUserEntryDto gameUserEntryDto);

    /**
     * Given a {@link JsonMergePatch} which will contain JSON information pertaining to a {@link GameUserEntryDto}, this method will attempt to retrieve
     * the {@link GameUserEntryDto} that matches the given ID and apply the new JSON on top of it. If the ID provided doesn't match any existing entities,
     * then the patch will fail and a {@link javax.persistence.EntityNotFoundException} will be thrown. If the {@link JsonMergePatch} contains any
     * JSON data not contained on the {@link GameUserEntryDto}, it'll be ignored. If the patching succeeds, the patched record will be updated and the updated
     * entity returned as a {@link GameUserEntryDto}.
     *
     * The method does not allow a null {@link JsonMergePatch} to be provided, if null is provided, a {@link NullPointerException} will be thrown.
     *
     * @param id The ID of the {@link GameUserEntryDto} to patch.
     * @param jsonMergePatch The {@link JsonMergePatch} containing the JSON data to patch.
     *
     * @return The patched persisted entity as a {@link GameUserEntryDto}.
     *
     * @throws javax.persistence.EntityNotFoundException Thrown if the ID provided doesn't match an existing {@link GameUserEntry} entity.
     * @throws NullPointerException Thrown if the {@link JsonMergePatch} argument is null.
     */
    GameUserEntryDto patch(long id, JsonMergePatch jsonMergePatch);

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
