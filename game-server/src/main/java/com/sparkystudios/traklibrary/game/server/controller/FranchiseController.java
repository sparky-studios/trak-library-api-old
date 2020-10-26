package com.sparkystudios.traklibrary.game.server.controller;

import com.sparkystudios.traklibrary.game.repository.specification.FranchiseSpecification;
import com.sparkystudios.traklibrary.game.repository.specification.GenreSpecification;
import com.sparkystudios.traklibrary.game.server.assembler.FranchiseRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.server.assembler.GameRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.service.FranchiseService;
import com.sparkystudios.traklibrary.game.service.GameService;
import com.sparkystudios.traklibrary.game.service.dto.FranchiseDto;
import com.sparkystudios.traklibrary.game.service.dto.GameDto;
import com.sparkystudios.traklibrary.security.annotation.AllowedForModerator;
import com.sparkystudios.traklibrary.security.annotation.AllowedForUser;
import com.sparkystudios.traklibrary.security.exception.ApiError;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.json.JsonMergePatch;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/franchises", produces = "application/vnd.traklibrary.v1.hal+json")
public class FranchiseController {

    private final FranchiseService franchiseService;
    private final GameService gameService;
    private final FranchiseRepresentationModelAssembler franchiseRepresentationModelAssembler;
    private final GameRepresentationModelAssembler gameRepresentationModelAssembler;

    /**
     * End-point that will attempt to save the given {@link FranchiseDto} request body to the underlying
     * persistence layer. The {@link FranchiseDto} must either be valid or have all of the required fields meet
     * its pre-requisite conditions in order to attempt a save to the persistence layer.
     *
     * If the {@link FranchiseDto} being saved contains an ID that matches an existing entity in the persistence layer,
     * the {@link FranchiseDto} will not be saved and a {@link ApiError} will
     * be returned with appropriate exceptions details.
     *
     * @param franchiseDto The {@link FranchiseDto} to save.
     *
     * @return The saved {@link FranchiseDto} instance as a HATEOAS response.
     */
    @AllowedForModerator
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<FranchiseDto> save(@Validated @RequestBody FranchiseDto franchiseDto) {
        return franchiseRepresentationModelAssembler.toModel(franchiseService.save(franchiseDto));
    }

    /**
     * End-point that will retrieve a {@link FranchiseDto} instance that matches the given ID and convert
     * it into a consumable HATEOAS response. If a {@link FranchiseDto} instance is found that matches the ID, then
     * that data is returned with a status of 200, however if the {@link FranchiseDto} cannot be found the method
     * will return a 404 and wrap the exception details in a {@link ApiError} with additional information.
     *
     * @param id The ID of the {@link FranchiseDto} to retrieve.
     *
     * @return The {@link FranchiseDto} that matches the given ID as a HATEOAS response.
     */
    @AllowedForUser
    @GetMapping("/{id}")
    public EntityModel<FranchiseDto> findById(@PathVariable long id) {
        return franchiseRepresentationModelAssembler.toModel(franchiseService.findById(id));
    }

    /**
     * End-point that will retrieve a {@link PagedModel} of {@link GameDto}s that have a link to the specified
     * {@link FranchiseDto}. If the ID doesn't match an existing {@link FranchiseDto}, then an {@link ApiError} will be
     * returned with additional error details. If the {@link FranchiseDto} exists but has no associated
     * {@link GameDto}'s, then an empty {@link PagedModel} will be returned.
     *
     * @param id The ID of the {@link FranchiseDto} to retrieve associated {@link GameDto}'s for.
     * @param pageable The size and ordering of the page to retrieve.
     * @param pagedResourcesAssembler The assembler used to convert the {@link GameDto}'s to a HATEOAS page.
     *
     * @return A {@link PagedModel} of {@link GameDto}'s that are associated with the given {@link FranchiseDto}.
     */
    @AllowedForUser
    @GetMapping("/{id}/games")
    public PagedModel<EntityModel<GameDto>> findGamesByFranchiseId(@PathVariable long id,
                                                                   @PageableDefault Pageable pageable,
                                                                   PagedResourcesAssembler<GameDto> pagedResourcesAssembler) {
        // The self, next and prev links won't include query parameters if not built manually.
        Link link = new Link(ServletUriComponentsBuilder.fromCurrentRequest().build()
                .toUriString())
                .withSelfRel();

        // Get the paged data from the service and convert into a list so it can be added to a page object.
        List<GameDto> gameDtos = StreamSupport.stream(gameService.findGamesByFranchiseId(id, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Get the total number of entities that match the given criteria, dis-regarding page sizing.
        long count = gameService.countGamesByFranchiseId(id);

        // Wrap the page in a HATEOAS response.
        return pagedResourcesAssembler
                .toModel(new PageImpl<>(gameDtos, pageable, count), gameRepresentationModelAssembler, link);
    }

    /**
     * End-point that can be used to retrieve a paged result of {@link FranchiseDto} instances, that are filtered by
     * the provided {@link GenreSpecification} which appear as request parameters on the URL. The page and each
     * {@link FranchiseDto} will be wrapped in a HATEOAS response. If no {@link FranchiseDto} match the given criteria,
     * an empty HATEOAS page response will be returned.
     *
     * If any exceptions are thrown internally, and {@link ApiError} response will be returned with additional
     * error details.
     *
     * @param franchiseSpecification The filter queries to filter the page by.
     * @param pageable The size, page and ordering of the {@link FranchiseDto} elements in the page.
     * @param pagedResourcesAssembler Injected, used to convert the {@link FranchiseDto}s into a {@link PagedModel}.
     *
     * @return A {@link PagedModel} containing the {@link FranchiseDto} that match the requested page and criteria.
     */
    @AllowedForUser
    @GetMapping
    public PagedModel<EntityModel<FranchiseDto>> findAll(FranchiseSpecification franchiseSpecification,
                                                         @PageableDefault Pageable pageable,
                                                         PagedResourcesAssembler<FranchiseDto> pagedResourcesAssembler) {
        // The self, next and prev links won't include query parameters if not built manually.
        Link link = new Link(ServletUriComponentsBuilder.fromCurrentRequest().build()
                .toUriString())
                .withSelfRel();

        // Get the paged data from the service and convert into a list so it can be added to a page object.
        List<FranchiseDto> franchiseDtos = StreamSupport.stream(franchiseService.findAll(franchiseSpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Get the total number of entities that match the given criteria, dis-regarding page sizing.
        long count = franchiseService.count(franchiseSpecification);

        // Wrap the page in a HATEOAS response.
        return pagedResourcesAssembler
                .toModel(new PageImpl<>(franchiseDtos, pageable, count), franchiseRepresentationModelAssembler, link);
    }

    /**
     * End-point that will attempt to updated the given {@link FranchiseDto} request body to the underlying
     * persistence layer. The {@link FranchiseDto} must either be valid or have all of the required fields meet
     * its pre-requisite conditions in order to attempt an update in the persistence layer.
     *
     * If the {@link FranchiseDto} being saved doesn't contain an ID that matches an existing entity in the persistence layer,
     * the {@link FranchiseDto} will not be updated and a {@link ApiError} will be returned with appropriate exceptions details.
     *
     * @param franchiseDto The {@link FranchiseDto} to updated.
     *
     * @return The updated {@link FranchiseDto} instance as a HATEOAS response.
     */
    @AllowedForModerator
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<FranchiseDto> update(@Validated @RequestBody FranchiseDto franchiseDto) {
        return franchiseRepresentationModelAssembler.toModel(franchiseService.update(franchiseDto));
    }

    /**
     * End-point that will attempt to patch the {@link FranchiseDto} that matches the given ID with the values
     * specified within the {@link JsonMergePatch}. The {@link JsonMergePatch} must contain valid data to be applied
     * to the {@link FranchiseDto}, otherwise an {@link ApiError} will be returned to the user with additional exception
     * data.
     *
     * The {@link JsonMergePatch} provided can contain JSON data that is not contained within the {@link FranchiseDto},
     * however it will not apply any unknown field, but instead ignore them. If the ID provided does not match
     * a {@link FranchiseDto} or the patch fails to apply, {@link ApiError} instances will be returned with additional
     * error information.
     *
     * @param id The ID of the {@link FranchiseDto} to patch.
     * @param jsonMergePatch The {@link JsonMergePatch} which contains JSON data to update the {@link FranchiseDto} with.
     *
     * @return The patched {@link FranchiseDto} instance.
     */
    @AllowedForModerator
    @PatchMapping(value = "/{id}", consumes = "application/merge-patch+json")
    public EntityModel<FranchiseDto> patch(@PathVariable long id, @RequestBody JsonMergePatch jsonMergePatch) {
        return franchiseRepresentationModelAssembler.toModel(franchiseService.patch(id, jsonMergePatch));
    }

    /**
     * End-point that will attempt to the delete the {@link FranchiseDto} instance associated with the given ID. If no {@link FranchiseDto}
     * is found that matches the ID, then an exception will be thrown and the end-point will return a {@link ApiError} along with
     * additional information.
     *
     * If the {@link FranchiseDto} is successfully deleted, no data will be returned but the endpoint will specify a response code of 204
     * (NO_CONTENT).
     *
     * @param id The ID of the {@link FranchiseDto} to delete.
     */
    @AllowedForModerator
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        franchiseService.deleteById(id);
    }
}
