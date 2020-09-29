package com.sparkystudios.traklibrary.game.domain.converter;

import com.sparkystudios.traklibrary.game.domain.GameMode;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.EnumSet;
import java.util.Set;

@Converter(autoApply = true)
public class GameModeAttributeConverter implements AttributeConverter<Set<GameMode>, Long> {

    @Override
    public Long convertToDatabaseColumn(Set<GameMode> gameModes) {
        long flagValue = 0L;
        if (gameModes != null) {
            for (GameMode gameMode : gameModes) {
                flagValue |= (long)Math.pow(2, gameMode.getFlagValue());
            }
        }

        return flagValue;
    }

    @Override
    public Set<GameMode> convertToEntityAttribute(Long flags) {
        Set<GameMode> result = EnumSet.noneOf(GameMode.class);

        if (flags != null) {
            EnumSet<GameMode> gameModes = EnumSet.allOf(GameMode.class);
            for (GameMode gameMode : gameModes) {
                short flagValue = gameMode.getFlagValue();
                long flag = Math.round(Math.pow(2, flagValue));

                if ((flags & flag) != 0) {
                    result.add(gameMode);
                }
            }
        }

        return result;
    }
}
