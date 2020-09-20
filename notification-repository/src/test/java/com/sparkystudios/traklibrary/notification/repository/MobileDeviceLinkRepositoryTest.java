package com.sparkystudios.traklibrary.notification.repository;

import com.sparkystudios.traklibrary.notification.domain.MobileDeviceLink;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataJpaTest
class MobileDeviceLinkRepositoryTest {

    @Autowired
    private MobileDeviceLinkRepository mobileDeviceLinkRepository;

    @BeforeAll
    public void beforeAll() {
        mobileDeviceLinkRepository.deleteAll();
    }

    @Test
    void findAllByUserId_withNoResults_returnsEmptyCollection() {
        // Act
        Collection<MobileDeviceLink> result = mobileDeviceLinkRepository.findAllByUserId(1L);

        // Assert
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findAllByUserId_withResult_returnsCollection() {
        // Arrange
        MobileDeviceLink mobileDeviceLink1 = new MobileDeviceLink();
        mobileDeviceLink1.setUserId(1L);
        mobileDeviceLink1.setDeviceGuid("device-guid-1");
        mobileDeviceLink1.setLinkedDate(LocalDateTime.now());
        mobileDeviceLink1.setToken("token-1");
        mobileDeviceLink1.setEndpointArn("endpoint-arn-1");
        mobileDeviceLink1 = mobileDeviceLinkRepository.save(mobileDeviceLink1);

        MobileDeviceLink mobileDeviceLink2 = new MobileDeviceLink();
        mobileDeviceLink2.setUserId(1L);
        mobileDeviceLink2.setDeviceGuid("device-guid-2");
        mobileDeviceLink2.setLinkedDate(LocalDateTime.now());
        mobileDeviceLink2.setToken("token-2");
        mobileDeviceLink2.setEndpointArn("endpoint-arn-2");
        mobileDeviceLink2 = mobileDeviceLinkRepository.save(mobileDeviceLink2);

        // Act
        Collection<MobileDeviceLink> result = mobileDeviceLinkRepository.findAllByUserId(1L);

        // Assert
        Assertions.assertThat(result).hasSize(2)
            .contains(mobileDeviceLink1)
            .contains(mobileDeviceLink2);
    }

    @Test
    void findByDeviceGuid_withNoResult_returnsEmptyOptional() {
        // Act
        Optional<MobileDeviceLink> result = mobileDeviceLinkRepository.findByDeviceGuid("device-guid");

        // Assert
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findByDeviceGuid_withMobileDeviceLink_returnsMobileDeviceLink() {
        // Arrange
        MobileDeviceLink mobileDeviceLink = new MobileDeviceLink();
        mobileDeviceLink.setUserId(1L);
        mobileDeviceLink.setDeviceGuid("device-guid");
        mobileDeviceLink.setLinkedDate(LocalDateTime.now());
        mobileDeviceLink.setToken("token");
        mobileDeviceLink.setEndpointArn("endpoint-arn");
        mobileDeviceLink = mobileDeviceLinkRepository.save(mobileDeviceLink);

        // Act
        Optional<MobileDeviceLink> result = mobileDeviceLinkRepository.findByDeviceGuid("device-guid");

        // Assert
        Assertions.assertThat(result).isPresent()
                .isEqualTo(Optional.of(mobileDeviceLink));
    }

    @Test
    void findByUserIdAndDeviceGuid_withNoResult_returnsEmptyOptional() {
        // Act
        Optional<MobileDeviceLink> result = mobileDeviceLinkRepository.findByUserIdAndDeviceGuid(1L, "device-guid");

        // Assert
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void findByUserIdAndDeviceGuid_withMobileDeviceLink_returnsMobileDeviceLink() {
        // Arrange
        MobileDeviceLink mobileDeviceLink = new MobileDeviceLink();
        mobileDeviceLink.setUserId(1L);
        mobileDeviceLink.setDeviceGuid("device-guid");
        mobileDeviceLink.setLinkedDate(LocalDateTime.now());
        mobileDeviceLink.setToken("token");
        mobileDeviceLink.setEndpointArn("endpoint-arn");
        mobileDeviceLink = mobileDeviceLinkRepository.save(mobileDeviceLink);

        // Act
        Optional<MobileDeviceLink> result = mobileDeviceLinkRepository.findByUserIdAndDeviceGuid(1L, "device-guid");

        // Assert
        Assertions.assertThat(result).isPresent()
                .isEqualTo(Optional.of(mobileDeviceLink));
    }
}
