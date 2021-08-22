package com.sparkystudios.traklibrary.game.server.controller;

import com.sparkystudios.traklibrary.game.repository.specification.GameSpecification;
import com.sparkystudios.traklibrary.game.repository.specification.GameUserEntrySpecification;
import com.sparkystudios.traklibrary.game.server.assembler.*;
import com.sparkystudios.traklibrary.game.service.*;
import com.sparkystudios.traklibrary.game.service.dto.*;
import com.sparkystudios.traklibrary.game.service.dto.request.NewGameRequest;
import com.sparkystudios.traklibrary.game.service.dto.request.UpdateGameRequest;
import com.sparkystudios.traklibrary.security.annotation.AllowedForModeratorWithGameDeleteAuthority;
import com.sparkystudios.traklibrary.security.annotation.AllowedForModeratorWithGameWriteAuthority;
import com.sparkystudios.traklibrary.security.annotation.AllowedForUser;
import com.sparkystudios.traklibrary.security.exception.ApiError;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.json.JsonMergePatch;
import java.util.Collection;
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
 * @since 0.1.0
 * @author Sparky Studios
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/", produces = "application/vnd.sparkystudios.traklibrary-hal+json;version=1.0")
public class GameController {

    private final GameService gameService;
    private final GameDetailsService gameDetailsService;
    private final GenreService genreService;
    private final PlatformService platformService;
    private final DeveloperService developerService;
    private final PublisherService publisherService;
    private final DownloadableContentService downloadableContentService;
    private final GameUserEntryService gameUserEntryService;
    private final GameRepresentationModelAssembler gameRepresentationModelAssembler;
    private final GameDetailsRepresentationModelAssembler gameDetailsRepresentationModelAssembler;
    private final GenreRepresentationModelAssembler genreRepresentationModelAssembler;
    private final PlatformRepresentationModelAssembler platformRepresentationModelAssembler;
    private final DeveloperRepresentationModelAssembler developerRepresentationModelAssembler;
    private final PublisherRepresentationModelAssembler publisherRepresentationModelAssembler;
    private final DownloadableContentRepresentationModelAssembler downloadableContentRepresentationModelAssembler;
    private final GameUserEntryRepresentationModelAssembler gameUserEntryRepresentationModelAssembler;

    /**
     * End-point that will attempt to save the given {@link NewGameRequest} request body to the underlying
     * persistence layer. The {@link NewGameRequest} must either be valid or have all of the required fields meet
     * its pre-requisite conditions in order to attempt a save to the persistence layer.
     *
     * @param newGameRequest The {@link NewGameRequest} to save.
     *
     * @return The saved {@link GameDto} instance as a HATEOAS response.
     */
    @AllowedForModeratorWithGameWriteAuthority
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<GameDto> save(@Validated @RequestBody NewGameRequest newGameRequest) {
        return gameRepresentationModelAssembler.toModel(gameService.save(newGameRequest));
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
     * End-point that will retrieve a {@link GameDto} instance that matches the given slug and convert
     * it into a consumable HATEOAS response. If a {@link GameDto} instance is found that matches the slug, then
     * that data is returned with a status of 200, however if the {@link GameDto} cannot be found the method
     * will return a 404 and wrap the exception details in a {@link ApiError} with additional information.
     *
     * @param slug The slug of the {@link GameDto} to retrieve.
     *
     * @return The {@link GameDto} that matches the given slug as a HATEOAS response.
     */
    @AllowedForUser
    @GetMapping("/slug/{slug}")
    public EntityModel<GameDto> findBySlug(@PathVariable String slug) {
        return gameRepresentationModelAssembler.toModel(gameService.findBySlug(slug));
    }

    /**
     * End-point that will retrieve a {@link GameDetailsDto} instance that matches the given Id and convert
     * it into a consumable HATEOAS response. If a {@link GameDetailsDto} instance is found that matches the Id, then
     * that data is returned with a status of 200, however if the {@link GameDetailsDto} cannot be found the method
     * will return a 404 and wrap the exception details in a {@link ApiError} with additional information.
     *
     * @param id The ID of the {@link GameDetailsDto} to retrieve.
     *
     * @return The {@link GameDetailsDto} that matches the given ID as a HATEOAS response.
     */
    @AllowedForUser
    @GetMapping("/{id}/details")
    public EntityModel<GameDetailsDto> findGameDetailsByGameId(@PathVariable long id) {
        return gameDetailsRepresentationModelAssembler.toModel(gameDetailsService.findByGameId(id));
    }

    /**
     * End-point that will retrieve a {@link GameDetailsDto} instance that matches the given slug and convert
     * it into a consumable HATEOAS response. If a {@link GameDetailsDto} instance is found that matches the slug, then
     * that data is returned with a status of 200, however if the {@link GameDetailsDto} cannot be found the method
     * will return a 404 and wrap the exception details in a {@link ApiError} with additional information.
     *
     * @param slug The slug of the {@link GameDetailsDto} to retrieve.
     *
     * @return The {@link GameDetailsDto} that matches the given slug as a HATEOAS response.
     */
    @AllowedForUser
    @GetMapping("/slug/{slug}/details")
    public EntityModel<GameDetailsDto> findGameDetailsByGameSlug(@PathVariable String slug) {
        return gameDetailsRepresentationModelAssembler.toModel(gameDetailsService.findByGameSlug(slug));
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
     * End-point that can be used to associate a {@link Collection} of {@link GenreDto} ID's with a given {@link GameDto}
     * that matches the provided ID. If the ID provided doesn't match an existing {@link GameDto} than an {@link ApiError}
     * will be returned with additional error details. If any of the {@link GenreDto} ID's don't currently exist within the
     * database, then an association will not be created.
     *
     * It should be noted, that this method will remove all existing {@link GenreDto} associations and replace with the
     * ID's provided.
     *
     * @param id The ID of the {@link GameDto} to associated {@link GenreDto}'s with.
     * @param genreIds A {@link Collection} of {@link GenreDto} ID's to associate with the {@link GameDto}.
     *
     * @return The {@link GameDto} that the {@link GenreDto} ID's have been associated with.
     */
    @AllowedForModeratorWithGameWriteAuthority
    @PutMapping(value = "/{id}/genres", consumes = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<GameDto> saveGenresForGameId(@PathVariable long id, @RequestBody Collection<Long> genreIds) {
        return gameRepresentationModelAssembler.toModel(gameService.saveGenresForGameId(id, genreIds));
    }

    /**
     * End-point that can be used to associate a {@link Collection} of {@link GenreDto} ID's with a given {@link GameDto}
     * that matches the provided ID. If the ID provided doesn't match an existing {@link GameDto} than an {@link ApiError}
     * will be returned with additional error details. If any of the {@link GenreDto} ID's don't currently exist within the
     * database, then an association will not be created.
     *
     * It should be noted, that this method will only update the {@link GameDto} with new associations, no existing
     * associations will be removed.
     *
     * @param id The ID of the {@link GameDto} to associated {@link GenreDto}'s with.
     * @param genreIds A {@link Collection} of {@link GenreDto} ID's to associate with the {@link GameDto}.
     *
     * @return The {@link GameDto} that the {@link GenreDto} ID's have been associated with.
     */
    @AllowedForModeratorWithGameWriteAuthority
    @PostMapping(value = "/{id}/genres", consumes = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<GameDto> updateGenresForGameId(@PathVariable long id, @RequestBody Collection<Long> genreIds) {
        return gameRepresentationModelAssembler.toModel(gameService.updateGenresForGameId(id, genreIds));
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
    public CollectionModel<EntityModel<PlatformDto>> findPlatformsByGameId(@PathVariable long id) {
        return platformRepresentationModelAssembler.toCollectionModel(platformService.findPlatformsByGameId(id));
    }

    /**
     * End-point that can be used to associate a {@link Collection} of {@link PlatformDto} ID's with a given {@link GameDto}
     * that matches the provided ID. If the ID provided doesn't match an existing {@link GameDto} than an {@link ApiError}
     * will be returned with additional error details. If any of the {@link PlatformDto} ID's don't currently exist within the
     * database, then an association will not be created.
     *
     * It should be noted, that this method will remove all existing {@link PlatformDto} associations and replace with the
     * ID's provided.
     *
     * @param id The ID of the {@link GameDto} to associated {@link PlatformDto}'s with.
     * @param platformIds A {@link Collection} of {@link PlatformDto} ID's to associate with the {@link GameDto}.
     *
     * @return The {@link GameDto} that the {@link PlatformDto} ID's have been associated with.
     */
    @AllowedForModeratorWithGameWriteAuthority
    @PutMapping(value = "/{id}/platforms", consumes = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<GameDto> savePlatformsForGameId(@PathVariable long id, @RequestBody Collection<Long> platformIds) {
        return gameRepresentationModelAssembler.toModel(gameService.savePlatformsForGameId(id, platformIds));
    }

    /**
     * End-point that can be used to associate a {@link Collection} of {@link PlatformDto} ID's with a given {@link GameDto}
     * that matches the provided ID. If the ID provided doesn't match an existing {@link GameDto} than an {@link ApiError}
     * will be returned with additional error details. If any of the {@link PlatformDto} ID's don't currently exist within the
     * database, then an association will not be created.
     *
     * It should be noted, that this method will only update the {@link PlatformDto} with new associations, no existing
     * associations will be removed.
     *
     * @param id The ID of the {@link GameDto} to associated {@link PlatformDto}'s with.
     * @param platformIds A {@link Collection} of {@link PlatformDto} ID's to associate with the {@link GameDto}.
     *
     * @return The {@link GameDto} that the {@link PlatformDto} ID's have been associated with.
     */
    @AllowedForModeratorWithGameWriteAuthority
    @PostMapping(value = "/{id}/platforms", consumes = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<GameDto> updatePlatformsForGameId(@PathVariable long id, @RequestBody Collection<Long> platformIds) {
        return gameRepresentationModelAssembler.toModel(gameService.updatePlatformsForGameId(id, platformIds));
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
    public CollectionModel<EntityModel<DeveloperDto>> findDevelopersByGameId(@PathVariable long id) {
        return developerRepresentationModelAssembler.toCollectionModel(developerService.findDevelopersByGameId(id));
    }

    /**
     * End-point that can be used to associate a {@link Collection} of {@link DeveloperDto} ID's with a given {@link GameDto}
     * that matches the provided ID. If the ID provided doesn't match an existing {@link GameDto} than an {@link ApiError}
     * will be returned with additional error details. If any of the {@link DeveloperDto} ID's don't currently exist within the
     * database, then an association will not be created.
     *
     * It should be noted, that this method will remove all existing {@link DeveloperDto} associations and replace with the
     * ID's provided.
     *
     * @param id The ID of the {@link GameDto} to associated {@link DeveloperDto}'s with.
     * @param developerIds A {@link Collection} of {@link DeveloperDto} ID's to associate with the {@link GameDto}.
     *
     * @return The {@link GameDto} that the {@link DeveloperDto} ID's have been associated with.
     */
    @AllowedForModeratorWithGameWriteAuthority
    @PutMapping(value = "/{id}/developers", consumes = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<GameDto> saveDevelopersForGameId(@PathVariable long id, @RequestBody Collection<Long> developerIds) {
        return gameRepresentationModelAssembler.toModel(gameService.saveDevelopersForGameId(id, developerIds));
    }

    /**
     * End-point that can be used to associate a {@link Collection} of {@link DeveloperDto} ID's with a given {@link GameDto}
     * that matches the provided ID. If the ID provided doesn't match an existing {@link GameDto} than an {@link ApiError}
     * will be returned with additional error details. If any of the {@link DeveloperDto} ID's don't currently exist within the
     * database, then an association will not be created.
     *
     * It should be noted, that this method will only update the {@link DeveloperDto} with new associations, no existing
     * associations will be removed.
     *
     * @param id The ID of the {@link GameDto} to associated {@link DeveloperDto}'s with.
     * @param developerIds A {@link Collection} of {@link DeveloperDto} ID's to associate with the {@link GameDto}.
     *
     * @return The {@link GameDto} that the {@link DeveloperDto} ID's have been associated with.
     */
    @AllowedForModeratorWithGameWriteAuthority
    @PostMapping(value = "/{id}/developers", consumes = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<GameDto> updateDevelopersForGameId(@PathVariable long id, @RequestBody Collection<Long> developerIds) {
        return gameRepresentationModelAssembler.toModel(gameService.updateDevelopersForGameId(id, developerIds));
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
    public CollectionModel<EntityModel<PublisherDto>> findPublishersByGameId(@PathVariable long id) {
        return publisherRepresentationModelAssembler.toCollectionModel(publisherService.findPublishersByGameId(id));
    }

    /**
     * End-point that can be used to associate a {@link Collection} of {@link PublisherDto} ID's with a given {@link GameDto}
     * that matches the provided ID. If the ID provided doesn't match an existing {@link GameDto} than an {@link ApiError}
     * will be returned with additional error details. If any of the {@link PublisherDto} ID's don't currently exist within the
     * database, then an association will not be created.
     *
     * It should be noted, that this method will remove all existing {@link PublisherDto} associations and replace with the
     * ID's provided.
     *
     * @param id The ID of the {@link GameDto} to associated {@link PublisherDto}'s with.
     * @param publisherIds A {@link Collection} of {@link PublisherDto} ID's to associate with the {@link GameDto}.
     *
     * @return The {@link GameDto} that the {@link PublisherDto} ID's have been associated with.
     */
    @AllowedForModeratorWithGameWriteAuthority
    @PutMapping(value = "/{id}/publishers", consumes = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<GameDto> savePublishersForGameId(@PathVariable long id, @RequestBody Collection<Long> publisherIds) {
        return gameRepresentationModelAssembler.toModel(gameService.savePublishersForGameId(id, publisherIds));
    }

    /**
     * End-point that can be used to associate a {@link Collection} of {@link PublisherDto} ID's with a given {@link GameDto}
     * that matches the provided ID. If the ID provided doesn't match an existing {@link GameDto} than an {@link ApiError}
     * will be returned with additional error details. If any of the {@link PublisherDto} ID's don't currently exist within the
     * database, then an association will not be created.
     *
     * It should be noted, that this method will only update the {@link PublisherDto} with new associations, no existing
     * associations will be removed.
     *
     * @param id The ID of the {@link GameDto} to associated {@link PublisherDto}'s with.
     * @param publisherIds A {@link Collection} of {@link PublisherDto} ID's to associate with the {@link GameDto}.
     *
     * @return The {@link GameDto} that the {@link PublisherDto} ID's have been associated with.
     */
    @AllowedForModeratorWithGameWriteAuthority
    @PostMapping(value = "/{id}/publishers", consumes = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<GameDto> updatePublishersForGameId(@PathVariable long id, @RequestBody Collection<Long> publisherIds) {
        return gameRepresentationModelAssembler.toModel(gameService.updatePublishersForGameId(id, publisherIds));
    }

    /**
     * End-point that will retrieve a {@link CollectionModel} of {@link DownloadableContentDto}s that are directly associated
     * with the {@link GameDto} that matches the given ID. If the ID doesn't match an existing {@link GameDto},
     * then an {@link ApiError} will be returned with additional error details. If the {@link GameDto} exists but
     * has no associated {@link DownloadableContentDto}'s, then an empty {@link CollectionModel} will be returned.
     *
     * @param id The ID of the {@link GameDto} to retrieve genre information for.
     *
     * @return A {@link CollectionModel} of {@link DownloadableContentDto}'s that are associated with the given {@link GameDto}.
     */
    @AllowedForUser
    @GetMapping("/{id}/dlc")
    public CollectionModel<EntityModel<DownloadableContentDto>> findDownloadableContentsByGameId(@PathVariable long id) {
        return downloadableContentRepresentationModelAssembler.toCollectionModel(downloadableContentService.findDownloadableContentsByGameId(id));
    }

    @AllowedForUser
    @GetMapping("/{id}/entries")
    public PagedModel<EntityModel<GameUserEntryDto>> findGameUserEntriesByGameId(@PathVariable long id,
                                                                                 GameUserEntrySpecification gameUserEntrySpecification,
                                                                                 @PageableDefault Pageable pageable,
                                                                                 PagedResourcesAssembler<GameUserEntryDto> pagedResourcesAssembler) {
        // The self, next and prev links won't include query parameters if not built manually.
        var link = Link.of(ServletUriComponentsBuilder.fromCurrentRequest().build()
                .toUriString())
                .withSelfRel();

        // Get the paged data from the service and convert into a list so it can be added to a page object.
        List<GameUserEntryDto> gameUserEntryDtos = StreamSupport.stream(gameUserEntryService.findGameUserEntriesByGameId(id, gameUserEntrySpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Get the total number of entities that match the given criteria, dis-regarding page sizing.
        long count = gameUserEntryService.countGameUserEntriesByGameId(id);

        // Wrap the page in a HATEOAS response.
        return pagedResourcesAssembler.toModel(new PageImpl<>(gameUserEntryDtos, pageable, count), gameUserEntryRepresentationModelAssembler, link);
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
        var link = Link.of(ServletUriComponentsBuilder.fromCurrentRequest().build()
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
     * End-point that can be used to retrieve a paged result of {@link GameDetailsDto} instances, that are filtered by
     * the provided {@link GameSpecification} which appear as request parameters on the URL. The page and each
     * {@link GameDetailsDto} will be wrapped in a HATEOAS response. If no {@link GameDetailsDto} match the given criteria,
     * an empty HATEOAS page response will be returned.
     *
     * If any exceptions are thrown internally, and {@link ApiError} response will be returned with additional
     * error details.
     *
     * @param gameSpecification The filter queries to filter the page by.
     * @param pageable The size, page and ordering of the {@link GameDetailsDto} elements in the page.
     * @param pagedResourcesAssembler Injected, used to convert the {@link GameDetailsDto}s into a {@link PagedModel}.
     *
     * @return A {@link PagedModel} containing the {@link GameDetailsDto} that match the requested page and criteria.
     */
    @AllowedForUser
    @GetMapping("/details")
    public PagedModel<EntityModel<GameDetailsDto>> findAllGameDetails(GameSpecification gameSpecification,
                                                                      @PageableDefault Pageable pageable,
                                                                      PagedResourcesAssembler<GameDetailsDto> pagedResourcesAssembler) {
        // The self, next and prev links won't include query parameters if not built manually.
        var link = Link.of(ServletUriComponentsBuilder.fromCurrentRequest().build()
                .toUriString())
                .withSelfRel();

        // Get the paged data from the service and convert into a list so it can be added to a page object.
        List<GameDetailsDto> gameDetailsDtos = StreamSupport.stream(gameDetailsService.findAll(gameSpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Get the total number of entities that match the given criteria, dis-regarding page sizing.
        long count = gameDetailsService.count(gameSpecification);

        // Wrap the page in a HATEOAS response.
        return pagedResourcesAssembler.toModel(new PageImpl<>(gameDetailsDtos, pageable, count), gameDetailsRepresentationModelAssembler, link);
    }

    /**
     * End-point that will attempt to updated the given {@link UpdateGameRequest} request body to the underlying
     * persistence layer. The {@link UpdateGameRequest} must either be valid or have all of the required fields meet
     * its pre-requisite conditions in order to attempt an update in the persistence layer.
     *
     * If the {@link GameDto} being saved doesn't contain an ID that matches an existing entity in the persistence layer,
     * the {@link GameDto} will not be updated and a {@link ApiError} will be returned with appropriate exceptions details.
     *
     * @param updateGameRequest The {@link UpdateGameRequest} to update.
     *
     * @return The updated {@link GameDto} instance as a HATEOAS response.
     */
    @AllowedForModeratorWithGameWriteAuthority
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<GameDto> update(@Validated @RequestBody UpdateGameRequest updateGameRequest) {
        return gameRepresentationModelAssembler.toModel(gameService.update(updateGameRequest));
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
    @AllowedForModeratorWithGameWriteAuthority
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
    @AllowedForModeratorWithGameDeleteAuthority
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        gameService.deleteById(id);
    }
}
