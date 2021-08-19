package com.sparkystudios.traklibrary.game.server.controller;

import com.sparkystudios.traklibrary.game.domain.PlatformImage;
import com.sparkystudios.traklibrary.game.repository.specification.DeveloperSpecification;
import com.sparkystudios.traklibrary.game.repository.specification.PublisherSpecification;
import com.sparkystudios.traklibrary.game.server.assembler.GameRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.server.assembler.PublisherRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.service.CompanyImageService;
import com.sparkystudios.traklibrary.game.service.GameService;
import com.sparkystudios.traklibrary.game.service.PublisherService;
import com.sparkystudios.traklibrary.game.service.dto.GameDto;
import com.sparkystudios.traklibrary.game.service.dto.PublisherDto;
import com.sparkystudios.traklibrary.security.annotation.AllowedForModeratorWithPublisherDeleteAuthority;
import com.sparkystudios.traklibrary.security.annotation.AllowedForModeratorWithPublisherWriteAuthority;
import com.sparkystudios.traklibrary.security.annotation.AllowedForUser;
import com.sparkystudios.traklibrary.security.exception.ApiError;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.json.JsonMergePatch;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * The {@link PublisherController} is a simple controller class that exposes a CRUD based API that is used to interact with
 * any entities or objects that pertain to companies. It provides API end-points for creating, updating, finding and deleting
 * company entities. It should be noted that the controller itself contains very little logic, the logic is contained within the
 * {@link PublisherService}. The controllers primary purpose is to wrap the responses it received from the {@link PublisherService}
 * into HATEOAS responses. All mappings on this controller therefore produce a {@link MediaTypes#HAL_JSON} response.
 *
 * @since 0.1.0
 * @author Sparky Studios
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/publishers", produces = "application/vnd.sparkystudios.traklibrary-hal+json;version=1.0")
public class PublisherController {

    private final PublisherService publisherService;
    private final CompanyImageService companyImageService;
    private final GameService gameService;
    private final PublisherRepresentationModelAssembler publisherRepresentationModelAssembler;
    private final GameRepresentationModelAssembler gameRepresentationModelAssembler;

    /**
     * End-point that will attempt to save the given {@link PublisherDto} request body to the underlying
     * persistence layer. The {@link PublisherDto} must either be valid or have all of the required fields meet
     * its pre-requisite conditions in order to attempt a save to the persistence layer.
     *
     * If the {@link PublisherDto} being saved contains an ID that matches an existing entity in the persistence layer,
     * the {@link PublisherDto} will not be saved and a {@link ApiError} will
     * be returned with appropriate exceptions details.
     *
     * @param publisherDto The {@link PublisherDto} to save.
     *
     * @return The saved {@link PublisherDto} instance as a HATEOAS response.
     */
    @AllowedForModeratorWithPublisherWriteAuthority
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<PublisherDto> save(@Validated @RequestBody PublisherDto publisherDto) {
        return publisherRepresentationModelAssembler.toModel(publisherService.save(publisherDto));
    }

    /**
     * End-point that will retrieve a {@link PublisherDto} instance that matches the given Id and convert
     * it into a consumable HATEOAS response. If a {@link PublisherDto} instance is found that matches the Id, then
     * that data is returned with a status of 200, however if the {@link PublisherDto} cannot be found the method
     * will return a 404 and wrap the exception details in a {@link ApiError} with additional information.
     *
     * @param id The ID of the {@link PublisherDto} to retrieve.
     *
     * @return The {@link PublisherDto} that matches the given ID as a HATEOAS response.
     */
    @AllowedForUser
    @GetMapping("/{id}")
    public EntityModel<PublisherDto> findById(@PathVariable long id) {
        return publisherRepresentationModelAssembler.toModel(publisherService.findById(id));
    }

    /**
     * End-point that will retrieve a {@link PublisherDto} instance that matches the given slug and convert
     * it into a consumable HATEOAS response. If a {@link PublisherDto} instance is found that matches the slug, then
     * that data is returned with a status of 200, however if the {@link PublisherDto} cannot be found the method
     * will return a 404 and wrap the exception details in a {@link ApiError} with additional information.
     *
     * @param slug The slug of the {@link PublisherDto} to retrieve.
     *
     * @return The {@link PublisherDto} that matches the given slug as a HATEOAS response.
     */
    @AllowedForUser
    @GetMapping("/slug/{slug}")
    public EntityModel<PublisherDto> findById(@PathVariable String slug) {
        return publisherRepresentationModelAssembler.toModel(publisherService.findBySlug(slug));
    }

    /**
     * End-point that will retrieve a {@link ByteArrayResource} for the image that is associated with the given
     * {@link PublisherDto} ID. If no image is associated with the {@link PublisherDto} or it fails to retrieve the data,
     * an empty {@link ByteArrayResource} will be returned and the error will be logged.
     *
     * This end-point can be called anonymously by anyone without providing any authentication or credentials.
     *
     * @param id The ID of the {@link PublisherDto} to retrieve the associated image for.
     *
     * @return A {@link ByteArrayResource} representing the byte information of the image file.
     */
    @GetMapping(value = "/{id}/image", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<ByteArrayResource> findCompanyImageByCompanyId(@PathVariable long id) {
        // Get the image data, all images are stored as *.png so it's safe to assume the file extension.
        var imageDataDto = companyImageService.download(id);
        return ResponseEntity
                .ok()
                .contentLength(imageDataDto.getContent().length)
                .header("Content-Disposition", "attachment; filename=\"" + imageDataDto.getFilename()+ "\"")
                .body(new ByteArrayResource(imageDataDto.getContent()));
    }

    /**
     * End-point that create a {@link PlatformImage} instance and register the specified file with the chosen image provider.
     * If the file is in a incorrect format or a image already exists for the given {@link PublisherDto}, an exception
     * will be thrown and an {@link ApiError} will be returned to the callee.
     *
     * {@link PublisherDto}'s can only be created for users with moderator privileges.
     *
     * @param id The ID of the {@link PublisherDto} to persist and image for.
     * @param file The {@link MultipartFile} containing the image to upload.
     */
    @AllowedForModeratorWithPublisherWriteAuthority
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void saveCompanyImageForCompanyId(@PathVariable long id, @RequestPart MultipartFile file) {
        companyImageService.upload(id, file);
    }

    /**
     * End-point that will retrieve a {@link PagedModel} of {@link GameDto}s that have a link to the specified
     * {@link PublisherDto}. If the ID doesn't match an existing {@link PublisherDto}, then an {@link ApiError} will be
     * returned with additional error details. If the {@link PublisherDto} exists but has no associated
     * {@link GameDto}'s, then an empty {@link PagedModel} will be returned.
     *
     * @param id The ID of the {@link PublisherDto} to retrieve associated {@link GameDto}'s for.
     * @param pageable The size and ordering of the page to retrieve.
     * @param pagedResourcesAssembler The assembler used to convert the {@link GameDto}'s to a HATEOAS page.
     *
     * @return A {@link PagedModel} of {@link GameDto}'s that are associated with the given {@link PublisherDto}.
     */
    @AllowedForUser
    @GetMapping("/{id}/games")
    public PagedModel<EntityModel<GameDto>> findGamesByPublisherId(@PathVariable long id,
                                                                   @PageableDefault Pageable pageable,
                                                                   PagedResourcesAssembler<GameDto> pagedResourcesAssembler) {
        // The self, next and prev links won't include query parameters if not built manually.
        var link = Link.of(ServletUriComponentsBuilder.fromCurrentRequest().build()
                .toUriString())
                .withSelfRel();

        // Get the paged data from the service and convert into a list so it can be added to a page object.
        List<GameDto> gameDtos = StreamSupport.stream(gameService.findGamesByPublisherId(id, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Get the total number of entities that match the given criteria, dis-regarding page sizing.
        long count = gameService.countGamesByPublisherId(id);

        // Wrap the page in a HATEOAS response.
        return pagedResourcesAssembler
                .toModel(new PageImpl<>(gameDtos, pageable, count), gameRepresentationModelAssembler, link);
    }

    /**
     * End-point that can be used to retrieve a paged result of {@link PublisherDto} instances, that are filtered by
     * the provided {@link DeveloperSpecification} which appear as request parameters on the URL. The page and each
     * {@link PublisherDto} will be wrapped in a HATEOAS response. If no {@link PublisherDto} match the given criteria,
     * an empty HATEOAS page response will be returned.
     *
     * If any exceptions are thrown internally, and {@link ApiError} response will be returned with additional
     * error details.
     *
     * @param publisherSpecification The filter queries to filter the page by.
     * @param pageable The size, page and ordering of the {@link PublisherDto} elements in the page.
     * @param pagedResourcesAssembler Injected, used to convert the {@link PublisherDto}s into a {@link PagedModel}.
     *
     * @return A {@link PagedModel} containing the {@link PublisherDto} that match the requested page and criteria.
     */
    @AllowedForUser
    @GetMapping
    public PagedModel<EntityModel<PublisherDto>> findAll(PublisherSpecification publisherSpecification,
                                                         @PageableDefault Pageable pageable,
                                                         PagedResourcesAssembler<PublisherDto> pagedResourcesAssembler) {
        // The self, next and prev links won't include query parameters if not built manually.
        var link = Link.of(ServletUriComponentsBuilder.fromCurrentRequest().build()
                .toUriString())
                .withSelfRel();

        // Get the paged data from the service and convert into a list so it can be added to a page object.
        List<PublisherDto> publisherDtos = StreamSupport.stream(publisherService.findAll(publisherSpecification, pageable).spliterator(), false)
                .collect(Collectors.toList());

        // Get the total number of entities that match the given criteria, dis-regarding page sizing.
        long count = publisherService.count(publisherSpecification);

        // Wrap the page in a HATEOAS response.
        return pagedResourcesAssembler
                .toModel(new PageImpl<>(publisherDtos, pageable, count), publisherRepresentationModelAssembler, link);
    }

    /**
     * End-point that will attempt to updated the given {@link PublisherDto} request body to the underlying
     * persistence layer. The {@link PublisherDto} must either be valid or have all of the required fields meet
     * its pre-requisite conditions in order to attempt an update in the persistence layer.
     *
     * If the {@link PublisherDto} being saved doesn't contain an ID that matches an existing entity in the persistence layer,
     * the {@link PublisherDto} will not be updated and a {@link ApiError} will
     * be returned with appropriate exceptions details.
     *
     * @param publisherDto The {@link PublisherDto} to updated.
     *
     * @return The updated {@link PublisherDto} instance as a HATEOAS response.
     */
    @AllowedForModeratorWithPublisherWriteAuthority
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<PublisherDto> update(@Validated @RequestBody PublisherDto publisherDto) {
        return publisherRepresentationModelAssembler.toModel(publisherService.update(publisherDto));
    }

    /**
     * End-point that will attempt to patch the {@link PublisherDto} that matches the given ID with the values
     * specified within the {@link JsonMergePatch}. The {@link JsonMergePatch} must contain valid data to be applied
     * to the {@link PublisherDto}, otherwise an {@link ApiError} will be returned to the user with additional exception
     * data.
     *
     * The {@link JsonMergePatch} provided can contain JSON data that is not contained within the {@link PublisherDto},
     * however it will not apply any unknown field, but instead ignore them. If the ID provided does not match
     * a {@link PublisherDto} or the patch fails to apply, {@link ApiError} instances will be returned with additional
     * error information.
     *
     * @param id The ID of the {@link PublisherDto} to patch.
     * @param jsonMergePatch The {@link JsonMergePatch} which contains JSON data to update the {@link PublisherDto} with.
     *
     * @return The patched {@link PublisherDto} instance.
     */
    @AllowedForModeratorWithPublisherWriteAuthority
    @PatchMapping(value = "/{id}", consumes = "application/merge-patch+json")
    public EntityModel<PublisherDto> patch(@PathVariable long id, @RequestBody JsonMergePatch jsonMergePatch) {
        return publisherRepresentationModelAssembler.toModel(publisherService.patch(id, jsonMergePatch));
    }

    /**
     * End-point that will attempt to the delete the {@link PublisherDto} instance associated with the given ID. If no {@link PublisherDto}
     * is found that matches the ID, then an exception will be thrown and the end-point will return a {@link ApiError} along with
     * additional information.
     *
     * If the {@link PublisherDto} is successfully deleted, no data will be returned but the endpoint will specify a response code of 204
     * (NO_CONTENT).
     *
     * @param id The ID of the {@link PublisherDto} to delete.
     */
    @AllowedForModeratorWithPublisherDeleteAuthority
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        publisherService.deleteById(id);
    }
}

