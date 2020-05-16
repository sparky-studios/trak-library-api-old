package com.sparky.trak.game.server.controller;

import com.sparky.trak.game.repository.specification.CompanySpecification;
import com.sparky.trak.game.server.annotation.AllowedForModerator;
import com.sparky.trak.game.server.annotation.AllowedForUser;
import com.sparky.trak.game.server.assembler.CompanyRepresentationModelAssembler;
import com.sparky.trak.game.server.exception.ApiError;
import com.sparky.trak.game.service.CompanyService;
import com.sparky.trak.game.service.dto.CompanyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.json.JsonMergePatch;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * The {@link CompanyController} is a simple controller class that exposes a CRUD based API that is used to interact with
 * any entities or objects that pertain to companies. It provides API end-points for creating, updating, finding and deleting
 * company entities. It should be noted that the controller itself contains very little logic, the logic is contained within the
 * {@link CompanyService}. The controllers primary purpose is to wrap the responses it received from the {@link CompanyService}
 * into HATEOAS responses. All mappings on this controller therefore produce a {@link MediaTypes#HAL_JSON} response.
 *
 * @since 1.0.0
 * @author Sparky Studios
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/companies", produces = MediaTypes.HAL_JSON_VALUE)
public class CompanyController {

    private final CompanyService companyService;
    private final CompanyRepresentationModelAssembler companyRepresentationModelAssembler;

    /**
     * End-point that will attempt to save the given {@link CompanyDto} request body to the underlying
     * persistence layer. The {@link CompanyDto} must either be valid or have all of the required fields meet
     * its pre-requisite conditions in order to attempt a save to the persistence layer.
     *
     * If the {@link CompanyDto} being saved contains an ID that matches an existing entity in the persistence layer,
     * the {@link CompanyDto} will not be saved and a {@link com.sparky.trak.game.server.exception.ApiError} will
     * be returned with appropriate exceptions details.
     *
     * @param companyDto The {@link CompanyDto} to save.
     *
     * @return The saved {@link CompanyDto} instance as a HATEOAS response.
     */
    @AllowedForModerator
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public EntityModel<CompanyDto> save(@Validated @RequestBody CompanyDto companyDto) {
        return companyRepresentationModelAssembler.toModel(companyService.save(companyDto));
    }

    /**
     * End-point that will retrieve a {@link CompanyDto} instance that matches the given Id and convert
     * it into a consumable HATEOAS response. If a {@link CompanyDto} instance is found that matches the Id, then
     * that data is returned with a status of 200, however if the {@link CompanyDto} cannot be found the method
     * will return a 404 and wrap the exception details in a {@link ApiError} with additional information.
     *
     * @param id The ID of the {@link CompanyDto} to retrieve.
     *
     * @return The {@link CompanyDto} that matches the given ID as a HATEOAS response.
     */
    @AllowedForUser
    @GetMapping("/{id}")
    public EntityModel<CompanyDto> findById(@PathVariable long id) {
        return companyRepresentationModelAssembler.toModel(companyService.findById(id));
    }

    /**
     * End-point that can be used to retrieve a paged result of {@link CompanyDto} instances, that are filtered by
     * the provided {@link CompanySpecification} which appear as request parameters on the URL. The page and each
     * {@link CompanyDto} will be wrapped in a HATEOAS response. If no {@link CompanyDto} match the given criteria,
     * an empty HATEOAS page response will be returned.
     *
     * If any exceptions are thrown internally, and {@link ApiError} response will be returned with additional
     * error details.
     *
     * @param companySpecification The filter queries to filter the page by.
     * @param pageable The size, page and ordering of the {@link CompanyDto} elements in the page.
     * @param pagedResourcesAssembler Injected, used to convert the {@link CompanyDto}s into a {@link PagedModel}.
     *
     * @return A {@link PagedModel} containing the {@link CompanyDto} that match the requested page and criteria.
     */
    @AllowedForUser
    @GetMapping
    public PagedModel<EntityModel<CompanyDto>> findAll(CompanySpecification companySpecification,
                                                       @PageableDefault Pageable pageable,
                                                       PagedResourcesAssembler<CompanyDto> pagedResourcesAssembler) {
        // Get the paged data from the service and convert into a list so it can be added to a page object.
        List<CompanyDto> companyDtos = StreamSupport.stream(companyService.findAll(companySpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Wrap the page in a HATEOAS response.
        return pagedResourcesAssembler.toModel(new PageImpl<>(companyDtos, pageable, companyDtos.size()), companyRepresentationModelAssembler);
    }

    /**
     * End-point that will attempt to updated the given {@link CompanyDto} request body to the underlying
     * persistence layer. The {@link CompanyDto} must either be valid or have all of the required fields meet
     * its pre-requisite conditions in order to attempt an update in the persistence layer.
     *
     * If the {@link CompanyDto} being saved doesn't contain an ID that matches an existing entity in the persistence layer,
     * the {@link CompanyDto} will not be updated and a {@link ApiError} will
     * be returned with appropriate exceptions details.
     *
     * @param companyDto The {@link CompanyDto} to updated.
     *
     * @return The updated {@link CompanyDto} instance as a HATEOAS response.
     */
    @AllowedForModerator
    @PutMapping
    public EntityModel<CompanyDto> update(@Validated @RequestBody CompanyDto companyDto) {
        return companyRepresentationModelAssembler.toModel(companyService.update(companyDto));
    }

    /**
     * End-point that will attempt to patch the {@link CompanyDto} that matches the given ID with the values
     * specified within the {@link JsonMergePatch}. The {@link JsonMergePatch} must contain valid data to be applied
     * to the {@link CompanyDto}, otherwise an {@link ApiError} will be returned to the user with additional exception
     * data.
     *
     * The {@link JsonMergePatch} provided can contain JSON data that is not contained within the {@link CompanyDto},
     * however it will not apply any unknown field, but instead ignore them. If the ID provided does not match
     * a {@link CompanyDto} or the patch fails to apply, {@link ApiError} instances will be returned with additional
     * error information.
     *
     * @param id The ID of the {@link CompanyDto} to patch.
     * @param jsonMergePatch The {@link JsonMergePatch} which contains JSON data to update the {@link CompanyDto} with.
     *
     * @return The patched {@link CompanyDto} instance.
     */
    @AllowedForModerator
    @PatchMapping(value = "/{id}", consumes = "application/merge-patch+json")
    public EntityModel<CompanyDto> patch(@PathVariable long id, @RequestBody JsonMergePatch jsonMergePatch) {
        return companyRepresentationModelAssembler.toModel(companyService.patch(id, jsonMergePatch));
    }

    /**
     * End-point that will attempt to the delete the {@link CompanyDto} instance associated with the given ID. If no {@link CompanyDto}
     * is found that matches the ID, then an exception will be thrown and the end-point will return a {@link ApiError} along with
     * additional information.
     *
     * If the {@link CompanyDto} is successfully deleted, no data will be returned but the endpoint will specify a response code of 204
     * (NO_CONTENT).
     *
     * @param id The ID of the {@link CompanyDto} to delete.
     */
    @AllowedForModerator
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        companyService.deleteById(id);
    }
}
