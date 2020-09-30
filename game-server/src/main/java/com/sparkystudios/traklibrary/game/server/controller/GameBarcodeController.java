package com.sparkystudios.traklibrary.game.server.controller;

import com.sparkystudios.traklibrary.game.domain.GameBarcode;
import com.sparkystudios.traklibrary.game.server.assembler.GameBarcodeRepresentationModelAssembler;
import com.sparkystudios.traklibrary.game.service.GameBarcodeService;
import com.sparkystudios.traklibrary.game.service.GameService;
import com.sparkystudios.traklibrary.game.service.dto.GameBarcodeDto;
import com.sparkystudios.traklibrary.security.annotation.AllowedForUser;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The {@link GameBarcodeController} is a simple controller class that exposes a CRUD based API that is used to interact with
 * any entities or objects that pertain to {@link GameBarcode}s. It provides API end-points for creating, updating, finding
 * and deleting {@link GameBarcode} entities. It should be noted that the controller itself contains very little logic,
 * the logic is contained within the {@link GameBarcodeService}. The controllers primary purpose is to wrap the responses it
 * received from the {@link GameService} into HATEOAS responses. All mappings on this controller therefore produce a
 * {@link MediaTypes#HAL_JSON} response.
 *
 * @since 0.1.0
 * @author Sparky Studios
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/barcodes", produces = "application/vnd.traklibrary.v1.hal+json")
public class GameBarcodeController {

    private final GameBarcodeService gameBarcodeService;
    private final GameBarcodeRepresentationModelAssembler gameBarcodeRepresentationModelAssembler;

    /**
     * Given a barcode as a {@link String}, this endpoint will call the {@link GameBarcodeService#findByBarcode(String)}
     * method and try and to retrieve the {@link GameBarcodeDto} object that matches the given barcode. If the barcode
     * provided does not map to any known {@link GameBarcodeDto} object, then an exception will be thrown and an
     * {@link ApiError} with error details will be returned as the response.
     *
     * If a {@link GameBarcodeDto} object is found with a matching barcode, the response is wrapped into a HATEOAS
     * response and returned the callee.
     *
     * @param barcode The barcode of the {@link GameBarcodeDto} object to try and retrieve.
     *
     * @return The {@link GameBarcodeDto} object matching the barcode wrapped within a HATEOAS response.
     */
    @AllowedForUser
    @GetMapping("/{barcode}")
    public EntityModel<GameBarcodeDto> findByBarcode(@PathVariable String barcode) {
        return gameBarcodeRepresentationModelAssembler.toModel(gameBarcodeService.findByBarcode(barcode));
    }
}
