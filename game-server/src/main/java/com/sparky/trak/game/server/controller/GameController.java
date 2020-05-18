package com.sparky.trak.game.server.controller;

import com.sparky.trak.game.repository.specification.GameSpecification;
import com.sparky.trak.game.server.annotation.AllowedForModerator;
import com.sparky.trak.game.server.annotation.AllowedForUser;
import com.sparky.trak.game.server.assembler.*;
import com.sparky.trak.game.service.*;
import com.sparky.trak.game.service.dto.*;
import com.sparky.trak.game.server.exception.ApiError;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.*;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.json.JsonMergePatch;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * The {@link GameController} is a simple controller class that exposes a CRUD based API that is used to interact with
 * any entities or objects that pertain to games. It provides API end-points for creating, updating, finding and deleting game
 * entities. It should be noted that the controller itself contains very little logic, the logic is contained within the
 * {@link GameService}. The controllers primary purpose is to wrap the responses it received from the {@link GameService}
 * into HATEOAS responses. All mappings on this controller therefore produce a {@link MediaTypes#HAL_JSON} response.
 *
 * @since 1.0.0
 * @author Sparky Studios
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/games", produces = MediaTypes.HAL_JSON_VALUE)
public class GameController {

    private final GameService gameService;
    private final GenreService genreService;
    private final PlatformService platformService;
    private final DeveloperService developerService;
    private final PublisherService publisherService;
    private final GameRepresentationModelAssembler gameRepresentationModelAssembler;
    private final GenreRepresentationModelAssembler genreRepresentationModelAssembler;
    private final PlatformRepresentationModelAssembler platformRepresentationModelAssembler;
    private final DeveloperRepresentationModelAssembler developerRepresentationModelAssembler;
    private final PublisherRepresentationModelAssembler publisherRepresentationModelAssembler;

    /**
     * End-point that will attempt to save the given {@link GameDto} request body to the underlying
     * persistence layer. The {@link GameDto} must either be valid or have all of the required fields meet
     * its pre-requisite conditions in order to attempt a save to the persistence layer.
     *
     * If the {@link GameDto} being saved contains an ID that matches an existing entity in the persistence layer,
     * the {@link GameDto} will not be saved and a {@link com.sparky.trak.game.server.exception.ApiError} will
     * be returned with appropriate exceptions details.
     *
     * @param gameDto The {@link GameDto} to save.
     *
     * @return The saved {@link GameDto} instance as a HATEOAS response.
     */
    @AllowedForModerator
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public EntityModel<GameDto> save(@Validated @RequestBody GameDto gameDto) {
        return gameRepresentationModelAssembler.toModel(gameService.save(gameDto));
    }

    /**
     * End-point that will retrieve a {@link GameDto} instance that matches the given Id and convert
     * it into a consumable HATEOAS response. If a {@link GameDto} instance is found that matches the Id, then
     * that data is returned with a status of 200, however if the {@link GameDto} cannot be found the method
     * will return a 404 and wrap the exception details in a {@link ApiError} with additional information.
     *
     * @param id The ID of the {@link GameDto} to retrieve.
     *
     * @return The {@link GameDto} that matches the given ID as a HATEOAS response.
     */
    @AllowedForUser
    @GetMapping("/{id}")
    public EntityModel<GameDto> findById(@PathVariable long id) {
        return gameRepresentationModelAssembler.toModel(gameService.findById(id));
    }

    /**
     * End-point that will retrieve a {@link CollectionModel} of {@link GenreDto}s that are directly associated
     * with the {@link GameDto} that matches the given ID. If the ID doesn't match an existing {@link GameDto},
     * then an {@link ApiError} will be returned with additional error details. If the {@link GameDto} exists but
     * has no associated {@link GenreDto}'s, then an empty {@link CollectionModel} will be returned.
     *
     * @param id The ID of the {@link GameDto} to retrieve genre information for.
     *
     * @return A {@link CollectionModel} of {@link GenreDto}'s that are associated with the given {@link GameDto}.
     */
    @AllowedForUser
    @GetMapping("/{id}/genres")
    public CollectionModel<EntityModel<GenreDto>> findGenresByGameId(@PathVariable long id) {
        return genreRepresentationModelAssembler.toCollectionModel(genreService.findGenresByGameId(id));
    }

    /**
     * End-point that will retrieve a {@link CollectionModel} of {@link PlatformDto}s that are directly associated
     * with the {@link GameDto} that matches the given ID. If the ID doesn't match an existing {@link GameDto},
     * then an {@link ApiError} will be returned with additional error details. If the {@link GameDto} exists but
     * has no associated {@link PlatformDto}'s, then an empty {@link CollectionModel} will be returned.
     *
     * @param id The ID of the {@link GameDto} to retrieve genre information for.
     *
     * @return A {@link CollectionModel} of {@link PlatformDto}'s that are associated with the given {@link GameDto}.
     */
    @AllowedForUser
    @GetMapping("/{id}/platforms")
    public PagedModel<EntityModel<PlatformDto>> findPlatformsByGameId(@PathVariable long id,
                                                                      @PageableDefault Pageable pageable,
                                                                      PagedResourcesAssembler<PlatformDto> pagedResourcesAssembler) {
        // The self, next and prev links won't include query parameters if not built manually.
        Link link = new Link(ServletUriComponentsBuilder.fromCurrentRequest().build()
                .toUriString())
                .withSelfRel();

        // Get the paged data from the service and convert into a list so it can be added to a page object.
        List<PlatformDto> platformDtos = StreamSupport.stream(platformService.findPlatformsByGameId(id, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Get the total number of entities that match the given criteria, dis-regarding page sizing.
        long count = platformService.countPlatformsByGameId(id);

        // Wrap the page in a HATEOAS response.
        return pagedResourcesAssembler
                .toModel(new PageImpl<>(platformDtos, pageable, count), platformRepresentationModelAssembler, link);
    }

    /**
     * End-point that will retrieve a {@link CollectionModel} of {@link DeveloperDto}s that are directly associated
     * with the {@link GameDto} that matches the given ID. If the ID doesn't match an existing {@link GameDto},
     * then an {@link ApiError} will be returned with additional error details. If the {@link GameDto} exists but
     * has no associated {@link DeveloperDto}'s, then an empty {@link CollectionModel} will be returned.
     *
     * @param id The ID of the {@link GameDto} to retrieve genre information for.
     *
     * @return A {@link CollectionModel} of {@link DeveloperDto}'s that are associated with the given {@link GameDto}.
     */
    @AllowedForUser
    @GetMapping("/{id}/developers")
    public PagedModel<EntityModel<DeveloperDto>> findDevelopersByGameId(@PathVariable long id,
                                                                        @PageableDefault Pageable pageable,
                                                                        PagedResourcesAssembler<DeveloperDto> pagedResourcesAssembler) {
        // The self, next and prev links won't include query parameters if not built manually.
        Link link = new Link(ServletUriComponentsBuilder.fromCurrentRequest().build()
                .toUriString())
                .withSelfRel();

        // Get the paged data from the service and convert into a list so it can be added to a page object.
        List<DeveloperDto> developerDtos = StreamSupport.stream(developerService.findDevelopersByGameId(id, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Get the total number of entities that match the given criteria, dis-regarding page sizing.
        long count = developerService.countDevelopersByGameId(id);

        // Wrap the page in a HATEOAS response.
        return pagedResourcesAssembler
                .toModel(new PageImpl<>(developerDtos, pageable, count), developerRepresentationModelAssembler, link);
    }

    /**
     * End-point that will retrieve a {@link CollectionModel} of {@link PublisherDto}s that are directly associated
     * with the {@link GameDto} that matches the given ID. If the ID doesn't match an existing {@link GameDto},
     * then an {@link ApiError} will be returned with additional error details. If the {@link GameDto} exists but
     * has no associated {@link PublisherDto}'s, then an empty {@link CollectionModel} will be returned.
     *
     * @param id The ID of the {@link GameDto} to retrieve genre information for.
     *
     * @return A {@link CollectionModel} of {@link PublisherDto}'s that are associated with the given {@link GameDto}.
     */
    @AllowedForUser
    @GetMapping("/{id}/publishers")
    public PagedModel<EntityModel<PublisherDto>> findPublishersByGameId(@PathVariable long id,
                                                                        @PageableDefault Pageable pageable,
                                                                        PagedResourcesAssembler<PublisherDto> pagedResourcesAssembler) {
        // The self, next and prev links won't include query parameters if not built manually.
        Link link = new Link(ServletUriComponentsBuilder.fromCurrentRequest().build()
                .toUriString())
                .withSelfRel();

        // Get the paged data from the service and convert into a list so it can be added to a page object.
        List<PublisherDto> publisherDtos = StreamSupport.stream(publisherService.findPublishersByGameId(id, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Get the total number of entities that match the given criteria, dis-regarding page sizing.
        long count = publisherService.countPublishersByGameId(id);

        // Wrap the page in a HATEOAS response.
        return pagedResourcesAssembler
                .toModel(new PageImpl<>(publisherDtos, pageable, count), publisherRepresentationModelAssembler, link);
    }

    /**
     * End-point that can be used to retrieve a paged result of {@link GameDto} instances, that are filtered by
     * the provided {@link GameSpecification} which appear as request parameters on the URL. The page and each
     * {@link GameDto} will be wrapped in a HATEOAS response. If no {@link GameDto} match the given criteria,
     * an empty HATEOAS page response will be returned.
     *
     * If any exceptions are thrown internally, and {@link ApiError} response will be returned with additional
     * error details.
     *
     * @param gameSpecification The filter queries to filter the page by.
     * @param pageable The size, page and ordering of the {@link GameDto} elements in the page.
     * @param pagedResourcesAssembler Injected, used to convert the {@link GameDto}s into a {@link PagedModel}.
     *
     * @return A {@link PagedModel} containing the {@link GameDto} that match the requested page and criteria.
     */
    @AllowedForUser
    @GetMapping
    public PagedModel<EntityModel<GameDto>> findAll(GameSpecification gameSpecification,
                                                    @PageableDefault Pageable pageable,
                                                    PagedResourcesAssembler<GameDto> pagedResourcesAssembler) {
        // The self, next and prev links won't include query parameters if not built manually.
        Link link = new Link(ServletUriComponentsBuilder.fromCurrentRequest().build()
                .toUriString())
                .withSelfRel();

        // Get the paged data from the service and convert into a list so it can be added to a page object.
        List<GameDto> gameDtos = StreamSupport.stream(gameService.findAll(gameSpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Get the total number of entities that match the given criteria, dis-regarding page sizing.
        long count = gameService.count(gameSpecification);

        // Wrap the page in a HATEOAS response.
        return pagedResourcesAssembler.toModel(new PageImpl<>(gameDtos, pageable, count), gameRepresentationModelAssembler, link);
    }

    /**
     * End-point that will attempt to updated the given {@link GameDto} request body to the underlying
     * persistence layer. The {@link GameDto} must either be valid or have all of the required fields meet
     * its pre-requisite conditions in order to attempt an update in the persistence layer.
     *
     * If the {@link GameDto} being saved doesn't contain an ID that matches an existing entity in the persistence layer,
     * the {@link GameDto} will not be updated and a {@link ApiError} will
     * be returned with appropriate exceptions details.
     *
     * @param gameDto The {@link GameDto} to updated.
     *
     * @return The updated {@link GameDto} instance as a HATEOAS response.
     */
    @AllowedForModerator
    @PutMapping
    public EntityModel<GameDto> update(@Validated @RequestBody GameDto gameDto) {
        return gameRepresentationModelAssembler.toModel(gameService.update(gameDto));
    }

    /**
     * End-point that will attempt to patch the {@link GameDto} that matches the given ID with the values
     * specified within the {@link JsonMergePatch}. The {@link JsonMergePatch} must contain valid data to be applied
     * to the {@link GameDto}, otherwise an {@link ApiError} will be returned to the user with additional exception
     * data.
     *
     * The {@link JsonMergePatch} provided can contain JSON data that is not contained within the {@link GameDto},
     * however it will not apply any unknown field, but instead ignore them. If the ID provided does not match
     * a {@link GameDto} or the patch fails to apply, {@link ApiError} instances will be returned with additional
     * error information.
     *
     * @param id The ID of the {@link GameDto} to patch.
     * @param jsonMergePatch The {@link JsonMergePatch} which contains JSON data to update the {@link GameDto} with.
     *
     * @return The patched {@link GameDto} instance.
     */
    @AllowedForModerator
    @PatchMapping(value = "/{id}", consumes = "application/merge-patch+json")
    public EntityModel<GameDto> patch(@PathVariable long id, @RequestBody JsonMergePatch jsonMergePatch) {
        return gameRepresentationModelAssembler.toModel(gameService.patch(id, jsonMergePatch));
    }

    /**
     * End-point that will attempt to the delete the {@link GameDto} instance associated with the given ID. If no {@link GameDto}
     * is found that matches the ID, then an exception will be thrown and the end-point will return a {@link ApiError} along with
     * additional information.
     *
     * If the {@link GameDto} is successfully deleted, no data will be returned but the endpoint will specify a response code of 204
     * (NO_CONTENT).
     *
     * @param id The ID of the {@link GameDto} to delete.
     */
    @AllowedForModerator
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        gameService.deleteById(id);
    }
}
