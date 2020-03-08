package com.sparky.maidcafe.game.service.impl;

import com.sparky.maidcafe.game.repository.GameRepository;
import com.sparky.maidcafe.game.repository.specification.GameSpecification;
import com.sparky.maidcafe.game.service.GameService;
import com.sparky.maidcafe.game.service.PatchService;
import com.sparky.maidcafe.game.service.dto.GameDto;
import com.sparky.maidcafe.game.service.mapper.GameMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.json.JsonMergePatch;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final GameMapper gameMapper;
    private final MessageSource messageSource;
    private final PatchService patchService;

    @Override
    public GameDto save(GameDto gameDto) {
        Objects.requireNonNull(gameDto);

        if (gameRepository.existsById(gameDto.getId())) {
            String errorMessage = messageSource
                    .getMessage("game.exception.entity-exists", new Object[] { gameDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityExistsException(errorMessage);
        }

        return gameMapper.gameToGameDto(gameRepository.save(gameMapper.gameDtoToGame(gameDto)));
    }

    @Override
    public GameDto findById(long id) {
        String errorMessage = messageSource
                .getMessage("game.exception.not-found", new Object[] { id }, LocaleContextHolder.getLocale());

        return gameMapper.gameToGameDto(gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage)));
    }

    @Override
    public Iterable<GameDto> findAll(GameSpecification gameSpecification, Pageable pageable) {
        Objects.requireNonNull(pageable);

        return gameRepository.findAll(gameSpecification, pageable)
                .map(gameMapper::gameToGameDto);
    }

    @Override
    public GameDto update(GameDto gameDto) {
        Objects.requireNonNull(gameDto);

        if (!gameRepository.existsById(gameDto.getId())) {
            String errorMessage = messageSource
                    .getMessage("game.exception.not-found", new Object[] { gameDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        return gameMapper.gameToGameDto(gameRepository.save(gameMapper.gameDtoToGame(gameDto)));
    }

    @Override
    public GameDto patch(long id, JsonMergePatch jsonMergePatch) {
        // Set the new Java object with the patch information.
        GameDto patched = patchService.patch(jsonMergePatch, findById(id), GameDto.class);
        // Save to the repository and convert it back to a GameDto.
        return gameMapper.gameToGameDto(gameRepository.save(gameMapper.gameDtoToGame(patched)));
    }

    @Override
    public void deleteById(long id) {
        if (!gameRepository.existsById(id)) {
            String errorMessage = messageSource
                    .getMessage("game.exception.not-found", new Object[] { id }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        gameRepository.deleteById(id);
    }
}
