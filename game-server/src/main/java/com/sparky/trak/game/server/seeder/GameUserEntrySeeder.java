package com.sparky.trak.game.server.seeder;

import com.sparky.trak.game.domain.GamePlatformXref;
import com.sparky.trak.game.domain.GameUserEntryStatus;
import com.sparky.trak.game.repository.GamePlatformXrefRepository;
import com.sparky.trak.game.service.GameUserEntryService;
import com.sparky.trak.game.service.dto.GameUserEntryDto;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@Profile({ "test", "development" })
@RequiredArgsConstructor
@Component
public class GameUserEntrySeeder implements Runnable {

    @Setter
    @Value("${seeding.game-user-entry.user-count ?: 1}")
    private int userCount;

    private final GameUserEntryService gameUserEntryService;
    private final GamePlatformXrefRepository gamePlatformXrefRepository;

    @Override
    public void run() {
        Random random = new SecureRandom();

        List<GamePlatformXref> gamePlatformXrefs = StreamSupport.stream(gamePlatformXrefRepository.findAll().spliterator(), false)
                .sorted(Comparator.comparingLong(GamePlatformXref::getGameId))
                .collect(Collectors.toList());

        IntStream.range(0, userCount).forEach(i -> {

            for (GamePlatformXref gamePlatformXref : gamePlatformXrefs) {
                GameUserEntryDto gameUserEntryDto = new GameUserEntryDto();
                gameUserEntryDto.setGameId(gamePlatformXref.getGameId());
                gameUserEntryDto.setPlatformId(gamePlatformXref.getPlatformId());
                gameUserEntryDto.setUserId(i);
                gameUserEntryDto.setRating((short)random.nextInt(6));
                gameUserEntryDto.setStatus(GameUserEntryStatus.values()[random.nextInt(GameUserEntryStatus.values().length)]);

                gameUserEntryService.save(gameUserEntryDto);
            }
        });
    }
}
