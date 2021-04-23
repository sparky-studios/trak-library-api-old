package com.sparkystudios.traklibrary.game.server.controller;

import com.sparkystudios.traklibrary.game.repository.specification.GenreSpecification;
import com.sparkystudios.traklibrary.game.server.assembler.GameDetailsRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.server.assembler.GameRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.server.assembler.GenreRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.service.GameDetailsService;
import com.sparkystudios.traklibrary.game.service.GameService;
import com.sparkystudios.traklibrary.game.service.GenreService;
import com.sparkystudios.traklibrary.game.service.dto.GameDetailsDto;
import com.sparkystudios.traklibrary.game.service.dto.GameDto;
import com.sparkystudios.traklibrary.game.service.dto.GenreDto;
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
@RequestMapping(value = "/genres", produces = "application/vnd.traklibrary.v1.hal+json")
public class GenreController {

    private final GenreService genreService;
    private final GameService gameService;
    private final GameDetailsService gameDetailsService;
    private final GenreRepresentationModelAssembler genreRepresentationModelAssembler;
    private final GameRepresentationModelAssembler gameRepresentationModelAssembler;
    private final GameDetailsRepresentationModelAssembler gameDetailsRepresentationModelAssembler;

    /**
     * End-point that will attempt to save the given {@link GenreDto} request body to the underlying
     * persistence layer. The {@link GenreDto} must either be valid or have all of the required fields meet
     * its pre-requisite conditions in order to attempt a save to the persistence layer.
     *
     * If the {@link GenreDto} being saved contains an ID that matches an existing entity in the persistence layer,
     * the {@link GenreDto} will not be saved and a {@link ApiError} will
     * be returned with appropriate exceptions details.
     *
     * @param genreDto The {@link GenreDto} to save.
     *
     * @return The saved {@link GenreDto} instance as a HATEOAS response.
     */
    @AllowedForModerator
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<GenreDto> save(@Validated @RequestBody GenreDto genreDto) {
        return genreRepresentationModelAssembler.toModel(genreService.save(genreDto));
    }

    /**
     * End-point that will retrieve a {@link GenreDto} instance that matches the given ID and convert
     * it into a consumable HATEOAS response. If a {@link GenreDto} instance is found that matches the ID, then
     * that data is returned with a status of 200, however if the {@link GenreDto} cannot be found the method
     * will return a 404 and wrap the exception details in a {@link ApiError} with additional information.
     *
     * @param id The ID of the {@link GenreDto} to retrieve.
     *
     * @return The {@link GenreDto} that matches the given ID as a HATEOAS response.
     */
    @AllowedForUser
    @GetMapping("/{id}")
    public EntityModel<GenreDto> findById(@PathVariable long id) {
        return genreRepresentationModelAssembler.toModel(genreService.findById(id));
    }

    /**
     * End-point that will retrieve a {@link GenreDto} instance that matches the given slug and convert
     * it into a consumable HATEOAS response. If a {@link GenreDto} instance is found that matches the slug, then
     * that data is returned with a status of 200, however if the {@link GenreDto} cannot be found the method
     * will return a 404 and wrap the exception details in a {@link ApiError} with additional information.
     *
     * @param slug The slug of the {@link GenreDto} to retrieve.
     *
     * @return The {@link GenreDto} that matches the given ID as a HATEOAS response.
     */
    @AllowedForUser
    @GetMapping("/slug/{slug}")
    public EntityModel<GenreDto> findBySlug(@PathVariable String slug) {
        return genreRepresentationModelAssembler.toModel(genreService.findBySlug(slug));
    }

    /**
     * End-point that will retrieve a {@link PagedModel} of {@link GameDto}s that have a link to the specified
     * {@link GenreDto}. If the ID doesn't match an existing {@link GenreDto}, then an {@link ApiError} will be
     * returned with additional error details. If the {@link GenreDto} exists but has no associated
     * {@link GameDto}'s, then an empty {@link PagedModel} will be returned.
     *
     * @param id The ID of the {@link GenreDto} to retrieve associated {@link GameDto}'s for.
     * @param pageable The size and ordering of the page to retrieve.
     * @param pagedResourcesAssembler The assembler used to convert the {@link GameDto}'s to a HATEOAS page.
     *
     * @return A {@link PagedModel} of {@link GameDto}'s that are associated with the given {@link GenreDto}.
     */
    @AllowedForUser
    @GetMapping("/{id}/games")
    public PagedModel<EntityModel<GameDto>> findGamesByGenreId(@PathVariable long id,
                                                               @PageableDefault Pageable pageable,
                                                               PagedResourcesAssembler<GameDto> pagedResourcesAssembler) {
        // The self, next and prev links won't include query parameters if not built manually.
        var link = Link.of(ServletUriComponentsBuilder.fromCurrentRequest().build()
                .toUriString())
                .withSelfRel();

        // Get the paged data from the service and convert into a list so it can be added to a page object.
        List<GameDto> gameDtos = StreamSupport.stream(gameService.findGamesByGenreId(id, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Get the total number of entities that match the given criteria, dis-regarding page sizing.
        long count = gameService.countGamesByGenreId(id);

        // Wrap the page in a HATEOAS response.
        return pagedResourcesAssembler
                .toModel(new PageImpl<>(gameDtos, pageable, count), gameRepresentationModelAssembler, link);
    }

    /**
     * End-point that will retrieve a {@link PagedModel} of {@link GameDetailsDto}s that have a link to the specified
     * {@link GenreDto}. If the ID doesn't match an existing {@link GenreDto}, then an {@link ApiError} will be
     * returned with additional error details. If the {@link GenreDto} exists but has no associated
     * {@link GameDetailsDto}'s, then an empty {@link PagedModel} will be returned.
     *
     * @param id The ID of the {@link GenreDto} to retrieve associated {@link GameDetailsDto}'s for.
     * @param pageable The size and ordering of the page to retrieve.
     * @param pagedResourcesAssembler The assembler used to convert the {@link GameDetailsDto}'s to a HATEOAS page.
     *
     * @return A {@link PagedModel} of {@link GameDetailsDto}'s that are associated with the given {@link GenreDto}.
     */
    @AllowedForUser
    @GetMapping("/{id}/games/details")
    public PagedModel<EntityModel<GameDetailsDto>> findGameDetailsByGenreId(@PathVariable long id,
                                                                            @PageableDefault Pageable pageable,
                                                                            PagedResourcesAssembler<GameDetailsDto> pagedResourcesAssembler) {
        // The self, next and prev links won't include query parameters if not built manually.
        var link = Link.of(ServletUriComponentsBuilder.fromCurrentRequest().build()
                .toUriString())
                .withSelfRel();

        // Get the paged data from the service and convert into a list so it can be added to a page object.
        List<GameDetailsDto> gameDetailsDtos = StreamSupport.stream(gameDetailsService.findByGenreId(id, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Get the total number of entities that match the given criteria, dis-regarding page sizing.
        long count = gameDetailsService.countByGenreId(id);

        // Wrap the page in a HATEOAS response.
        return pagedResourcesAssembler
                .toModel(new PageImpl<>(gameDetailsDtos, pageable, count), gameDetailsRepresentationModelAssembler, link);
    }

    /**
     * End-point that can be used to retrieve a paged result of {@link GenreDto} instances, that are filtered by
     * the provided {@link GenreSpecification} which appear as request parameters on the URL. The page and each
     * {@link GenreDto} will be wrapped in a HATEOAS response. If no {@link GenreDto} match the given criteria,
     * an empty HATEOAS page response will be returned.
     *
     * If any exceptions are thrown internally, and {@link ApiError} response will be returned with additional
     * error details.
     *
     * @param genreSpecification The filter queries to filter the page by.
     * @param pageable The size, page and ordering of the {@link GenreDto} elements in the page.
     * @param pagedResourcesAssembler Injected, used to convert the {@link GenreDto}s into a {@link PagedModel}.
     *
     * @return A {@link PagedModel} containing the {@link GenreDto} that match the requested page and criteria.
     */
    @AllowedForUser
    @GetMapping
    public PagedModel<EntityModel<GenreDto>> findAll(GenreSpecification genreSpecification,
                                                    @PageableDefault Pageable pageable,
                                                    PagedResourcesAssembler<GenreDto> pagedResourcesAssembler) {
        // The self, next and prev links won't include query parameters if not built manually.
        var link = Link.of(ServletUriComponentsBuilder.fromCurrentRequest().build()
                .toUriString())
                .withSelfRel();

        // Get the paged data from the service and convert into a list so it can be added to a page object.
        List<GenreDto> genreDtos = StreamSupport.stream(genreService.findAll(genreSpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Get the total number of entities that match the given criteria, dis-regarding page sizing.
        long count = genreService.count(genreSpecification);

        // Wrap the page in a HATEOAS response.
        return pagedResourcesAssembler
                .toModel(new PageImpl<>(genreDtos, pageable, count), genreRepresentationModelAssembler, link);
    }

    /**
     * End-point that will attempt to updated the given {@link GenreDto} request body to the underlying
     * persistence layer. The {@link GenreDto} must either be valid or have all of the required fields meet
     * its pre-requisite conditions in order to attempt an update in the persistence layer.
     *
     * If the {@link GenreDto} being saved doesn't contain an ID that matches an existing entity in the persistence layer,
     * the {@link GenreDto} will not be updated and a {@link ApiError} will be returned with appropriate exceptions details.
     *
     * @param genreDto The {@link GenreDto} to updated.
     *
     * @return The updated {@link GenreDto} instance as a HATEOAS response.
     */
    @AllowedForModerator
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<GenreDto> update(@Validated @RequestBody GenreDto genreDto) {
        return genreRepresentationModelAssembler.toModel(genreService.update(genreDto));
    }

    /**
     * End-point that will attempt to patch the {@link GenreDto} that matches the given ID with the values
     * specified within the {@link JsonMergePatch}. The {@link JsonMergePatch} must contain valid data to be applied
     * to the {@link GenreDto}, otherwise an {@link ApiError} will be returned to the user with additional exception
     * data.
     *
     * The {@link JsonMergePatch} provided can contain JSON data that is not contained within the {@link GenreDto},
     * however it will not apply any unknown field, but instead ignore them. If the ID provided does not match
     * a {@link GenreDto} or the patch fails to apply, {@link ApiError} instances will be returned with additional
     * error information.
     *
     * @param id The ID of the {@link GenreDto} to patch.
     * @param jsonMergePatch The {@link JsonMergePatch} which contains JSON data to update the {@link GenreDto} with.
     *
     * @return The patched {@link GenreDto} instance.
     */
    @AllowedForModerator
    @PatchMapping(value = "/{id}", consumes = "application/merge-patch+json")
    public EntityModel<GenreDto> patch(@PathVariable long id, @RequestBody JsonMergePatch jsonMergePatch) {
        return genreRepresentationModelAssembler.toModel(genreService.patch(id, jsonMergePatch));
    }

    /**
     * End-point that will attempt to the delete the {@link GenreDto} instance associated with the given ID. If no {@link GenreDto}
     * is found that matches the ID, then an exception will be thrown and the end-point will return a {@link ApiError} along with
     * additional information.
     *
     * If the {@link GenreDto} is successfully deleted, no data will be returned but the endpoint will specify a response code of 204
     * (NO_CONTENT).
     *
     * @param id The ID of the {@link GenreDto} to delete.
     */
    @AllowedForModerator
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        genreService.deleteById(id);
    }
}
