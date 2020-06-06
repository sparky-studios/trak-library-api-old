package com.sparky.trak.game.domain.converter;

import com.sparky.trak.game.domain.BarcodeType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class BarcodeTypeAttributeConverter implements AttributeConverter<BarcodeType, Short> {

    @Override
    public Short convertToDatabaseColumn(BarcodeType barcodeType) {
        if (barcodeType == null) {
            return BarcodeType.EAN_13.getId();
        }

        return barcodeType.getId();
    }

    @Override
    public BarcodeType convertToEntityAttribute(Short barcodeTypeId) {
        if (barcodeTypeId == null) {
            return BarcodeType.EAN_13;
        }

        return Stream.of(BarcodeType.values())
                .filter(ag -> ag.getId() == barcodeTypeId)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
