package com.sparkystudios.traklibrary.game.service;

import com.sparkystudios.traklibrary.game.domain.CompanyImage;
import com.sparkystudios.traklibrary.game.service.dto.CompanyDto;
import com.sparkystudios.traklibrary.game.service.dto.ImageDataDto;
import com.sparkystudios.traklibrary.game.service.exception.UploadFailedException;
import org.springframework.web.multipart.MultipartFile;

public interface CompanyImageService {

    /**
     * Given a {@link CompanyDto} ID and a {@link MultipartFile}, this service method
     * will attempt to upload the given image for the given {@link CompanyDto} to the
     * implemented image provider. When an image is being uploaded, a {@link CompanyImage}
     * instance will be persisted, which will contain information about the file and which {@link CompanyDto}
     * it is linked to. Only one {@link CompanyImage} can be persisted at any one for a
     * {@link CompanyDto}.
     *
     * The {@link CompanyImageService} assumes that the image is stored within a different location, such as central
     * image provider such as an AWS S3 bucket. Image storage is handled by the image service.
     *
     * If the image fails to upload or persist for any reason, a {@link UploadFailedException}
     * will be thrown to the callee.
     *
     * @param companyId The ID of the {@link CompanyDto} to upload an image for.
     * @param multipartFile The image that is to be associated with the given {@link CompanyDto}.
     *
     * @throws UploadFailedException Thrown if image uploading fails.
     */
    void upload(long companyId, MultipartFile multipartFile);

    /**
     * Given the ID of a {@link CompanyDto}, this method will attempt to find the
     * {@link CompanyImage} that is mapped to the given ID. If one is found, the method will invoke the
     * image service to return the byte data of the given image file that is associated with the
     * {@link CompanyImage} and return the byte data with additional information wrapped in
     * a {@link ImageDataDto} object.
     *
     * If the download of the image data fails, an {@link ImageDataDto} is still returned, but the {@link ImageDataDto#getContent()}
     * will return an empty byte array.
     *
     * @param companyId The ID of the {@link CompanyDto} to retrieve image data for.
     *
     * @return An {@link ImageDataDto} object that contains the image information for the given {@link CompanyDto}.
     */
    ImageDataDto download(long companyId);
}
