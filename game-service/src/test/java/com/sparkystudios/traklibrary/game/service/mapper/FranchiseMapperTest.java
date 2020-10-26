package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.Franchise;
import com.sparkystudios.traklibrary.game.service.dto.FranchiseDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class FranchiseMapperTest {

    @Test
    void franchiseToFranchiseDto_withNull_returnsNull() {
        // Act
        FranchiseDto result = GameMappers.FRANCHISE_MAPPER.franchiseToFranchiseDto(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void franchiseToFranchiseDto_withFranchise_mapsFields() {
        // Arrange
        Franchise franchise = new Franchise();
        franchise.setId(5L);
        franchise.setTitle("test-title");
        franchise.setDescription("test-description");
        franchise.setCreatedAt(LocalDateTime.now());
        franchise.setUpdatedAt(LocalDateTime.now());
        franchise.setVersion(1L);

        // Act
        FranchiseDto result = GameMappers.FRANCHISE_MAPPER.franchiseToFranchiseDto(franchise);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(franchise.getId());
        Assertions.assertThat(result.getTitle()).isEqualTo(franchise.getTitle());
        Assertions.assertThat(result.getDescription()).isEqualTo(franchise.getDescription());
        Assertions.assertThat(result.getCreatedAt()).isEqualTo(franchise.getCreatedAt());
        Assertions.assertThat(result.getUpdatedAt()).isEqualTo(franchise.getUpdatedAt());
        Assertions.assertThat(result.getVersion()).isEqualTo(franchise.getVersion());
    }

    @Test
    void franchiseDtoToFranchise_withNull_returnsNull() {
        // Act
        Franchise result = GameMappers.FRANCHISE_MAPPER.franchiseDtoToFranchise(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void franchiseDtoToFranchise_withFranchiseDto_mapsFields() {
        // Arrange
        FranchiseDto franchiseDto = new FranchiseDto();
        franchiseDto.setId(5L);
        franchiseDto.setTitle("test-title");
        franchiseDto.setDescription("test-description");
        franchiseDto.setCreatedAt(LocalDateTime.now());
        franchiseDto.setUpdatedAt(LocalDateTime.now());
        franchiseDto.setVersion(1L);

        // Act
        Franchise result = GameMappers.FRANCHISE_MAPPER.franchiseDtoToFranchise(franchiseDto);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(franchiseDto.getId());
        Assertions.assertThat(result.getTitle()).isEqualTo(franchiseDto.getTitle());
        Assertions.assertThat(result.getDescription()).isEqualTo(franchiseDto.getDescription());
        Assertions.assertThat(result.getCreatedAt()).isNull();
        Assertions.assertThat(result.getUpdatedAt()).isNull();
        Assertions.assertThat(result.getVersion()).isEqualTo(franchiseDto.getVersion());
    }
}
