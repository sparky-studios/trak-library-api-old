package com.sparky.trak.game.service;

import com.sparky.trak.game.service.dto.ImageDataDto;
import org.springframework.web.multipart.MultipartFile;

public interface GameImageService {

    /**
     * Given a {@link com.sparky.trak.game.service.dto.GameDto} ID and a {@link MultipartFile}, this service method
     * will attempt to upload the given image for the given {@link com.sparky.trak.game.service.dto.GameDto} to the
     * implemented image provider. When an image is being uploaded, a {@link com.sparky.trak.game.domain.GameImage}
     * instance will be persisted, which will contain information about the file and which {@link com.sparky.trak.game.service.dto.GameDto}
     * it is linked to. Only one {@link com.sparky.trak.game.domain.GameImage} can be persisted at any one for a
     * {@link com.sparky.trak.game.service.dto.GameDto}.
     *
     * The {@link GameImageService} assumes that the image is stored within a different location, such as central
     * image provider such as an AWS S3 bucket. Image storage is handled by the image service.
     *
     * If the image fails to upload or persist for any reason, a {@link com.sparky.trak.game.service.exception.UploadFailedException}
     * will be thrown to the callee.
     *
     * @param gameId The ID of the {@link com.sparky.trak.game.service.dto.GameDto} to upload an image for.
     * @param multipartFile The image that is to be associated with the given {@link com.sparky.trak.game.service.dto.GameDto}.
     *
     * @throws com.sparky.trak.game.service.exception.UploadFailedException Thrown if image uploading fails.
     */
    void upload(long gameId, MultipartFile multipartFile);

    /**
     * Given the ID of a {@link com.sparky.trak.game.service.dto.GameDto}, this method will attempt to find the
     * {@link com.sparky.trak.game.domain.GameImage} that is mapped to the given ID. If one is found, the method will
     * invoke the image service to return the byte data of the given image file that is associated with the
     * {@link com.sparky.trak.game.domain.GameImage} and return the byte data with additional information wrapped in
     * a {@link ImageDataDto} object.
     *
     * If the download of the image data fails, an {@link ImageDataDto} is still returned, but the {@link ImageDataDto#getContent()}
     * will return an empty byte array.
     *
     * @param gameId The ID of the {@link com.sparky.trak.game.service.dto.GameDto} to retrieve image data for.
     *
     * @return An {@link ImageDataDto} object that contains the image information for the given {@link com.sparky.trak.game.service.dto.GameDto}.
     */
    ImageDataDto download(long gameId);
}
