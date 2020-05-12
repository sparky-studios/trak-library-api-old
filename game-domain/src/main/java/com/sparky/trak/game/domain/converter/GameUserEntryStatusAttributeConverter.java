package com.sparky.trak.game.domain.converter;

import com.sparky.trak.game.domain.GameUserEntryStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class GameUserEntryStatusAttributeConverter implements AttributeConverter<GameUserEntryStatus, Short> {

    @Override
    public Short convertToDatabaseColumn(GameUserEntryStatus gameUserEntryStatus) {
        if (gameUserEntryStatus == null) {
            return GameUserEntryStatus.WISH_LIST.getId();
        }

        return gameUserEntryStatus.getId();
    }

    @Override
    public GameUserEntryStatus convertToEntityAttribute(Short gameUserEntryStatusId) {
        if (gameUserEntryStatusId == null) {
            return GameUserEntryStatus.WISH_LIST;
        }

        return Stream.of(GameUserEntryStatus.values())
                .filter(ag -> ag.getId() == gameUserEntryStatusId)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
