package com.sparkystudios.traklibrary.image.server.controller;

import com.sparkystudios.traklibrary.image.service.ImageService;
import com.sparkystudios.traklibrary.image.service.exception.ImageFailedException;
import com.sparkystudios.traklibrary.security.annotation.AllowedForModeratorWithDeveloperWriteAuthority;
import com.sparkystudios.traklibrary.security.annotation.AllowedForModeratorWithGameWriteAuthority;
import com.sparkystudios.traklibrary.security.annotation.AllowedForModeratorWithPlatformWriteAuthority;
import com.sparkystudios.traklibrary.security.exception.ApiError;
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
 * @since 0.1.0
 * @author Sparky Studios
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/", produces = "application/vnd.sparkystudios.traklibrary+json;version=1.0")
public class ImageController {

    private final ImageService imageService;

    /**
     * End-point that is used to upload image data into the games storage, this image will be used to represent the thumbnail and
     * cover art of the game when viewed by a client. The end-point makes no assumptions that the image is being uploaded in a
     * valid format, validation is done within the {@link ImageService#upload(String, String, byte[])} method. If the image is
     * not in a valid format or it fails to upload to the chosen image provider, a {@link ImageFailedException} will be thrown
     * and the end-point will return an {@link ApiError} with exception details.
     *
     * This end-point can only be invoked by users that have moderator privileges associated with their account.
     *
     * @param file The file contents to upload to the image provider.
     *
     * @throws IOException Thrown if the bytes can't be retrieved from the file.
     */
    @AllowedForModeratorWithGameWriteAuthority
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(value = "/games", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadGameImage(@RequestPart MultipartFile file) throws IOException {
        imageService.upload("games", file.getOriginalFilename(), file.getBytes());
    }

    /**
     * End-point that is used to upload image data into the games/companies storage, this image will be used to represent the image
     * of the game company when viewed by a client. The end-point makes no assumptions that the image is being uploaded in a
     * valid format, validation is done within the {@link ImageService#upload(String, String, byte[])} method. If the image is
     * not in a valid format or it fails to upload to the chosen image provider, a {@link ImageFailedException} will be thrown
     * and the end-point will return an {@link ApiError} with exception details.
     *
     * This end-point can only be invoked by users that have moderator privileges associated with their account.
     *
     * @param file The file contents to upload to the image provider.
     *
     * @throws IOException Thrown if the bytes can't be retrieved from the file.
     */
    @AllowedForModeratorWithDeveloperWriteAuthority
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(value = "/games/companies", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadCompanyImage(@RequestPart MultipartFile file) throws IOException {
        imageService.upload("games/companies", file.getOriginalFilename(), file.getBytes());
    }

    /**
     * End-point that is used to upload image data into the games/dlc storage, this image will be used to represent the thumbnail and
     * cover art of the game DLC when viewed by a client. The end-point makes no assumptions that the image is being uploaded in a
     * valid format, validation is done within the {@link ImageService#upload(String, String, byte[])} method. If the image is
     * not in a valid format or it fails to upload to the chosen image provider, a {@link ImageFailedException} will be thrown
     * and the end-point will return an {@link ApiError} with exception details.
     *
     * This end-point can only be invoked by users that have moderator privileges associated with their account.
     *
     * @param file The file contents to upload to the image provider.
     *
     * @throws IOException Thrown if the bytes can't be retrieved from the file.
     */
    @AllowedForModeratorWithGameWriteAuthority
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(value = "/games/dlc", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadDownloadableContentImage(@RequestPart MultipartFile file) throws IOException {
        imageService.upload("games/dlc", file.getOriginalFilename(), file.getBytes());
    }

    /**
     * End-point that is used to upload image data into the games/platforms storage, this image will be used to represent the image of
     * a platform when viewed by a client. The end-point makes no assumptions that the image is being uploaded in a
     * valid format, validation is done within the {@link ImageService#upload(String, String, byte[])} method. If the image is
     * not in a valid format or it fails to upload to the chosen image provider, a {@link ImageFailedException} will be thrown
     * and the end-point will return an {@link ApiError} with exception details.
     *
     * This end-point can only be invoked by users that have moderator privileges associated with their account.
     *
     * @param file The file contents to upload to the image provider.
     *
     * @throws IOException Thrown if the bytes can't be retrieved from the file.
     */
    @AllowedForModeratorWithPlatformWriteAuthority
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(value = "/games/platforms", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadPlatformImage(@RequestPart MultipartFile file) throws IOException {
        imageService.upload("games/platforms", file.getOriginalFilename(), file.getBytes());
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
        return getResponseEntity(imageService.download("games/games", filename), filename);
    }

    /**
     * End-point that is used to retrieve the image data for a game company thumbnail that is mapped to the given filename.
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
    @GetMapping(value = "/games/companies/{filename}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<ByteArrayResource> downloadCompanyImage(@PathVariable String filename) {
        // Get the image data, all images are stored as *.png so it's safe to assume the file extension.
        return getResponseEntity(imageService.download("games/companies", filename), filename);
    }

    /**
     * End-point that is used to retrieve the image data for a game DLC that is mapped to the given filename.
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
    @GetMapping(value = "/games/dlc/{filename}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<ByteArrayResource> downloadDownloadableContentImage(@PathVariable String filename) {
        // Get the image data, all images are stored as *.png so it's safe to assume the file extension.
        return getResponseEntity(imageService.download("games/dlc", filename), filename);
    }

    /**
     * End-point that is used to retrieve the image data for a game platform that is mapped to the given filename.
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
    @GetMapping(value = "/games/platforms/{filename}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<ByteArrayResource> downloadPlatformImage(@PathVariable String filename) {
        // Get the image data, all images are stored as *.png so it's safe to assume the file extension.
        return getResponseEntity(imageService.download("games/platforms", filename), filename);
    }

    private ResponseEntity<ByteArrayResource> getResponseEntity(byte[] imageData, String filename) {
        return ResponseEntity
                .ok()
                .contentLength(imageData.length)
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .body(new ByteArrayResource(imageData));
    }
}
