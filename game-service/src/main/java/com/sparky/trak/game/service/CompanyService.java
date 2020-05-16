package com.sparky.trak.game.service;

import com.sparky.trak.game.domain.Company;
import com.sparky.trak.game.repository.specification.CompanySpecification;
import com.sparky.trak.game.service.dto.CompanyDto;
import org.springframework.data.domain.Pageable;

import javax.json.JsonMergePatch;

/**
 * The {@link CompanyService} follows the basic CRUD principle for interaction with {@link Company} entities on the persistence layer.
 * However, the {@link CompanyService} builds an additional layer of abstraction, with the primary purpose being checking the validity of
 * {@link Company} data being requested from the persistence layer, as well as encapsulating any domain-based objects into {@link CompanyDto}
 * transfer objects, for additional validation and protection.
 *
 * The {@link CompanyService} still follows the practise in that it will not catch or handle exceptions thrown by the persistence layer,
 * instead it will wrap them in a more reasonable response and propagate the exception to the callee.
 *
 * @since 1.0.0
 * @author Sparky Studios
 */
public interface CompanyService {

    /**
     * Given a {@link CompanyDto} instance, the service will attempt to persist the data with the underlying persistence layer.
     * If the {@link CompanyDto} supplied contains an Id that matches an existing entity, insertion to the persistence layer will
     * fail and a {@link javax.persistence.EntityExistsException} will be thrown. If persistence succeeds, the data is saved and the
     * saved entity is returned as a {@link CompanyDto}.
     *
     * The method does not allow <code>null</code> entities to be inserted, if null is provided, a {@link NullPointerException}
     * will be thrown.
     *
     * @param companyDto The {@link CompanyDto} instance to persist.
     *
     * @return The updated persisted entity as a {@link CompanyDto}.
     */
    CompanyDto save(CompanyDto companyDto);

    /**
     * Given an ID of a {@link Company} entity, this service method will query the underlying persistence layer and try and
     * retrieve the {@link Company} entity that matches the given ID and map it to a {@link CompanyDto}. If the Id provided does not
     * map to any known {@link Company} entity, then an exception will be thrown specifying that it can't be found.
     *
     * @param id The ID of the {@link Company} entity to try and retrieve.
     *
     * @return The {@link Company} entity matching the ID mapped to a {@link CompanyDto}.
     */
    CompanyDto findById(long id);

    /**
     * This method will retrieve an {@link Iterable} of {@link CompanyDto} with a response size specified by the {@link Pageable}. The
     * results can be queried and filtered by utilising the exposed specifications on the {@link CompanySpecification} object. If the response
     * from the specifications is that none match, an empty {@link Iterable} will be returned.
     *
     * The {@link CompanySpecification} argument can be omitted and is optional, however if the callee provides <code>null</code> for the
     * {@link Pageable}, an exception will be thrown.
     *
     * @param companySpecification The {@link CompanySpecification} to filter the query by.
     * @param pageable The size and page of data to return.
     *
     * @return An {@link Iterable} of relevant queried {@link CompanyDto} instances.
     */
    Iterable<CompanyDto> findAll(CompanySpecification companySpecification, Pageable pageable);

    /**
     * Given a {@link CompanyDto} instance, the service will attempt to the update the persisted data which matches the given {@link CompanyDto}
     * in the underlying persistence layer. If the {@link CompanyDto} supplied contains an ID that doesn't match any existing entities, then
     * the update will fail and a {@link javax.persistence.EntityNotFoundException} will be thrown. If persistence succeeds, the relevant
     * record is updated and the updated entity is returned as a {@link CompanyDto}.
     *
     * The method does not allow <code>null</code> entities to be inserted, if null is provided, a {@link NullPointerException}
     * will be thrown.
     *
     * @param companyDto The {@link CompanyDto} instance to update.
     *
     * @return The updated persisted entity as a {@link CompanyDto}.
     */
    CompanyDto update(CompanyDto companyDto);

    /**
     * Given a {@link JsonMergePatch} which will contain JSON information pertaining to a {@link CompanyDto}, this method will attempt to retrieve
     * the {@link CompanyDto} that matches the given ID and apply the new JSON on top of it. If the ID provided doesn't match any existing entities,
     * then the patch will fail and a {@link javax.persistence.EntityNotFoundException} will be thrown. If the {@link JsonMergePatch} contains any
     * JSON data not contained on the {@link CompanyDto}, it'll be ignored. If the patching succeeds, the patched record will be updated and the updated
     * entity returned as a {@link CompanyDto}.
     *
     * The method does not allow a null {@link JsonMergePatch} to be provided, if null is provided, a {@link NullPointerException} will be thrown.
     *
     * @param id The ID of the {@link CompanyDto} to patch.
     * @param jsonMergePatch The {@link JsonMergePatch} containing the JSON data to patch.
     *
     * @return The patched persisted entity as a {@link CompanyDto}.
     */
    CompanyDto patch(long id, JsonMergePatch jsonMergePatch);

    /**
     * Deletes the persisted entity that is mapped to the given ID. If the service cannot find a {@link CompanyDto} that is mapped to the ID,
     * then deletion will not occur and a {@link javax.persistence.EntityNotFoundException} exception will be thrown. Deletion can be
     * classes as successful if the method completes without throwing additional errors.
     *
     * @param id The ID of the {@link CompanyDto} to delete.
     */
    void deleteById(long id);
}
