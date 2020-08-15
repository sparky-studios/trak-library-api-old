package com.traklibrary.game.server.controller;

import com.traklibrary.game.repository.specification.PlatformSpecification;
import com.traklibrary.game.server.annotation.AllowedForModerator;
import com.traklibrary.game.server.annotation.AllowedForUser;
import com.traklibrary.game.service.PlatformService;
import com.traklibrary.game.service.GameService;
import com.traklibrary.game.service.dto.PlatformDto;
import com.traklibrary.game.service.dto.GameDto;
import com.traklibrary.game.server.assembler.PlatformRepresentationModelAssembler;
import com.traklibrary.game.server.assembler.GameRepresentationModelAssembler;
import com.traklibrary.game.server.exception.ApiError;
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
@RequestMapping(value = "/platforms", produces = "application/vnd.traklibrary.v1.0.hal+json")
public class PlatformController {

    private final PlatformService platformService;
    private final GameService gameService;
    private final PlatformRepresentationModelAssembler platformRepresentationModelAssembler;
    private final GameRepresentationModelAssembler gameRepresentationModelAssembler;

    /**
     * End-point that will attempt to save the given {@link PlatformDto} request body to the underlying
     * persistence layer. The {@link PlatformDto} must either be valid or have all of the required fields meet
     * its pre-requisite conditions in order to attempt a save to the persistence layer.
     *
     * If the {@link PlatformDto} being saved contains an ID that matches an existing entity in the persistence layer,
     * the {@link PlatformDto} will not be saved and a {@link ApiError} will
     * be returned with appropriate exceptions details.
     *
     * @param platformDto The {@link PlatformDto} to save.
     *
     * @return The saved {@link PlatformDto} instance as a HATEOAS response.
     */
    @AllowedForModerator
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<PlatformDto> save(@Validated @RequestBody PlatformDto platformDto) {
        return platformRepresentationModelAssembler.toModel(platformService.save(platformDto));
    }

    /**
     * End-point that will retrieve a {@link PlatformDto} instance that matches the given ID and convert
     * it into a consumable HATEOAS response. If a {@link PlatformDto} instance is found that matches the ID, then
     * that data is returned with a status of 200, however if the {@link PlatformDto} cannot be found the method
     * will return a 404 and wrap the exception details in a {@link ApiError} with additional information.
     *
     * @param id The ID of the {@link PlatformDto} to retrieve.
     *
     * @return The {@link PlatformDto} that matches the given ID as a HATEOAS response.
     */
    @AllowedForUser
    @GetMapping("/{id}")
    public EntityModel<PlatformDto> findById(@PathVariable long id) {
        return platformRepresentationModelAssembler.toModel(platformService.findById(id));
    }

    /**
     * End-point that will retrieve a {@link PagedModel} of {@link GameDto}s that have a link to the specified
     * {@link PlatformDto}. If the ID doesn't match an existing {@link PlatformDto}, then an {@link ApiError} will be
     * returned with additional error details. If the {@link PlatformDto} exists but has no associated
     * {@link GameDto}'s, then an empty {@link PagedModel} will be returned.
     *
     * @param id The ID of the {@link PlatformDto} to retrieve associated {@link GameDto}'s for.
     * @param pageable The size and ordering of the page to retrieve.
     * @param pagedResourcesAssembler The assembler used to convert the {@link GameDto}'s to a HATEOAS page.
     *
     * @return A {@link PagedModel} of {@link GameDto}'s that are associated with the given {@link PlatformDto}.
     */
    @AllowedForUser
    @GetMapping("/{id}/games")
    public PagedModel<EntityModel<GameDto>> findGamesByPlatformId(@PathVariable long id,
                                                                 @PageableDefault Pageable pageable,
                                                                 PagedResourcesAssembler<GameDto> pagedResourcesAssembler) {
        // The self, next and prev links won't include query parameters if not built manually.
        Link link = new Link(ServletUriComponentsBuilder.fromCurrentRequest().build()
                .toUriString())
                .withSelfRel();

        // Get the paged data from the service and convert into a list so it can be added to a page object.
        List<GameDto> gameDtos = StreamSupport.stream(gameService.findGamesByPlatformId(id, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Get the total number of entities that match the given criteria, dis-regarding page sizing.
        long count = gameService.countGamesByPlatformId(id);

        // Wrap the page in a HATEOAS response.
        return pagedResourcesAssembler
                .toModel(new PageImpl<>(gameDtos, pageable, count), gameRepresentationModelAssembler, link);
    }

    /**
     * End-point that can be used to retrieve a paged result of {@link PlatformDto} instances, that are filtered by
     * the provided {@link PlatformSpecification} which appear as request parameters on the URL. The page and each
     * {@link PlatformDto} will be wrapped in a HATEOAS response. If no {@link PlatformDto} match the given criteria,
     * an empty HATEOAS page response will be returned.
     *
     * If any exceptions are thrown internally, and {@link ApiError} response will be returned with additional
     * error details.
     *
     * @param platformSpecification The filter queries to filter the page by.
     * @param pageable The size, page and ordering of the {@link PlatformDto} elements in the page.
     * @param pagedResourcesAssembler Injected, used to convert the {@link PlatformDto}s into a {@link PagedModel}.
     *
     * @return A {@link PagedModel} containing the {@link PlatformDto} that match the requested page and criteria.
     */
    @AllowedForUser
    @GetMapping
    public PagedModel<EntityModel<PlatformDto>> findAll(PlatformSpecification platformSpecification,
                                                        @PageableDefault Pageable pageable,
                                                        PagedResourcesAssembler<PlatformDto> pagedResourcesAssembler) {
        // The self, next and prev links won't include query parameters if not built manually.
        Link link = new Link(ServletUriComponentsBuilder.fromCurrentRequest().build()
                .toUriString())
                .withSelfRel();

        // Get the paged data from the service and convert into a list so it can be added to a page object.
        List<PlatformDto> platformDtos = StreamSupport.stream(platformService.findAll(platformSpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Get the total number of entities that match the given criteria, dis-regarding page sizing.
        long count = platformService.count(platformSpecification);

        // Wrap the page in a HATEOAS response.
        return pagedResourcesAssembler
                .toModel(new PageImpl<>(platformDtos, pageable, count), platformRepresentationModelAssembler, link);
    }

    /**
     * End-point that will attempt to updated the given {@link PlatformDto} request body to the underlying
     * persistence layer. The {@link PlatformDto} must either be valid or have all of the required fields meet
     * its pre-requisite conditions in order to attempt an update in the persistence layer.
     *
     * If the {@link PlatformDto} being saved doesn't contain an ID that matches an existing entity in the persistence layer,
     * the {@link PlatformDto} will not be updated and a {@link ApiError} will be returned with appropriate exceptions details.
     *
     * @param platformDto The {@link PlatformDto} to updated.
     *
     * @return The updated {@link PlatformDto} instance as a HATEOAS response.
     */
    @AllowedForModerator
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<PlatformDto> update(@Validated @RequestBody PlatformDto platformDto) {
        return platformRepresentationModelAssembler.toModel(platformService.update(platformDto));
    }

    /**
     * End-point that will attempt to patch the {@link PlatformDto} that matches the given ID with the values
     * specified within the {@link JsonMergePatch}. The {@link JsonMergePatch} must contain valid data to be applied
     * to the {@link PlatformDto}, otherwise an {@link ApiError} will be returned to the user with additional exception
     * data.
     *
     * The {@link JsonMergePatch} provided can contain JSON data that is not contained within the {@link PlatformDto},
     * however it will not apply any unknown field, but instead ignore them. If the ID provided does not match
     * a {@link PlatformDto} or the patch fails to apply, {@link ApiError} instances will be returned with additional
     * error information.
     *
     * @param id The ID of the {@link PlatformDto} to patch.
     * @param jsonMergePatch The {@link JsonMergePatch} which contains JSON data to update the {@link PlatformDto} with.
     *
     * @return The patched {@link PlatformDto} instance.
     */
    @AllowedForModerator
    @PatchMapping(value = "/{id}", consumes = "application/merge-patch+json")
    public EntityModel<PlatformDto> patch(@PathVariable long id, @RequestBody JsonMergePatch jsonMergePatch) {
        return platformRepresentationModelAssembler.toModel(platformService.patch(id, jsonMergePatch));
    }

    /**
     * End-point that will attempt to the delete the {@link PlatformDto} instance associated with the given ID. If no {@link PlatformDto}
     * is found that matches the ID, then an exception will be thrown and the end-point will return a {@link ApiError} along with
     * additional information.
     *
     * If the {@link PlatformDto} is successfully deleted, no data will be returned but the endpoint will specify a response code of 204
     * (NO_CONTENT).
     *
     * @param id The ID of the {@link PlatformDto} to delete.
     */
    @AllowedForModerator
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        platformService.deleteById(id);
    }
}
