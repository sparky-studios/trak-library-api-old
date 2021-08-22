package com.sparkystudios.traklibrary.game.server.controller;

import com.sparkystudios.traklibrary.game.server.assembler.DownloadableContentRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.service.DownloadableContentService;
import com.sparkystudios.traklibrary.game.service.dto.DownloadableContentDto;
import com.sparkystudios.traklibrary.security.annotation.AllowedForUser;
import com.sparkystudios.traklibrary.security.exception.ApiError;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The {@link DownloadableContentController} is a simple controller class that exposes a CRUD based API that is used to interact with
 * any entities or objects that pertain to DLC. It provides API end-points for creating, updating, finding and deleting
 * DLC entities. It should be noted that the controller itself contains very little logic, the logic is contained within the
 * {@link DownloadableContentService}. The controllers primary purpose is to wrap the responses it received from the {@link DownloadableContentService}
 * into HATEOAS responses. All mappings on this controller therefore produce a {@link MediaTypes#HAL_JSON} response.
 *
 * @since 0.1.0
 * @author Sparky Studios
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/", produces = "application/vnd.sparkystudios.traklibrary-hal+json;version=1.0")
public class DownloadableContentController {

    private final DownloadableContentService downloadableContentService;

    private final DownloadableContentRepresentationModelAssembler downloadableContentRepresentationModelAssembler;

    /**
     * End-point that will retrieve a {@link DownloadableContentDto} instance that matches the given ID and convert
     * it into a consumable HATEOAS response. If a {@link DownloadableContentDto} instance is found that matches the ID, then
     * that data is returned with a status of 200, however if the {@link DownloadableContentDto} cannot be found the method
     * will return a 404 and wrap the exception details in a {@link ApiError} with additional information.
     *
     * @param id The ID of the {@link DownloadableContentDto} to retrieve.
     *
     * @return The {@link DownloadableContentDto} that matches the given ID as a HATEOAS response.
     */
    @AllowedForUser
    @GetMapping("/dlc/{id}")
    public EntityModel<DownloadableContentDto> findById(@PathVariable long id) {
        return downloadableContentRepresentationModelAssembler.toModel(downloadableContentService.findById(id));
    }
}
