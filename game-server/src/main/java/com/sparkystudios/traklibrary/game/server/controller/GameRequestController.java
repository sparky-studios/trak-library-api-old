package com.sparkystudios.traklibrary.game.server.controller;

import com.sparkystudios.traklibrary.game.repository.specification.GameRequestSpecification;
import com.sparkystudios.traklibrary.game.server.assembler.GameRequestRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.service.GameRequestService;
import com.sparkystudios.traklibrary.game.service.dto.GameRequestDto;
import com.sparkystudios.traklibrary.security.annotation.AllowedForAdmin;
import com.sparkystudios.traklibrary.security.annotation.AllowedForUser;
import com.sparkystudios.traklibrary.security.exception.ApiError;
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

/**
 * The {@link GameRequestController} is a simple controller class that exposes a CRUD based API that is used to interact with
 * any entities or objects that pertain to {@link GameRequestDto}s. It provides API end-points for creating, updating, finding and
 * deleting {@link GameRequestDto} objects. It should be noted that the controller itself contains very little logic, the logic is
 * contained within the {@link GameRequestService}. The controllers primary purpose is to wrap the responses it received from
 * the {@link GameRequestService} into HATEOAS responses. All mappings on this controller therefore produce a {@link MediaTypes#HAL_JSON}
 * response.
 *
 * @since 0.1.0
 * @author Sparky Studios
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/requests", produces = "application/vnd.traklibrary.v1.hal+json")
public class GameRequestController {

    private final GameRequestService gameRequestService;
    private final GameRequestRepresentationModelAssembler gameRequestRepresentationModelAssembler;

    /**
     * End-point that will attempt to save the given {@link GameRequestDto} request body to the underlying
     * persistence layer. The {@link GameRequestDto} must either be valid or have all of the required fields meet
     * its pre-requisite conditions in order to attempt a save to the persistence layer.
     *
     * If the {@link GameRequestDto} being saved contains an ID that matches an existing entity in the persistence layer,
     * the {@link GameRequestDto} will not be saved and a {@link ApiError} will
     * be returned with appropriate exceptions details.
     *
     * @param gameRequestDto The {@link GameRequestDto} to save.
     *
     * @return The saved {@link GameRequestDto} instance as a HATEOAS response.
     */
    @AllowedForUser
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<GameRequestDto> save(@Validated @RequestBody GameRequestDto gameRequestDto) {
        return gameRequestRepresentationModelAssembler.toModel(gameRequestService.save(gameRequestDto));
    }

    /**
     * End-point that will retrieve a {@link GameRequestDto} instance that matches the given ID and convert
     * it into a consumable HATEOAS response. If a {@link GameRequestDto} instance is found that matches the ID, then
     * that data is returned with a status of 200, however if the {@link GameRequestDto} cannot be found the method
     * will return a 404 and wrap the exception details in a {@link ApiError} with additional information.
     *
     * @param id The ID of the {@link GameRequestDto} to retrieve.
     *
     * @return The {@link GameRequestDto} that matches the given ID as a HATEOAS response.
     */
    @AllowedForUser
    @GetMapping("/{id}")
    public EntityModel<GameRequestDto> findById(@PathVariable long id) {
        return gameRequestRepresentationModelAssembler.toModel(gameRequestService.findById(id));
    }

    /**
     * End-point that can be used to retrieve a paged result of {@link GameRequestDto} instances. The page and each
     * {@link GameRequestDto} will be wrapped in a HATEOAS response. If no {@link GameRequestDto} match the given criteria,
     * an empty HATEOAS page response will be returned.
     *
     * If any exceptions are thrown internally, and {@link ApiError} response will be returned with additional
     * error details.
     *
     * @param pageable The size, page and ordering of the {@link GameRequestDto} elements in the page.
     * @param pagedResourcesAssembler Injected, used to convert the {@link GameRequestDto}s into a {@link PagedModel}.
     *
     * @return A {@link PagedModel} containing the {@link GameRequestDto} that match the requested page and criteria.
     */
    @AllowedForUser
    @GetMapping
    public PagedModel<EntityModel<GameRequestDto>> findAll(GameRequestSpecification gameRequestSpecification,
                                                           @PageableDefault Pageable pageable,
                                                           PagedResourcesAssembler<GameRequestDto> pagedResourcesAssembler) {
        // The self, next and prev links won't include query parameters if not built manually.
        var link = Link.of(ServletUriComponentsBuilder.fromCurrentRequest().build()
                .toUriString())
                .withSelfRel();

        // Get the paged data from the service and convert into a list so it can be added to a page object.
        List<GameRequestDto> gameRequestDtos = StreamSupport.stream(gameRequestService.findAll(gameRequestSpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Get the total number of entities that match the given criteria, dis-regarding page sizing.
        long count = gameRequestService.count(gameRequestSpecification);

        // Wrap the page in a HATEOAS response.
        return pagedResourcesAssembler
                .toModel(new PageImpl<>(gameRequestDtos, pageable, count), gameRequestRepresentationModelAssembler, link);
    }

    /**
     * End-point that will attempt to updated the given {@link GameRequestDto} request body to the underlying
     * persistence layer. The {@link GameRequestDto} must either be valid or have all of the required fields meet
     * its pre-requisite conditions in order to attempt an update in the persistence layer.
     *
     * If the {@link GameRequestDto} being saved doesn't contain an ID that matches an existing entity in the persistence layer,
     * the {@link GameRequestDto} will not be updated and a {@link ApiError} will be returned with appropriate exceptions details.
     *
     * @param gameRequestDto The {@link GameRequestDto} to updated.
     *
     * @return The updated {@link GameRequestDto} instance as a HATEOAS response.
     */
    @AllowedForUser
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<GameRequestDto> update(@Validated @RequestBody GameRequestDto gameRequestDto) {
        return gameRequestRepresentationModelAssembler.toModel(gameRequestService.update(gameRequestDto));
    }

    /**
     * End-point that is used to complete the {@link GameRequestDto} that is associated with the given ID. When a {@link GameRequestDto}
     * is completed, the completed value is set to true and the completion date is set to when this end-point was invoked.
     * If the {@link GameRequestDto} is successfully completed, a push notification is sent to the user that made the
     * initial request. If the {@link GameRequestDto} that is associated with the given ID is already completed, it is
     * not completed again and no push notifications are sent.
     *
     * It should be noted that the ID should match an existing {@link GameRequestDto} instance, if not an exception will be thrown
     * and an {@link ApiError} will be returned.
     *
     * As requests send push notifications, it is only available to users who have elevated admin privileges. If any issues
     * occur during completion, an {@link ApiError} will be returned with additional exception details.
     *
     * @param id The ID of the {@link GameRequestDto} to complete.
     */
    @AllowedForAdmin
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(value = "/{id}/complete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void complete(@PathVariable long id) {
        gameRequestService.complete(gameRequestService.findById(id));
    }

    /**
     * End-point that will attempt to patch the {@link GameRequestDto} that matches the given ID with the values
     * specified within the {@link JsonMergePatch}. The {@link JsonMergePatch} must contain valid data to be applied
     * to the {@link GameRequestDto}, otherwise an {@link ApiError} will be returned to the user with additional exception
     * data.
     *
     * The {@link JsonMergePatch} provided can contain JSON data that is not contained within the {@link GameRequestDto},
     * however it will not apply any unknown field, but instead ignore them. If the ID provided does not match
     * a {@link GameRequestDto} or the patch fails to apply, {@link ApiError} instances will be returned with additional
     * error information.
     *
     * @param id The ID of the {@link GameRequestDto} to patch.
     * @param jsonMergePatch The {@link JsonMergePatch} which contains JSON data to update the {@link GameRequestDto} with.
     *
     * @return The patched {@link GameRequestDto} instance.
     */
    @AllowedForUser
    @PatchMapping(value = "/{id}", consumes = "application/merge-patch+json")
    public EntityModel<GameRequestDto> patch(@PathVariable long id, @RequestBody JsonMergePatch jsonMergePatch) {
        return gameRequestRepresentationModelAssembler.toModel(gameRequestService.patch(id, jsonMergePatch));
    }

    /**
     * End-point that will attempt to the delete the {@link GameRequestDto} instance associated with the given ID. If no {@link GameRequestDto}
     * is found that matches the ID, then an exception will be thrown and the end-point will return a {@link ApiError} along with
     * additional information.
     *
     * If the {@link GameRequestDto} is successfully deleted, no data will be returned but the endpoint will specify a response code of 204
     * (NO_CONTENT).
     *
     * @param id The ID of the {@link GameRequestDto} to delete.
     */
    @AllowedForUser
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        gameRequestService.deleteById(id);
    }
}
