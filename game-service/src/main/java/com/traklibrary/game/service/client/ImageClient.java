package com.traklibrary.game.service.client;

import org.springframework.web.multipart.MultipartFile;

public interface ImageClient {

    void uploadGameImage(MultipartFile multipartFile, long gameId);

    byte[] downloadGameImage(String filename, long gameId);
}
