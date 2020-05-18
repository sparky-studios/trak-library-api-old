package com.sparky.trak.game.server.controller;

import com.sparky.trak.game.repository.specification.DeveloperSpecification;
import com.sparky.trak.game.server.annotation.AllowedForModerator;
import com.sparky.trak.game.server.annotation.AllowedForUser;
import com.sparky.trak.game.server.assembler.DeveloperRepresentationModelAssembler;
import com.sparky.trak.game.server.assembler.GameRepresentationModelAssembler;
import com.sparky.trak.game.server.exception.ApiError;
import com.sparky.trak.game.service.DeveloperService;
import com.sparky.trak.game.service.GameService;
import com.sparky.trak.game.service.dto.DeveloperDto;
import com.sparky.trak.game.service.dto.GameDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.json.JsonMergePatch;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * The {@link DeveloperController} is a simple controller class that exposes a CRUD based API that is used to interact with
 * any entities or objects that pertain to companies. It provides API end-points for creating, updating, finding and deleting
 * company entities. It should be noted that the controller itself contains very little logic, the logic is contained within the
 * {@link DeveloperService}. The controllers primary purpose is to wrap the responses it received from the {@link DeveloperService}
 * into HATEOAS responses. All mappings on this controller therefore produce a {@link MediaTypes#HAL_JSON} response.
 *
 * @since 1.0.0
 * @author Sparky Studios
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/developers", produces = MediaTypes.HAL_JSON_VALUE)
public class DeveloperController {

    private final DeveloperService developerService;
    private final GameService gameService;
    private final DeveloperRepresentationModelAssembler developerRepresentationModelAssembler;
    private final GameRepresentationModelAssembler gameRepresentationModelAssembler;

    /**
     * End-point that will attempt to save the given {@link DeveloperDto} request body to the underlying
     * persistence layer. The {@link DeveloperDto} must either be valid or have all of the required fields meet
     * its pre-requisite conditions in order to attempt a save to the persistence layer.
     *
     * If the {@link DeveloperDto} being saved contains an ID that matches an existing entity in the persistence layer,
     * the {@link DeveloperDto} will not be saved and a {@link com.sparky.trak.game.server.exception.ApiError} will
     * be returned with appropriate exceptions details.
     *
     * @param developerDto The {@link DeveloperDto} to save.
     *
     * @return The saved {@link DeveloperDto} instance as a HATEOAS response.
     */
    @AllowedForModerator
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public EntityModel<DeveloperDto> save(@Validated @RequestBody DeveloperDto developerDto) {
        return developerRepresentationModelAssembler.toModel(developerService.save(developerDto));
    }

    /**
     * End-point that will retrieve a {@link DeveloperDto} instance that matches the given Id and convert
     * it into a consumable HATEOAS response. If a {@link DeveloperDto} instance is found that matches the Id, then
     * that data is returned with a status of 200, however if the {@link DeveloperDto} cannot be found the method
     * will return a 404 and wrap the exception details in a {@link ApiError} with additional information.
     *
     * @param id The ID of the {@link DeveloperDto} to retrieve.
     *
     * @return The {@link DeveloperDto} that matches the given ID as a HATEOAS response.
     */
    @AllowedForUser
    @GetMapping("/{id}")
    public EntityModel<DeveloperDto> findById(@PathVariable long id) {
        return developerRepresentationModelAssembler.toModel(developerService.findById(id));
    }

    /**
     * End-point that will retrieve a {@link PagedModel} of {@link GameDto}s that have a link to the specified
     * {@link DeveloperDto}. If the ID doesn't match an existing {@link DeveloperDto}, then an {@link ApiError} will be
     * returned with additional error details. If the {@link DeveloperDto} exists but has no associated
     * {@link GameDto}'s, then an empty {@link PagedModel} will be returned.
     *
     * @param id The ID of the {@link DeveloperDto} to retrieve associated {@link GameDto}'s for.
     * @param pageable The size and ordering of the page to retrieve.
     * @param pagedResourcesAssembler The assembler used to convert the {@link GameDto}'s to a HATEOAS page.
     *
     * @return A {@link PagedModel} of {@link GameDto}'s that are associated with the given {@link DeveloperDto}.
     */
    @AllowedForUser
    @GetMapping("/{id}/games")
    public PagedModel<EntityModel<GameDto>> findGamesByDeveloperId(@PathVariable long id,
                                                                   @PageableDefault Pageable pageable,
                                                                   PagedResourcesAssembler<GameDto> pagedResourcesAssembler) {
        // The self, next and prev links won't include query parameters if not built manually.
        Link link = new Link(ServletUriComponentsBuilder.fromCurrentRequest().build()
                .toUriString())
                .withSelfRel();

        // Get the paged data from the service and convert into a list so it can be added to a page object.
        List<GameDto> gameDtos = StreamSupport.stream(gameService.findGamesByDeveloperId(id, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Get the total number of entities that match the given criteria, dis-regarding page sizing.
        long count = gameService.countGamesByDeveloperId(id);

        // Wrap the page in a HATEOAS response.
        return pagedResourcesAssembler
                .toModel(new PageImpl<>(gameDtos, pageable, count), gameRepresentationModelAssembler, link);
    }

    /**
     * End-point that can be used to retrieve a paged result of {@link DeveloperDto} instances, that are filtered by
     * the provided {@link DeveloperSpecification} which appear as request parameters on the URL. The page and each
     * {@link DeveloperDto} will be wrapped in a HATEOAS response. If no {@link DeveloperDto} match the given criteria,
     * an empty HATEOAS page response will be returned.
     *
     * If any exceptions are thrown internally, and {@link ApiError} response will be returned with additional
     * error details.
     *
     * @param developerSpecification The filter queries to filter the page by.
     * @param pageable The size, page and ordering of the {@link DeveloperDto} elements in the page.
     * @param pagedResourcesAssembler Injected, used to convert the {@link DeveloperDto}s into a {@link PagedModel}.
     *
     * @return A {@link PagedModel} containing the {@link DeveloperDto} that match the requested page and criteria.
     */
    @AllowedForUser
    @GetMapping
    public PagedModel<EntityModel<DeveloperDto>> findAll(DeveloperSpecification developerSpecification,
                                                         @PageableDefault Pageable pageable,
                                                         PagedResourcesAssembler<DeveloperDto> pagedResourcesAssembler) {
        // The self, next and prev links won't include query parameters if not built manually.
        Link link = new Link(ServletUriComponentsBuilder.fromCurrentRequest().build()
                .toUriString())
                .withSelfRel();

        // Get the paged data from the service and convert into a list so it can be added to a page object.
        List<DeveloperDto> developerDtos = StreamSupport.stream(developerService.findAll(developerSpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Get the total number of entities that match the given criteria, dis-regarding page sizing.
        long count = developerService.count(developerSpecification);

        // Wrap the page in a HATEOAS response.
        return pagedResourcesAssembler
                .toModel(new PageImpl<>(developerDtos, pageable, count), developerRepresentationModelAssembler, link);
    }

    /**
     * End-point that will attempt to updated the given {@link DeveloperDto} request body to the underlying
     * persistence layer. The {@link DeveloperDto} must either be valid or have all of the required fields meet
     * its pre-requisite conditions in order to attempt an update in the persistence layer.
     *
     * If the {@link DeveloperDto} being saved doesn't contain an ID that matches an existing entity in the persistence layer,
     * the {@link DeveloperDto} will not be updated and a {@link ApiError} will
     * be returned with appropriate exceptions details.
     *
     * @param developerDto The {@link DeveloperDto} to updated.
     *
     * @return The updated {@link DeveloperDto} instance as a HATEOAS response.
     */
    @AllowedForModerator
    @PutMapping
    public EntityModel<DeveloperDto> update(@Validated @RequestBody DeveloperDto developerDto) {
        return developerRepresentationModelAssembler.toModel(developerService.update(developerDto));
    }

    /**
     * End-point that will attempt to patch the {@link DeveloperDto} that matches the given ID with the values
     * specified within the {@link JsonMergePatch}. The {@link JsonMergePatch} must contain valid data to be applied
     * to the {@link DeveloperDto}, otherwise an {@link ApiError} will be returned to the user with additional exception
     * data.
     *
     * The {@link JsonMergePatch} provided can contain JSON data that is not contained within the {@link DeveloperDto},
     * however it will not apply any unknown field, but instead ignore them. If the ID provided does not match
     * a {@link DeveloperDto} or the patch fails to apply, {@link ApiError} instances will be returned with additional
     * error information.
     *
     * @param id The ID of the {@link DeveloperDto} to patch.
     * @param jsonMergePatch The {@link JsonMergePatch} which contains JSON data to update the {@link DeveloperDto} with.
     *
     * @return The patched {@link DeveloperDto} instance.
     */
    @AllowedForModerator
    @PatchMapping(value = "/{id}", consumes = "application/merge-patch+json")
    public EntityModel<DeveloperDto> patch(@PathVariable long id, @RequestBody JsonMergePatch jsonMergePatch) {
        return developerRepresentationModelAssembler.toModel(developerService.patch(id, jsonMergePatch));
    }

    /**
     * End-point that will attempt to the delete the {@link DeveloperDto} instance associated with the given ID. If no {@link DeveloperDto}
     * is found that matches the ID, then an exception will be thrown and the end-point will return a {@link ApiError} along with
     * additional information.
     *
     * If the {@link DeveloperDto} is successfully deleted, no data will be returned but the endpoint will specify a response code of 204
     * (NO_CONTENT).
     *
     * @param id The ID of the {@link DeveloperDto} to delete.
     */
    @AllowedForModerator
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        developerService.deleteById(id);
    }
}
