package com.sparkystudios.traklibrary.game.domain.converter;

import com.sparkystudios.traklibrary.game.domain.GameRegion;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class GameRegionAttributeConverter implements AttributeConverter<GameRegion, Short> {

    @Override
    public Short convertToDatabaseColumn(GameRegion gameRegion) {
        if (gameRegion == null) {
            return GameRegion.NORTH_AMERICA.getId();
        }

        return gameRegion.getId();
    }

    @Override
    public GameRegion convertToEntityAttribute(Short gameRegionId) {
        if (gameRegionId == null) {
            return GameRegion.NORTH_AMERICA;
        }

        return Stream.of(GameRegion.values())
                .filter(ag -> ag.getId() == gameRegionId)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}