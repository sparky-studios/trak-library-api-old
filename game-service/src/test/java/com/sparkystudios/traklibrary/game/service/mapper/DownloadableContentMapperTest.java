package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.DownloadableContent;
import com.sparkystudios.traklibrary.game.service.dto.DownloadableContentDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

class DownloadableContentMapperTest {

    @Test
    void downloadableContentToDownloadableContentDto_withNull_returnsNull() {
        // Act
        DownloadableContentDto result = GameMappers.DOWNLOADABLE_CONTENT_MAPPER.downloadableContentToDownloadableContentDto(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void downloadableContentToDownloadableContentDto_withDownloadableContent_mapsFields() {
        // Arrange
        DownloadableContent downloadableContent = new DownloadableContent();
        downloadableContent.setId(5L);
        downloadableContent.setName("test-name");
        downloadableContent.setDescription("test-description");
        downloadableContent.setReleaseDate(LocalDate.now());
        downloadableContent.setCreatedAt(LocalDateTime.now());
        downloadableContent.setUpdatedAt(LocalDateTime.now());
        downloadableContent.setVersion(1L);

        // Act
        DownloadableContentDto result = GameMappers.DOWNLOADABLE_CONTENT_MAPPER.downloadableContentToDownloadableContentDto(downloadableContent);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(downloadableContent.getId());
        Assertions.assertThat(result.getName()).isEqualTo(downloadableContent.getName());
        Assertions.assertThat(result.getDescription()).isEqualTo(downloadableContent.getDescription());
        Assertions.assertThat(result.getReleaseDate()).isEqualTo(downloadableContent.getReleaseDate());
        Assertions.assertThat(result.getCreatedAt()).isEqualTo(downloadableContent.getCreatedAt());
        Assertions.assertThat(result.getUpdatedAt()).isEqualTo(downloadableContent.getUpdatedAt());
        Assertions.assertThat(result.getVersion()).isEqualTo(downloadableContent.getVersion());
    }

    @Test
    void downloadableContentDtoToDownloadableContent_withNull_returnsNull() {
        // Act
        DownloadableContent result = GameMappers.DOWNLOADABLE_CONTENT_MAPPER.downloadableContentDtoToDownloadableContent(null);

        // Assert
        Assertions.assertThat(result).isNull();
    }

    @Test
    void downloadableContentDtoToDownloadableContent_withDownloadableContentDto_mapsFields() {
        // Arrange
        DownloadableContentDto downloadableContentDto = new DownloadableContentDto();
        downloadableContentDto.setId(5L);
        downloadableContentDto.setName("test-name");
        downloadableContentDto.setDescription("test-description");
        downloadableContentDto.setReleaseDate(LocalDate.now());
        downloadableContentDto.setCreatedAt(LocalDateTime.now());
        downloadableContentDto.setUpdatedAt(LocalDateTime.now());
        downloadableContentDto.setVersion(1L);

        // Act
        DownloadableContent result = GameMappers.DOWNLOADABLE_CONTENT_MAPPER.downloadableContentDtoToDownloadableContent(downloadableContentDto);

        // Assert
        Assertions.assertThat(result.getId()).isEqualTo(downloadableContentDto.getId());
        Assertions.assertThat(result.getName()).isEqualTo(downloadableContentDto.getName());
        Assertions.assertThat(result.getDescription()).isEqualTo(downloadableContentDto.getDescription());
        Assertions.assertThat(result.getReleaseDate()).isEqualTo(downloadableContentDto.getReleaseDate());
        Assertions.assertThat(result.getCreatedAt()).isNull();
        Assertions.assertThat(result.getUpdatedAt()).isNull();
        Assertions.assertThat(result.getVersion()).isEqualTo(downloadableContentDto.getVersion());
    }
}
