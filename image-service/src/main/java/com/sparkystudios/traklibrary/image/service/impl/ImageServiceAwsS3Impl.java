package com.sparkystudios.traklibrary.image.service.impl;

import com.amazonaws.util.IOUtils;
import com.google.common.io.Files;
import com.sparkystudios.traklibrary.image.service.ImageService;
import com.sparkystudios.traklibrary.image.service.exception.ImageFailedException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.WritableResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;

@RequiredArgsConstructor
@Service
public class ImageServiceAwsS3Impl implements ImageService {

    private static final String INVALID_FILE_FORMAT_MESSAGE = "image.exception.invalid-file-format";
    private static final String UPLOAD_FAILED_MESSAGE = "image.exception.upload-failed";
    private static final String DOWNLOAD_FAILED_MESSAGE = "image.exception.download-failed";

    @Setter
    @Value("${trak.aws.s3.bucket-name}")
    private String bucketName;

    private final MessageSource messageSource;
    private final ResourceLoader resourceLoader;

    @Override
    public void upload(String folder, String filename, byte[] content) {
        // Only allow the uploading of valid image files.
        @SuppressWarnings("UnstableApiUsage")
        String extension = Files.getFileExtension(filename);

        if (!Arrays.asList("png", "jpg", "jpeg").contains(extension)) {
            throw new IllegalArgumentException(messageSource
                    .getMessage(INVALID_FILE_FORMAT_MESSAGE, new Object[] {}, LocaleContextHolder.getLocale()));
        }

        WritableResource resource = (WritableResource) resourceLoader.getResource("s3://" + bucketName + "/" + folder + "/" + filename);

        try (var outputStream = resource.getOutputStream()) {
            outputStream.write(content);
        } catch (IOException e) {
            String errorMessage = messageSource
                    .getMessage(UPLOAD_FAILED_MESSAGE, new Object[] {filename}, LocaleContextHolder.getLocale());

            throw new ImageFailedException(errorMessage, e);
        }
    }

    @Override
    public byte[] download(String folder, String filename) {
        var resource = resourceLoader.getResource("s3://" + bucketName + "/" + folder + "/" + filename);

        try (var inputStream = resource.getInputStream()) {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            String errorMessage = messageSource
                    .getMessage(DOWNLOAD_FAILED_MESSAGE, new Object[] {filename}, LocaleContextHolder.getLocale());

            throw new ImageFailedException(errorMessage, e);
        }
    }
}
