package com.sparky.trak.image.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.google.common.io.Files;
import com.sparky.trak.image.service.ImageService;
import com.sparky.trak.image.service.exception.ImageFailedException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

@RequiredArgsConstructor
@Service
public class ImageServiceAwsS3Impl implements ImageService {

    @Setter
    @Value("${trak.aws.s3.bucket-name}")
    private String bucketName;

    private final AmazonS3 amazonS3;
    private final MessageSource messageSource;

    @Override
    public void upload(String folder, String filename, byte[] content) {
        // Only allow the uploading of valid image files.
        @SuppressWarnings("UnstableApiUsage")
        String extension = Files.getFileExtension(filename);

        if (!Arrays.asList("png", "jpg", "jpeg").contains(extension)) {
            throw new IllegalArgumentException(messageSource
                    .getMessage("image.exception.invalid-file-format", new Object[] {}, LocaleContextHolder.getLocale()));
        }

        try {
            Path path = java.nio.file.Files.createTempFile(folder, filename);

            try (FileOutputStream stream = new FileOutputStream(path.toFile())) {
                stream.write(content);
                amazonS3.putObject(bucketName, folder + "/" + filename, path.toFile());
            }
        } catch (IOException e) {
            String errorMessage = messageSource
                    .getMessage("image.exception.upload-failed", new Object[] {filename}, LocaleContextHolder.getLocale());

            throw new ImageFailedException(errorMessage, e);
        }
    }

    @Override
    public byte[] download(String folder, String filename) {
        try (S3Object object = amazonS3.getObject(bucketName, folder + "/" + filename)) {
            S3ObjectInputStream stream = object.getObjectContent();
            return IOUtils.toByteArray(stream);
        } catch (IOException e) {
            String errorMessage = messageSource
                    .getMessage("image.exception.download-failed", new Object[] {filename}, LocaleContextHolder.getLocale());

            throw new ImageFailedException(errorMessage, e);
        }
    }
}
