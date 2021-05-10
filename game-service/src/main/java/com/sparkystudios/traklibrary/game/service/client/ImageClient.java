package com.sparkystudios.traklibrary.game.service.client;

import org.springframework.web.multipart.MultipartFile;

public interface ImageClient {

    void uploadCompanyImage(MultipartFile multipartFile, long companyId);

    void uploadDownloadableContentImage(MultipartFile multipartFile, long downloadableContentId);

    void uploadGameImage(MultipartFile multipartFile, long gameId);

    void uploadPlatformImage(MultipartFile multipartFile, long platformId);

    byte[] downloadCompanyImage(String filename);

    byte[] downloadDownloadableContentImage(String filename);

    byte[] downloadGameImage(String filename);

    byte[] downloadPlatformImage(String filename);
}
