package com.sparkystudios.traklibrary.game.server.controller;

import com.sparkystudios.traklibrary.game.domain.GameImage;
import com.sparkystudios.traklibrary.game.domain.ImageSize;
import com.sparkystudios.traklibrary.game.service.DownloadableContentImageService;
import com.sparkystudios.traklibrary.game.service.dto.DownloadableContentDto;
import com.sparkystudios.traklibrary.security.annotation.AllowedForModeratorWithGameWriteAuthority;
import com.sparkystudios.traklibrary.security.exception.ApiError;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * The {@link DownloadableContentImageController} is a simple controller class that exposes an API that is used to upload and download images for
 * different DLC's at different sizes/dimensions. It provides API end-points for uploading and downloading images (with each size given
 * an individual end-point). It should be noted that the controller itself contains very little logic, the logic is contained within the
 * {@link DownloadableContentImageService}. The controllers primary purpose is to wrap the responses it received from the {@link DownloadableContentImageService}
 * into byte arrays. All mappings on this controller therefore produce a application/octet-stream response.
 *
 * @since 0.1.0
 * @author Sparky Studios
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/dlc/{id}/image", produces = "application/vnd.sparkystudios.traklibrary-hal+json;version=1.0")
public class DownloadableContentImageController {

    private final DownloadableContentImageService downloadableContentImageService;

    /**
     * End-point that create a {@link GameImage} instance and register
     * the specified file with the chosen image provider. If the file is in a incorrect format or a
     * image already exists for the given {@link DownloadableContentDto}, an exception will be thrown and an {@link ApiError}
     * will be returned to the callee.
     *
     * {@link GameImage}'s can only be created for users with moderator privileges.
     *
     * @param id The ID of the {@link DownloadableContentDto} to persist and image for.
     * @param imageSize The size of the image to upload.
     * @param file The {@link MultipartFile} containing the image to upload.
     */
    @AllowedForModeratorWithGameWriteAuthority
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void saveDownloadableContentImageForDownloadableContentIdAndGameImageSize(@PathVariable long id,
                                                                                     @RequestParam("image-size") ImageSize imageSize,
                                                                                     @RequestPart MultipartFile file) {
        downloadableContentImageService.upload(id, imageSize, file);
    }

    /**
     * End-point that will retrieve a {@link ByteArrayResource} for the image that is associated with the given
     * {@link DownloadableContentDto} ID and the given {@link ImageSize}. If no image is
     * associated with the {@link DownloadableContentDto} or it fails to retrieve the data, an empty {@link ByteArrayResource}
     * will be returned and the error will be logged.
     *
     * This end-point can be called anonymously by anyone without providing any authentication or credentials.
     *
     * @param id The ID of the {@link DownloadableContentDto} to retrieve the associated image for.
     *
     * @return A {@link ByteArrayResource} representing the byte information of the image file.
     */
    @GetMapping(produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<ByteArrayResource> findDownloadableContentImageByDownloadableContentIdAndImageSize(@PathVariable long id,
                                                                                                             @RequestParam(required = false, defaultValue = "SMALL", name = "image-size") ImageSize imageSize) {
        // Get the image data, all images are stored as *.png so it's safe to assume the file extension.
        var imageDataDto = downloadableContentImageService.download(id, ImageSize.SMALL);
        return ResponseEntity
                .ok()
                .contentLength(imageDataDto.getContent().length)
                .header("Content-Disposition", "attachment; filename=\"" + imageDataDto.getFilename()+ "\"")
                .body(new ByteArrayResource(imageDataDto.getContent()));
    }
}
