package com.traklibrary.image.server.controller;

import com.traklibrary.image.server.annotation.AllowedForAdmin;
import com.traklibrary.image.server.exception.ApiError;
import com.traklibrary.image.service.ImageService;
import com.traklibrary.image.service.exception.ImageFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * The {@link ImageController} is a simple controller class that exposes a small number of end-points that are used to upload and
 * download different types of images to different services that require them, such as games and anime. It should be noted that the
 * controller itself contains very little logic, the logic is contained within the {@link ImageService}, which specifies the behavior
 * and which image provider to utilize. It should be noted that to reach any of these end-points within this controller, the user
 * must have a valid authentication token.
 *
 * @since 1.0.0
 * @author Sparky Studios
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/", produces = "application/vnd.traklibrary.v1+json")
public class ImageController {

    private final ImageService imageService;

    /**
     * End-point that is used to upload image data into the games storage, this image will be used to represent the thumbnail and
     * cover art of the game when viewed by a client. The end-point makes no assumptions that the image is being uploaded in a
     * valid format, validation is done within the {@link ImageService#upload(String, String, byte[])} method. If the image is
     * not in a valid format or it fails to upload to the chosen image provider, a {@link ImageFailedException} will be thrown
     * and the end-point will return an {@link ApiError} with exception details.
     *
     * This end-point can only be invoked by users that have admin privileges associated with their account.
     *
     * @param file The file contents to upload to the image provider.
     *
     * @throws IOException Thrown if the bytes can't be retrieved from the file.
     */
    @AllowedForAdmin
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(value = "/games", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadGameImage(@RequestPart MultipartFile file) throws IOException {
        imageService.upload("games", file.getOriginalFilename(), file.getBytes());
    }

    /**
     * End-point that is used to retrieve the image data for a game thumbnail that is mapped to the given filename.
     * The end-point assumes the data being downloaded has been uploaded to a valid image format
     *
     * If the image fails to download, an {@link ImageFailedException}
     * will be thrown and an {@link ApiError} will be returned to the
     * callee with exception details.
     *
     * @param filename The name of the file to retrieve image data for.
     *
     * @return A {@link ByteArrayResource} with contains the byte information of the requested image.
     */
    @GetMapping(value = "/games/{filename}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<ByteArrayResource> downloadGameImage(@PathVariable String filename) {
        // Get the image data, all images are stored as *.png so it's safe to assume the file extension.
        byte[] imageData = imageService.download("games", filename);

        return ResponseEntity
                .ok()
                .contentLength(imageData.length)
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .body(new ByteArrayResource(imageData));
    }
}
