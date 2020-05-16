package com.sparky.trak.game.service;

import com.sparky.trak.game.domain.Publisher;
import com.sparky.trak.game.repository.specification.PublisherSpecification;
import com.sparky.trak.game.service.dto.GameDto;
import com.sparky.trak.game.service.dto.PublisherDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.json.JsonMergePatch;

/**
 * The {@link PublisherService} follows the basic CRUD principle for interaction with {@link Publisher} entities on the persistence layer.
 * However, the {@link PublisherService} builds an additional layer of abstraction, with the primary purpose being checking the validity of
 * {@link Publisher} data being requested from the persistence layer, as well as encapsulating any domain-based objects into {@link PublisherDto}
 * transfer objects, for additional validation and protection.
 *
 * The {@link PublisherService} still follows the practise in that it will not catch or handle exceptions thrown by the persistence layer,
 * instead it will wrap them in a more reasonable response and propagate the exception to the callee.
 *
 * @since 1.0.0
 * @author Sparky Studios
 */
public interface PublisherService {

    /**
     * Given a {@link PublisherDto} instance, the service will attempt to persist the data with the underlying persistence layer.
     * If the {@link PublisherDto} supplied contains an Id that matches an existing entity, insertion to the persistence layer will
     * fail and a {@link javax.persistence.EntityExistsException} will be thrown. If persistence succeeds, the data is saved and the
     * saved entity is returned as a {@link PublisherDto}.
     *
     * The method does not allow <code>null</code> entities to be inserted, if null is provided, a {@link NullPointerException}
     * will be thrown.
     *
     * @param publisherDto The {@link PublisherDto} instance to persist.
     *
     * @return The updated persisted entity as a {@link PublisherDto}.
     */
    PublisherDto save(PublisherDto publisherDto);

    /**
     * Given an ID of a {@link Publisher} entity, this service method will query the underlying persistence layer and try and
     * retrieve the {@link Publisher} entity that matches the given ID and map it to a {@link PublisherDto}. If the Id provided does not
     * map to any known {@link Publisher} entity, then an exception will be thrown specifying that it can't be found.
     *
     * @param id The ID of the {@link Publisher} entity to try and retrieve.
     *
     * @return The {@link Publisher} entity matching the ID mapped to a {@link PublisherDto}.
     */
    PublisherDto findById(long id);

    /**
     * Given the ID of a {@link GameDto}, this method will retrieve a {@link Page} of {@link PublisherDto}s that are associated with
     * the given {@link GameDto}. If the ID provided does not map to any {@link GameDto}, a {@link javax.persistence.EntityNotFoundException}
     * will be thrown. If there are no {@link PublisherDto}s associated with the {@link GameDto}, an empty {@link Iterable} will be returned.
     *
     * @param gameId The ID of the {@link GameDto} to retrieve {@link PublisherDto} entries for.
     * @param pageable The amount of data and the page to return.
     *
     * @return A page of {@link PublisherDto}s that are mapped with the given {@link GameDto} entity.
     */
    Iterable<PublisherDto> findPublishersByGameId(long gameId, Pageable pageable);

    /**
     * This method will retrieve an {@link Iterable} of {@link PublisherDto} with a response size specified by the {@link Pageable}. The
     * results can be queried and filtered by utilising the exposed specifications on the {@link PublisherSpecification} object. If the response
     * from the specifications is that none match, an empty {@link Iterable} will be returned.
     *
     * The {@link PublisherSpecification} argument can be omitted and is optional, however if the callee provides <code>null</code> for the
     * {@link Pageable}, an exception will be thrown.
     *
     * @param publisherSpecification The {@link PublisherSpecification} to filter the query by.
     * @param pageable The size and page of data to return.
     *
     * @return An {@link Iterable} of relevant queried {@link PublisherDto} instances.
     */
    Iterable<PublisherDto> findAll(PublisherSpecification publisherSpecification, Pageable pageable);

    /**
     * Given a {@link PublisherDto} instance, the service will attempt to the update the persisted data which matches the given {@link PublisherDto}
     * in the underlying persistence layer. If the {@link PublisherDto} supplied contains an ID that doesn't match any existing entities, then
     * the update will fail and a {@link javax.persistence.EntityNotFoundException} will be thrown. If persistence succeeds, the relevant
     * record is updated and the updated entity is returned as a {@link PublisherDto}.
     *
     * The method does not allow <code>null</code> entities to be inserted, if null is provided, a {@link NullPointerException}
     * will be thrown.
     *
     * @param publisherDto The {@link PublisherDto} instance to update.
     *
     * @return The updated persisted entity as a {@link PublisherDto}.
     */
    PublisherDto update(PublisherDto publisherDto);

    /**
     * Given a {@link JsonMergePatch} which will contain JSON information pertaining to a {@link PublisherDto}, this method will attempt to retrieve
     * the {@link PublisherDto} that matches the given ID and apply the new JSON on top of it. If the ID provided doesn't match any existing entities,
     * then the patch will fail and a {@link javax.persistence.EntityNotFoundException} will be thrown. If the {@link JsonMergePatch} contains any
     * JSON data not contained on the {@link PublisherDto}, it'll be ignored. If the patching succeeds, the patched record will be updated and the updated
     * entity returned as a {@link PublisherDto}.
     *
     * The method does not allow a null {@link JsonMergePatch} to be provided, if null is provided, a {@link NullPointerException} will be thrown.
     *
     * @param id The ID of the {@link PublisherDto} to patch.
     * @param jsonMergePatch The {@link JsonMergePatch} containing the JSON data to patch.
     *
     * @return The patched persisted entity as a {@link PublisherDto}.
     */
    PublisherDto patch(long id, JsonMergePatch jsonMergePatch);

    /**
     * Deletes the persisted entity that is mapped to the given ID. If the service cannot find a {@link PublisherDto} that is mapped to the ID,
     * then deletion will not occur and a {@link javax.persistence.EntityNotFoundException} exception will be thrown. Deletion can be
     * classes as successful if the method completes without throwing additional errors.
     *
     * @param id The ID of the {@link PublisherDto} to delete.
     */
    void deleteById(long id);
}