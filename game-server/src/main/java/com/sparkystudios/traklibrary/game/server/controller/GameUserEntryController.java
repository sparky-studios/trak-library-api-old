package com.sparkystudios.traklibrary.game.server.controller;

import com.sparkystudios.traklibrary.game.repository.specification.GameUserEntrySpecification;
import com.sparkystudios.traklibrary.game.server.assembler.GameUserEntryRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.service.GameUserEntryService;
import com.sparkystudios.traklibrary.game.service.dto.GameUserEntryDto;
import com.sparkystudios.traklibrary.game.service.dto.request.GameUserEntryRequest;
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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/entries", produces = "application/vnd.traklibrary.v1.hal+json")
public class GameUserEntryController {

    private final GameUserEntryService gameUserEntryService;
    private final GameUserEntryRepresentationModelAssembler gameUserEntryRepresentationModelAssembler;

    /**
     * End-point that will attempt to save the given {@link GameUserEntryRequest} request body to the underlying
     * persistence layer. The {@link GameUserEntryRequest} must either be valid or have all of the required fields meet
     * its pre-requisite conditions in order to attempt a save to the persistence layer.
     *
     * If the {@link GameUserEntryRequest} being saved contains an ID that matches an existing entity in the persistence layer,
     * the {@link GameUserEntryRequest} will not be saved and a {@link ApiError} will be returned with appropriate exceptions
     * details.
     *
     * @param gameUserEntryRequest The {@link GameUserEntryRequest} to save.
     *
     * @return The saved {@link GameUserEntryDto} instance as a HATEOAS response.
     */
    @AllowedForUser
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<GameUserEntryDto> save(@Validated @RequestBody GameUserEntryRequest gameUserEntryRequest) {
        return gameUserEntryRepresentationModelAssembler.toModel(gameUserEntryService.save(gameUserEntryRequest));
    }

    /**
     * End-point that will retrieve a {@link GameUserEntryDto} instance that matches the given Id and convert
     * it into a consumable HATEOAS response. If a {@link GameUserEntryDto} instance is found that matches the Id, then
     * that data is returned with a status of 200, however if the {@link GameUserEntryDto} cannot be found the method
     * will return a 404 and wrap the exception details in a {@link ApiError} with additional information.
     *
     * @param id The ID of the {@link GameUserEntryDto} to retrieve.
     *
     * @return The {@link GameUserEntryDto} that matches the given ID as a HATEOAS response.
     */
    @AllowedForUser
    @GetMapping("/{id}")
    public EntityModel<GameUserEntryDto> findById(@PathVariable long id) {
        return gameUserEntryRepresentationModelAssembler.toModel(gameUserEntryService.findById(id));
    }

    /**
     * End-point that can be used to retrieve a paged result of {@link GameUserEntryDto} instances, that are filtered by
     * the provided {@link GameUserEntrySpecification} which appear as request parameters on the URL. The page and each
     * {@link GameUserEntryDto} will be wrapped in a HATEOAS response. If no {@link GameUserEntryDto} match the given criteria,
     * an empty HATEOAS page response will be returned.
     *
     * If any exceptions are thrown internally, and {@link ApiError} response will be returned with additional
     * error details.
     *
     * @param gameUserEntrySpecification The filter queries to filter the page by.
     * @param pageable The size, page and ordering of the {@link GameUserEntryDto} elements in the page.
     * @param pagedResourcesAssembler Injected, used to convert the {@link GameUserEntryDto}s into a {@link PagedModel}.
     *
     * @return A {@link PagedModel} containing the {@link GameUserEntryDto} that match the requested page and criteria.
     */
    @AllowedForUser
    @GetMapping
    public PagedModel<EntityModel<GameUserEntryDto>> findAll(GameUserEntrySpecification gameUserEntrySpecification,
                                                             @PageableDefault Pageable pageable,
                                                             PagedResourcesAssembler<GameUserEntryDto> pagedResourcesAssembler) {

        // The self, next and prev links won't include query parameters if not built manually.
        var link = Link.of(ServletUriComponentsBuilder.fromCurrentRequest().build()
                .toUriString())
                .withSelfRel();

        // Get the paged data from the service and convert into a list so it can be added to a page object.
        List<GameUserEntryDto> gameUserEntryDtos = StreamSupport.stream(gameUserEntryService.findAll(gameUserEntrySpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Get the total number of entities that match the given criteria, dis-regarding page sizing.
        long count = gameUserEntryService.count(gameUserEntrySpecification);

        // Wrap the page in a HATEOAS response.
        return pagedResourcesAssembler.toModel(new PageImpl<>(gameUserEntryDtos, pageable, count), gameUserEntryRepresentationModelAssembler, link);
    }

    /**
     * End-point that will attempt to updated the given {@link GameUserEntryRequest} request body to the underlying
     * persistence layer. The {@link GameUserEntryRequest} must either be valid or have all of the required fields meet
     * its pre-requisite conditions in order to attempt an update in the persistence layer.
     *
     * If the {@link GameUserEntryRequest} being saved doesn't contain an ID that matches an existing entity in the persistence layer,
     * the {@link GameUserEntryRequest} will not be updated and a {@link ApiError} will be returned with appropriate exceptions details.
     *
     * @param gameUserEntryRequest The {@link GameUserEntryRequest} to updated.
     *
     * @return The updated {@link GameUserEntryDto} instance as a HATEOAS response.
     */
    @AllowedForUser
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<GameUserEntryDto> update(@Validated @RequestBody GameUserEntryRequest gameUserEntryRequest) {
        return gameUserEntryRepresentationModelAssembler.toModel(gameUserEntryService.update(gameUserEntryRequest));
    }

    /**
     * End-point that will attempt to the delete the {@link GameUserEntryDto} instance associated with the given ID. If no {@link GameUserEntryDto}
     * is found that matches the ID, then an exception will be thrown and the end-point will return a {@link ApiError} along with
     * additional information.
     *
     * If the {@link GameUserEntryDto} is successfully deleted, no data will be returned but the endpoint will specify a response code of 204
     * (NO_CONTENT).
     *
     * @param id The ID of the {@link GameUserEntryDto} to delete.
     */
    @AllowedForUser
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        gameUserEntryService.deleteById(id);
    }
} 