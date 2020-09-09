package com.traklibrary.game.service.impl;

import com.traklibrary.game.repository.GameBarcodeRepository;
import com.traklibrary.game.service.GameBarcodeService;
import com.traklibrary.game.service.dto.GameBarcodeDto;
import com.traklibrary.game.service.mapper.GameBarcodeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@RequiredArgsConstructor
@Service
public class GameBarcodeServiceImpl implements GameBarcodeService {

    private static final String NOT_FOUND_MESSAGE = "game-barcode.exception.barcode-not-found";

    private final GameBarcodeRepository gameBarcodeRepository;
    private final MessageSource messageSource;
    private final GameBarcodeMapper gameBarcodeMapper;

    @Override
    @Transactional(readOnly = true)
    public GameBarcodeDto findByBarcode(String barcode) {
        String errorMessage = messageSource
                .getMessage(NOT_FOUND_MESSAGE, new Object[] { barcode }, LocaleContextHolder.getLocale());

        return gameBarcodeMapper.gameBarcodeToGameBarcodeDto(gameBarcodeRepository.findByBarcode(barcode)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage)));
    }
}
