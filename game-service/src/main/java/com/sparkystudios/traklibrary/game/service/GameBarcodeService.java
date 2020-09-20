package com.sparkystudios.traklibrary.game.service;

import com.sparkystudios.traklibrary.game.domain.GameBarcode;
import com.sparkystudios.traklibrary.game.service.dto.GameBarcodeDto;

/**
 * The {@link GameBarcodeService} follows the basic CRUD principle for interaction with {@link GameBarcode} entities on the persistence layer.
 * However, the {@link GameBarcodeService} builds an additional layer of abstraction, with the primary purpose being checking the validity of
 * {@link GameBarcode} data being requested from the persistence layer, as well as encapsulating any domain-based objects into {@link GameBarcodeDto}
 * transfer objects, for additional validation and protection.
 *
 * The {@link GameBarcodeService} still follows the practise in that it will not catch or handle exceptions thrown by the persistence layer,
 * instead it will wrap them in a more reasonable response and propagate the exception to the callee.
 *
 * @since 1.0.0
 * @author Sparky Studios
 */
public interface GameBarcodeService {

    /**
     * Given a barcode as a {@link String}, this service method will query the underlying persistence layer and try and
     * retrieve the {@link GameBarcode} entity that matches the given barcode and map it to a {@link GameBarcodeDto}.
     * If the barcode provided does not map to any known {@link GameBarcode} entity, then an exception will be thrown
     * specifying that it can't be found.
     *
     * @param barcode The barcode of the {@link GameBarcode} entity to try and retrieve.
     *
     * @return The {@link GameBarcode} entity matching the barcode mapped to a {@link GameBarcodeDto}.
     */
    GameBarcodeDto findByBarcode(String barcode);
}
