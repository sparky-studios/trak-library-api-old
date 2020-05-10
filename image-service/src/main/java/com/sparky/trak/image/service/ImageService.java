package com.sparky.trak.image.service;

import com.sparky.trak.image.service.exception.ImageFailedException;

/**
 * The {@link ImageService} is an interface that is used to define all of the methods that
 * have to be implemented to ensure a contract between the Trak API and the underlying
 * image provider. The {@link ImageService} only provides two methods for the uploading and downloading
 * of images, any other filtering or functionality will need to be done within the confines of the
 * interfaces implementation.
 *
 * For an implementation, refer to the {@link com.sparky.trak.image.service.impl.ImageServiceAwsS3Impl}
 * which utilizes AWS S3 buckets as the method of storing and downloading images within a shared context.
 *
 * @since 1.0.0
 * @author Sparky Studios
 */
public interface ImageService {

    /**
     * Uploads and writes the byte content provided to the specified subfolder with the given name.
     * If the image fails to be written to the image provider, a {@link ImageFailedException} will be
     * thrown, specifying the reason.
     *
     * It should be noted, that additional errors can be thrown by implementations if the file
     * provided is not in the correct format and is an invalid file type.
     *
     * @param folder The subfolder to upload the image to.
     * @param name The name of the file to write the content to and upload.
     * @param content The content of the file to write to the image provider.
     */
    void upload(String folder, String name, byte[] content);

    /**
     * Downloads the information from the specified subfolder with the chosen name and retrieves the
     * byte contents of the file. If the file is not found within the image provider, a {@link ImageFailedException}
     * will be thrown and <code>null</code> will be returned to the callee.
     *
     * @param folder The subfolder to search for the specified image.
     * @param name The name of the image to retrieve.
     *
     * @return The contents of the file, in bytes or <code>null</code> if the image can't be found.
     */
    byte[] download(String folder, String name);
}
