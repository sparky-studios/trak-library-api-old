package com.sparkystudios.traklibrary.game.server.controller;

import com.sparkystudios.traklibrary.game.domain.GameImage;
import com.sparkystudios.traklibrary.game.domain.GameImageSize;
import com.sparkystudios.traklibrary.game.service.GameImageService;
import com.sparkystudios.traklibrary.game.service.dto.GameDto;
import com.sparkystudios.traklibrary.game.service.dto.ImageDataDto;
import com.sparkystudios.traklibrary.security.annotation.AllowedForAdmin;
import com.sparkystudios.traklibrary.security.exception.ApiError;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * The {@link GameImageController} is a simple controller class that exposes an API that is used to upload and download images for
 * different games at different sizes/dimensions. It provides API end-points for uploading and downloading images (with each size given
 * an individual end-point). It should be noted that the controller itself contains very little logic, the logic is contained within the
 * {@link GameImageService}. The controllers primary purpose is to wrap the responses it received from the {@link GameImageService}
 * into byte arrays. All mappings on this controller therefore produce a application/octet-stream response.
 *
 * @since 0.1.0
 * @author Sparky Studios
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/{id}/images", produces = "application/vnd.traklibrary.v1.hal+json")
public class GameImageController {

    private final GameImageService gameImageService;

    /**
     * End-point that create a {@link GameImage} instance and register
     * the specified file with the chosen image provider. If the file is in a incorrect format or a
     * image already exists for the given {@link GameDto}, an exception will be thrown and an {@link ApiError}
     * will be returned to the callee.
     *
     * {@link GameImage}'s can only be created for users with admin privileges.
     *
     * @param id The ID of the {@link GameDto} to persist and image for.
     * @param gameImageSize The size of the image to upload.
     * @param file The {@link MultipartFile} containing the image to upload.
     */
    @AllowedForAdmin
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void saveGameImageForGameIdAndGameImageSize(@PathVariable long id,
                                                       @RequestParam("image-size") GameImageSize gameImageSize,
                                                       @RequestPart MultipartFile file) {
        gameImageService.upload(id, gameImageSize, file);
    }

    /**
     * End-point that will retrieve a {@link ByteArrayResource} for the image that is associated with the given
     * {@link GameDto} ID and is assigned that image size of {@link GameImageSize#SMALL}. If no image is
     * associated with the {@link GameDto} or it fails to retrieve the data, an empty {@link ByteArrayResource}
     * will be returned and the error will be logged.
     *
     * This end-point can be called anonymously by anyone without providing any authentication or credentials.
     *
     * @param id The ID of the {@link GameDto} to retrieve the associated image for.
     *
     * @return A {@link ByteArrayResource} representing the byte information of the image file.
     */
    @GetMapping(value = "/small", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<ByteArrayResource> findGameImageByGameIdAndImageSizeSmall(@PathVariable long id) {
        // Get the image data, all images are stored as *.png so it's safe to assume the file extension.
        ImageDataDto imageDataDto = gameImageService.download(id, GameImageSize.SMALL);
        return getImageDataAsByteArrayResource(imageDataDto.getFilename(), imageDataDto.getContent());
    }

    /**
     * End-point that will retrieve a {@link ByteArrayResource} for the image that is associated with the given
     * {@link GameDto} ID and is assigned that image size of {@link GameImageSize#MEDIUM}. If no image is
     * associated with the {@link GameDto} or it fails to retrieve the data, an empty {@link ByteArrayResource}
     * will be returned and the error will be logged.
     *
     * This end-point can be called anonymously by anyone without providing any authentication or credentials.
     *
     * @param id The ID of the {@link GameDto} to retrieve the associated image for.
     *
     * @return A {@link ByteArrayResource} representing the byte information of the image file.
     */
    @GetMapping(value = "/medium", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<ByteArrayResource> findGameImageByGameIdAndImageSizeMedium(@PathVariable long id) {
        // Get the image data, all images are stored as *.png so it's safe to assume the file extension.
        ImageDataDto imageDataDto = gameImageService.download(id, GameImageSize.MEDIUM);
        return getImageDataAsByteArrayResource(imageDataDto.getFilename(), imageDataDto.getContent());
    }

    /**
     * End-point that will retrieve a {@link ByteArrayResource} for the image that is associated with the given
     * {@link GameDto} ID and is assigned that image size of {@link GameImageSize#LARGE}. If no image is
     * associated with the {@link GameDto} or it fails to retrieve the data, an empty {@link ByteArrayResource}
     * will be returned and the error will be logged.
     *
     * This end-point can be called anonymously by anyone without providing any authentication or credentials.
     *
     * @param id The ID of the {@link GameDto} to retrieve the associated image for.
     *
     * @return A {@link ByteArrayResource} representing the byte information of the image file.
     */
    @GetMapping(value = "/large", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<ByteArrayResource> findGameImageByGameIdAndImageSizeLarge(@PathVariable long id) {
        // Get the image data, all images are stored as *.png so it's safe to assume the file extension.
        ImageDataDto imageDataDto = gameImageService.download(id, GameImageSize.LARGE);
        return getImageDataAsByteArrayResource(imageDataDto.getFilename(), imageDataDto.getContent());
    }

    private ResponseEntity<ByteArrayResource> getImageDataAsByteArrayResource(String filename, byte[] imageData) {
        return ResponseEntity
                .ok()
                .contentLength(imageData.length)
                .header("Content-Disposition", "attachment; filename=\"" + filename+ "\"")
                .body(new ByteArrayResource(imageData));
    }
}
