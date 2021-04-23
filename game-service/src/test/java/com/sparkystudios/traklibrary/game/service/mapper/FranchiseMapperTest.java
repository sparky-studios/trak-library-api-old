package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.Franchise;
import com.sparkystudios.traklibrary.game.service.dto.FranchiseDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        FranchiseMapperImpl.class,
})
class FranchiseMapperTest {

    @Autowired
    private FranchiseMapper franchiseMapper;

    @Test
    void fromFranchise_withNull_returnsNull() {
        // Act
        FranchiseDto result = franchiseMapper.fromFranchise(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void fromFranchise_withFranchise_mapsFields() {
        // Arrange
        Franchise franchise = new Franchise();
        franchise.setId(5L);
        franchise.setTitle("test-title");
        franchise.setDescription("test-description");
        franchise.setCreatedAt(LocalDateTime.now());
        franchise.setUpdatedAt(LocalDateTime.now());
        franchise.setVersion(1L);

        // Act
        FranchiseDto result = franchiseMapper.fromFranchise(franchise);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(franchise.getId());
        Assertions.assertThat(result.getTitle()).isEqualTo(franchise.getTitle());
        Assertions.assertThat(result.getDescription()).isEqualTo(franchise.getDescription());
        Assertions.assertThat(result.getCreatedAt()).isEqualTo(franchise.getCreatedAt());
        Assertions.assertThat(result.getUpdatedAt()).isEqualTo(franchise.getUpdatedAt());
        Assertions.assertThat(result.getVersion()).isEqualTo(franchise.getVersion());
    }

    @Test
    void toFranchise_withNull_returnsNull() {
        // Act
        Franchise result = franchiseMapper.toFranchise(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void toFranchise_withFranchiseDto_mapsFields() {
        // Arrange
        FranchiseDto franchiseDto = new FranchiseDto();
        franchiseDto.setId(5L);
        franchiseDto.setTitle("Test Title");
        franchiseDto.setDescription("test-description");
        franchiseDto.setCreatedAt(LocalDateTime.now());
        franchiseDto.setUpdatedAt(LocalDateTime.now());
        franchiseDto.setVersion(1L);

        // Act
        Franchise result = franchiseMapper.toFranchise(franchiseDto);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(franchiseDto.getId());
        Assertions.assertThat(result.getTitle()).isEqualTo(franchiseDto.getTitle());
        Assertions.assertThat(result.getDescription()).isEqualTo(franchiseDto.getDescription());
        Assertions.assertThat(result.getSlug()).isEqualTo("test-title");
        Assertions.assertThat(result.getCreatedAt()).isNull();
        Assertions.assertThat(result.getUpdatedAt()).isNull();
        Assertions.assertThat(result.getVersion()).isEqualTo(franchiseDto.getVersion());
    }
}
