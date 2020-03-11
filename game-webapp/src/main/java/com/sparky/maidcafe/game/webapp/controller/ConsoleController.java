package com.sparky.maidcafe.game.webapp.controller;

import com.sparky.maidcafe.game.repository.specification.ConsoleSpecification;
import com.sparky.maidcafe.game.service.ConsoleService;
import com.sparky.maidcafe.game.service.GameService;
import com.sparky.maidcafe.game.service.dto.ConsoleDto;
import com.sparky.maidcafe.game.service.dto.GameDto;
import com.sparky.maidcafe.game.service.dto.GenreDto;
import com.sparky.maidcafe.game.webapp.assembler.ConsoleRepresentationModelAssembler;
import com.sparky.maidcafe.game.webapp.assembler.GameRepresentationModelAssembler;
import com.sparky.maidcafe.game.webapp.exception.ApiError;
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

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/game-management/consoles", produces = MediaTypes.HAL_JSON_VALUE)
public class ConsoleController {

    private final ConsoleService consoleService;
    private final GameService gameService;
    private final ConsoleRepresentationModelAssembler consoleRepresentationModelAssembler;
    private final GameRepresentationModelAssembler gameRepresentationModelAssembler;

    /**
     * End-point that will attempt to save the given {@link ConsoleDto} request body to the underlying
     * persistence layer. The {@link ConsoleDto} must either be valid or have all of the required fields meet
     * its pre-requisite conditions in order to attempt a save to the persistence layer.
     *
     * If the {@link ConsoleDto} being saved contains an ID that matches an existing entity in the persistence layer,
     * the {@link ConsoleDto} will not be saved and a {@link com.sparky.maidcafe.game.webapp.exception.ApiError} will
     * be returned with appropriate exceptions details.
     *
     * @param consoleDto The {@link ConsoleDto} to save.
     *
     * @return The saved {@link ConsoleDto} instance as a HATEOAS response.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public EntityModel<ConsoleDto> save(@Validated @RequestBody ConsoleDto consoleDto) {
        return consoleRepresentationModelAssembler.toModel(consoleService.save(consoleDto));
    }

    /**
     * End-point that will retrieve a {@link ConsoleDto} instance that matches the given ID and convert
     * it into a consumable HATEOAS response. If a {@link ConsoleDto} instance is found that matches the ID, then
     * that data is returned with a status of 200, however if the {@link ConsoleDto} cannot be found the method
     * will return a 404 and wrap the exception details in a {@link ApiError} with additional information.
     *
     * @param id The ID of the {@link ConsoleDto} to retrieve.
     *
     * @return The {@link ConsoleDto} that matches the given ID as a HATEOAS response.
     */
    @GetMapping("/{id}")
    public EntityModel<ConsoleDto> findById(@PathVariable long id) {
        return consoleRepresentationModelAssembler.toModel(consoleService.findById(id));
    }

    /**
     * End-point that will retrieve a {@link PagedModel} of {@link GameDto}s that have a link to the specified
     * {@link ConsoleDto}. If the ID doesn't match an existing {@link ConsoleDto}, then an {@link ApiError} will be
     * returned with additional error details. If the {@link ConsoleDto} exists but has no associated
     * {@link GameDto}'s, then an empty {@link PagedModel} will be returned.
     *
     * @param id The ID of the {@link ConsoleDto} to retrieve associated {@link GameDto}'s for.
     * @param pageable The size and ordering of the page to retrieve.
     * @param pagedResourcesAssembler The assembler used to convert the {@link GameDto}'s to a HATEOAS page.
     *
     * @return A {@link PagedModel} of {@link GameDto}'s that are associated with the given {@link ConsoleDto}.
     */
    @GetMapping("/{id}/games")
    public PagedModel<EntityModel<GameDto>> findGamesByConsoleId(@PathVariable long id,
                                                                 @PageableDefault Pageable pageable,
                                                                 PagedResourcesAssembler<GameDto> pagedResourcesAssembler) {
        // Get the paged data from the service and convert into a list so it can be added to a page object.
        List<GameDto> gameDtos = StreamSupport.stream(gameService.findGamesByConsoleId(id, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Wrap the page in a HATEOAS response.
        return pagedResourcesAssembler.toModel(new PageImpl<>(gameDtos, pageable, gameDtos.size()), gameRepresentationModelAssembler);
    }

    /**
     * End-point that can be used to retrieve a paged result of {@link ConsoleDto} instances, that are filtered by
     * the provided {@link ConsoleSpecification} which appear as request parameters on the URL. The page and each
     * {@link ConsoleDto} will be wrapped in a HATEOAS response. If no {@link ConsoleDto} match the given criteria,
     * an empty HATEOAS page response will be returned.
     *
     * If any exceptions are thrown internally, and {@link ApiError} response will be returned with additional
     * error details.
     *
     * @param consoleSpecification The filter queries to filter the page by.
     * @param pageable The size, page and ordering of the {@link ConsoleDto} elements in the page.
     * @param pagedResourcesAssembler Injected, used to convert the {@link ConsoleDto}s into a {@link PagedModel}.
     *
     * @return A {@link PagedModel} containing the {@link ConsoleDto} that match the requested page and criteria.
     */
    @GetMapping
    public PagedModel<EntityModel<ConsoleDto>> findAll(ConsoleSpecification consoleSpecification,
                                                       @PageableDefault Pageable pageable,
                                                       PagedResourcesAssembler<ConsoleDto> pagedResourcesAssembler) {
        // Get the paged data from the service and convert into a list so it can be added to a page object.
        List<ConsoleDto> consoleDtos = StreamSupport.stream(consoleService.findAll(consoleSpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Wrap the page in a HATEOAS response.
        return pagedResourcesAssembler.toModel(new PageImpl<>(consoleDtos, pageable, consoleDtos.size()), consoleRepresentationModelAssembler);
    }

    /**
     * End-point that will attempt to updated the given {@link ConsoleDto} request body to the underlying
     * persistence layer. The {@link ConsoleDto} must either be valid or have all of the required fields meet
     * its pre-requisite conditions in order to attempt an update in the persistence layer.
     *
     * If the {@link ConsoleDto} being saved doesn't contain an ID that matches an existing entity in the persistence layer,
     * the {@link ConsoleDto} will not be updated and a {@link ApiError} will be returned with appropriate exceptions details.
     *
     * @param consoleDto The {@link ConsoleDto} to updated.
     *
     * @return The updated {@link ConsoleDto} instance as a HATEOAS response.
     */
    @PutMapping
    public EntityModel<ConsoleDto> update(@Validated @RequestBody ConsoleDto consoleDto) {
        return consoleRepresentationModelAssembler.toModel(consoleService.update(consoleDto));
    }

    /**
     * End-point that will attempt to patch the {@link ConsoleDto} that matches the given ID with the values
     * specified within the {@link JsonMergePatch}. The {@link JsonMergePatch} must contain valid data to be applied
     * to the {@link ConsoleDto}, otherwise an {@link ApiError} will be returned to the user with additional exception
     * data.
     *
     * The {@link JsonMergePatch} provided can contain JSON data that is not contained within the {@link ConsoleDto},
     * however it will not apply any unknown field, but instead ignore them. If the ID provided does not match
     * a {@link ConsoleDto} or the patch fails to apply, {@link ApiError} instances will be returned with additional
     * error information.
     *
     * @param id The ID of the {@link ConsoleDto} to patch.
     * @param jsonMergePatch The {@link JsonMergePatch} which contains JSON data to update the {@link ConsoleDto} with.
     *
     * @return The patched {@link ConsoleDto} instance.
     */
    @PatchMapping(value = "/{id}", consumes = "application/merge-patch+json")
    public EntityModel<ConsoleDto> patch(@PathVariable long id, @RequestBody JsonMergePatch jsonMergePatch) {
        return consoleRepresentationModelAssembler.toModel(consoleService.patch(id, jsonMergePatch));
    }

    /**
     * End-point that will attempt to the delete the {@link ConsoleDto} instance associated with the given ID. If no {@link ConsoleDto}
     * is found that matches the ID, then an exception will be thrown and the end-point will return a {@link ApiError} along with
     * additional information.
     *
     * If the {@link ConsoleDto} is successfully deleted, no data will be returned but the endpoint will specify a response code of 204
     * (NO_CONTENT).
     *
     * @param id The ID of the {@link ConsoleDto} to delete.
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        consoleService.deleteById(id);
    }
}
