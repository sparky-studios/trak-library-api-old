package com.sparkystudios.traklibrary.game.domain.converter;

import com.sparkystudios.traklibrary.game.domain.ImageSize;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Objects;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class GameImageSizeAttributeConverter implements AttributeConverter<ImageSize, Short> {

    @Override
    public Short convertToDatabaseColumn(ImageSize imageSize) {
        return Objects.requireNonNullElse(imageSize, ImageSize.SMALL).getId();
    }

    @Override
    public ImageSize convertToEntityAttribute(Short gameImageSizeId) {
        if (gameImageSizeId == null) {
            return ImageSize.SMALL;
        }

        return Stream.of(ImageSize.values())
                .filter(ag -> ag.getId() == gameImageSizeId)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
