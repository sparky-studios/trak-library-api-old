package com.sparky.trak.image.server.controller;

import com.google.common.io.Files;
import com.sparky.trak.image.server.annotation.AllowedForAdmin;
import com.sparky.trak.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

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
@RequestMapping("/v1/images")
public class ImageController {

    private final ImageService imageService;

    /**
     * End-point that is used to upload image data for a given game, this image will be used to represent the thumbnail and
     * cover art of the game when viewed by a client. The end-point assumes that the image data being uploaded is in a png
     * format, if the image is not in a png format or it fails to upload to the chosen image provider, a
     * {@link com.sparky.trak.image.service.exception.ImageFailedException} will be thrown and the end-point will return
     * an {@link com.sparky.trak.image.server.exception.ApiError} with exception details.
     *
     * This end-point can only be invoked by users that have admin privileges associated with their account.
     *
     * @param id The ID of the game to associated the uploaded image data with.
     * @param file The file contents to upload to the image provider.
     *
     * @throws IOException Thrown if the bytes can't be retrieved from the file.
     */
    @AllowedForAdmin
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(path = "/games/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadGameImage(@PathVariable long id, @RequestPart MultipartFile file) throws IOException {
        imageService.upload("games", id + "." + Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename())), file.getBytes());
    }

    /**
     * End-point that is used to retrieve the image data for a game thumbnail that is mapped to the given ID.
     * The end-point assumes the data being downloaded is in the png image format, as none of the underlying
     * image providers will support other formats and uploading prevents any files that don't have the png
     * file extension.
     *
     * If the image fails to download, an {@link com.sparky.trak.image.service.exception.ImageFailedException}
     * will be thrown and an {@link com.sparky.trak.image.server.exception.ApiError} will be returned to the
     * callee with exception details.
     *
     * @param id The ID of the game to retrieve image data for.
     *
     * @return A {@link ByteArrayResource} with contains the byte information of the requested image.
     */
    @GetMapping("/games/{id}")
    public ResponseEntity<ByteArrayResource> downloadGameImage(@PathVariable long id) {
        // Get the image data, all images are stored as *.png so it's safe to assume the file extension.
        byte[] imageData = imageService.download("games", id + ".png");

        return ResponseEntity
                .ok()
                .contentLength(imageData.length)
                .header("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header("Content-Disposition", "attachment; filename=\"" + id + ".png\"")
                .body(new ByteArrayResource(imageData));
    }
}
