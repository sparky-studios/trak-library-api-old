package com.sparkystudios.traklibrary.game.service.impl;

import com.sparkystudios.traklibrary.game.domain.Game;
import com.sparkystudios.traklibrary.game.repository.DownloadableContentRepository;
import com.sparkystudios.traklibrary.game.repository.GameRepository;
import com.sparkystudios.traklibrary.game.service.DownloadableContentService;
import com.sparkystudios.traklibrary.game.service.dto.DownloadableContentDto;
import com.sparkystudios.traklibrary.game.service.mapper.DownloadableContentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DownloadableContentServiceImpl implements DownloadableContentService {

    private static final String NOT_FOUND_MESSAGE = "downloadable-content-image.exception.not-found";
    private static final String GAME_NOT_FOUND_MESSAGE = "game.exception.not-found";

    private final DownloadableContentRepository downloadableContentRepository;
    private final GameRepository gameRepository;

    private final MessageSource messageSource;
    private final DownloadableContentMapper downloadableContentMapper;

    @Override
    public DownloadableContentDto findById(long id) {
        String errorMessage = messageSource
                .getMessage(NOT_FOUND_MESSAGE, new Object[] { id }, LocaleContextHolder.getLocale());

        return downloadableContentMapper.fromDownloadableContent(downloadableContentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage)));
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<DownloadableContentDto> findDownloadableContentsByGameId(long gameId) {
        // Get the game as the platforms can be lazily loaded from it.
        Optional<Game> game = gameRepository.findById(gameId);

        if (game.isEmpty()) {
            String errorMessage = messageSource
                    .getMessage(GAME_NOT_FOUND_MESSAGE, new Object[] { gameId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        // Retrieve all associated downloadable content and just convert them to their DTO counterparts.
        return game.get().getDownloadableContents().stream()
                .map(downloadableContentMapper::fromDownloadableContent)
                .collect(Collectors.toList());
    }
}
