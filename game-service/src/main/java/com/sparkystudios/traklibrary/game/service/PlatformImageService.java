package com.sparkystudios.traklibrary.game.service;

import com.sparkystudios.traklibrary.game.domain.PlatformImage;
import com.sparkystudios.traklibrary.game.service.dto.ImageDataDto;
import com.sparkystudios.traklibrary.game.service.dto.PlatformDto;
import com.sparkystudios.traklibrary.game.service.exception.UploadFailedException;
import org.springframework.web.multipart.MultipartFile;

public interface PlatformImageService {

    /**
     * Given a {@link PlatformDto} ID and a {@link MultipartFile}, this service method
     * will attempt to upload the given image for the given {@link PlatformDto} to the
     * implemented image provider. When an image is being uploaded, a {@link PlatformImage}
     * instance will be persisted, which will contain information about the file and which {@link PlatformDto}
     * it is linked to. Only one {@link PlatformImage} can be persisted at any one for a
     * {@link PlatformDto}.
     *
     * The {@link PlatformImageService} assumes that the image is stored within a different location, such as central
     * image provider such as an AWS S3 bucket. Image storage is handled by the image service.
     *
     * If the image fails to upload or persist for any reason, a {@link UploadFailedException}
     * will be thrown to the callee.
     *
     * @param platformId The ID of the {@link PlatformDto} to upload an image for.
     * @param multipartFile The image that is to be associated with the given {@link PlatformDto}.
     *
     * @throws UploadFailedException Thrown if image uploading fails.
     */
    void upload(long platformId, MultipartFile multipartFile);

    /**
     * Given the ID of a {@link PlatformDto}, this method will attempt to find the
     * {@link PlatformImage} that is mapped to the given ID. If one is found, the method will invoke the
     * image service to return the byte data of the given image file that is associated with the
     * {@link PlatformImage} and return the byte data with additional information wrapped in
     * a {@link ImageDataDto} object.
     *
     * If the download of the image data fails, an {@link ImageDataDto} is still returned, but the {@link ImageDataDto#getContent()}
     * will return an empty byte array.
     *
     * @param platformId The ID of the {@link PlatformDto} to retrieve image data for.
     *
     * @return An {@link ImageDataDto} object that contains the image information for the given {@link PlatformDto}.
     */
    ImageDataDto download(long platformId);
}
