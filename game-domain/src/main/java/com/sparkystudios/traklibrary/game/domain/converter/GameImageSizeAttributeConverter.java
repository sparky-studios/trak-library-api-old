package com.sparkystudios.traklibrary.game.domain.converter;

import com.sparkystudios.traklibrary.game.domain.GameImageSize;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Objects;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class GameImageSizeAttributeConverter implements AttributeConverter<GameImageSize, Short> {

    @Override
    public Short convertToDatabaseColumn(GameImageSize gameImageSize) {
        return Objects.requireNonNullElse(gameImageSize, GameImageSize.SMALL).getId();
    }

    @Override
    public GameImageSize convertToEntityAttribute(Short gameImageSizeId) {
        if (gameImageSizeId == null) {
            return GameImageSize.SMALL;
        }

        return Stream.of(GameImageSize.values())
                .filter(ag -> ag.getId() == gameImageSizeId)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
