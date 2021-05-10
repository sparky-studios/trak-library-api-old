package com.sparkystudios.traklibrary.game.service.client.impl;

import com.google.common.io.Files;
import com.sparkystudios.traklibrary.game.service.client.ImageClient;
import com.sparkystudios.traklibrary.game.service.exception.UploadFailedException;
import com.sparkystudios.traklibrary.security.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Component
public class ImageClientCircuitBreakerImpl implements ImageClient {

    private static final String COMPANY_UPLOAD_FAILED_MESSAGE = "company-image.exception.upload-failed";
    private static final String DOWNLOADABLE_CONTENT_UPLOAD_FAILED_MESSAGE = "downloadable-content-image.exception.upload-failed";
    private static final String GAME_UPLOAD_FAILED_MESSAGE = "game-image.exception.upload-failed";
    private static final String PLATFORM_UPLOAD_FAILED_MESSAGE = "platform-image.exception.upload-failed";

    private final AuthenticationService authenticationService;
    @SuppressWarnings("all")
    private final CircuitBreakerFactory circuitBreakerFactory;
    private final MessageSource messageSource;
    private final RestTemplate restTemplate;

    @Setter
    private CircuitBreaker imageServerCircuitBreaker;

    @PostConstruct
    private void postConstruct() {
        imageServerCircuitBreaker = circuitBreakerFactory.create("image-server-circuit-breaker");
    }

    @Override
    public void uploadCompanyImage(MultipartFile multipartFile, long companyId) {
        uploadImage(multipartFile, COMPANY_UPLOAD_FAILED_MESSAGE, companyId, "/companies");
    }

    @Override
    public void uploadDownloadableContentImage(MultipartFile multipartFile, long downloadableContentId) {
        uploadImage(multipartFile, DOWNLOADABLE_CONTENT_UPLOAD_FAILED_MESSAGE, downloadableContentId, "/dlc");
    }

    @Override
    public void uploadGameImage(MultipartFile multipartFile, long gameId) {
        uploadImage(multipartFile, GAME_UPLOAD_FAILED_MESSAGE, gameId, "");
    }

    @Override
    public void uploadPlatformImage(MultipartFile multipartFile, long platformId) {
        uploadImage(multipartFile, PLATFORM_UPLOAD_FAILED_MESSAGE, platformId, "/platforms");
    }

    private void uploadImage(MultipartFile multipartFile, String failureMessage, long id, String folder) {
        // Create a temporary directory for the file to ensure the name doesn't clash with any other files.
        @SuppressWarnings("UnstableApiUsage")
        var file = new File(Files.createTempDir(), Objects.requireNonNull(multipartFile.getOriginalFilename()));
        // Write the contents of the file to the new temporary file created.
        try (var stream = new FileOutputStream(file)) {
            stream.write(multipartFile.getBytes());
        } catch (IOException e) {
            String errorMessage = messageSource
                    .getMessage(failureMessage, new Object[] { id }, LocaleContextHolder.getLocale());

            throw new UploadFailedException(errorMessage, e);
        }

        // Create the http headers with the multi-part file for the image service request.
        var httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        httpHeaders.setBearerAuth(authenticationService.getToken());

        // Jackson struggles to serialize MultipartFile objects, so it's sent up as a file system resource.
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));

        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(body, httpHeaders);

        // Send the request. We'll fail the call if the image can't be uploaded to keep the table
        // and image provider in sync with one another.
        imageServerCircuitBreaker.run(() ->
                restTemplate.postForEntity("http://trak-image-server/games" + folder, requestEntity, Void.class), throwable -> {
            String errorMessage = messageSource
                    .getMessage(failureMessage, new Object[] { id }, LocaleContextHolder.getLocale());

            throw new UploadFailedException(errorMessage, throwable);
        });
    }

    @Override
    public byte[] downloadCompanyImage(String filename) {
        return downloadImage("/companies", filename);
    }

    @Override
    public byte[] downloadDownloadableContentImage(String filename) {
        return downloadImage("/dlc", filename);
    }

    @Override
    public byte[] downloadGameImage(String filename) {
        return downloadImage("", filename);
    }

    @Override
    public byte[] downloadPlatformImage(String filename) {
        return downloadImage("/platforms", filename);
    }

    private byte[] downloadImage(String folder, String filename) {
        return imageServerCircuitBreaker.run(() -> {
                    ByteArrayResource resource = restTemplate
                            .getForObject("http://trak-image-server/games" + folder + "/{filename}", ByteArrayResource.class, filename);

                    return resource != null ? resource.getByteArray() : new byte[0];
                },
                throwable -> {
                    log.error("Failed to retrieve image: " + folder + "/" + filename, throwable);
                    return new byte[0];
                });
    }
}
