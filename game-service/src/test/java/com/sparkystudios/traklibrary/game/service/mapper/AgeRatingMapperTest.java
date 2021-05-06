package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.AgeRating;
import com.sparkystudios.traklibrary.game.domain.AgeRatingClassification;
import com.sparkystudios.traklibrary.game.service.dto.AgeRatingDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        AgeRatingMapperImpl.class,
})
class AgeRatingMapperTest {

    @Autowired
    private AgeRatingMapper ageRatingMapper;

    @Test
    void fromAgeRating_withNull_returnsNull() {
        // Act
        AgeRatingDto result = ageRatingMapper.fromAgeRating(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void fromAgeRating_withAgeRating_mapsFields() {
        // Arrange
        AgeRating ageRating = new AgeRating();
        ageRating.setId(1L);
        ageRating.setClassification(AgeRatingClassification.CERO);
        ageRating.setRating((short)4);
        ageRating.setCreatedAt(LocalDateTime.now());
        ageRating.setUpdatedAt(LocalDateTime.now());
        ageRating.setVersion(1L);

        // Act
        AgeRatingDto result = ageRatingMapper.fromAgeRating(ageRating);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(ageRating.getId());
        Assertions.assertThat(result.getClassification()).isEqualTo(ageRating.getClassification());
        Assertions.assertThat(result.getRating()).isEqualTo(ageRating.getRating());
        Assertions.assertThat(result.getCreatedAt()).isEqualTo(ageRating.getCreatedAt());
        Assertions.assertThat(result.getUpdatedAt()).isEqualTo(ageRating.getUpdatedAt());
        Assertions.assertThat(result.getVersion()).isEqualTo(ageRating.getVersion());
    }

    @Test
    void toAgeRating_withNull_returnsNull() {
        // Act
        AgeRating result = ageRatingMapper.toAgeRating(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void toAgeRating_withAgeRatingDto_mapsFields() {
        // Arrange
        AgeRatingDto ageRatingDto = new AgeRatingDto();
        ageRatingDto.setId(1L);
        ageRatingDto.setClassification(AgeRatingClassification.CERO);
        ageRatingDto.setRating((short)4);
        ageRatingDto.setCreatedAt(LocalDateTime.now());
        ageRatingDto.setUpdatedAt(LocalDateTime.now());
        ageRatingDto.setVersion(1L);

        // Act
        AgeRating result = ageRatingMapper.toAgeRating(ageRatingDto);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(ageRatingDto.getId());
        Assertions.assertThat(result.getClassification()).isEqualTo(ageRatingDto.getClassification());
        Assertions.assertThat(result.getRating()).isEqualTo(ageRatingDto.getRating());
        Assertions.assertThat(result.getCreatedAt()).isNull();
        Assertions.assertThat(result.getUpdatedAt()).isNull();
        Assertions.assertThat(result.getVersion()).isEqualTo(ageRatingDto.getVersion());
    }
}
