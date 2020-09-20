package com.sparkystudios.traklibrary.notification.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;
import java.time.LocalDateTime;
import java.util.Collections;

@DataJpaTest
class MobileDeviceLinkTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void persist_withNullDeviceGuid_throwsPersistenceException() {
        // Arrange
        MobileDeviceLink mobileDeviceLink = new MobileDeviceLink();
        mobileDeviceLink.setUserId(1L);
        mobileDeviceLink.setDeviceGuid(null);
        mobileDeviceLink.setLinkedDate(LocalDateTime.now());
        mobileDeviceLink.setToken("token");
        mobileDeviceLink.setEndpointArn("endpoint");

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(mobileDeviceLink));
    }

    @Test
    void persist_withDeviceGuidExceedingLength_throwsPersistenceException() {
        // Arrange
        MobileDeviceLink mobileDeviceLink = new MobileDeviceLink();
        mobileDeviceLink.setUserId(1L);
        mobileDeviceLink.setDeviceGuid(String.join("", Collections.nCopies(300, "t")));
        mobileDeviceLink.setLinkedDate(LocalDateTime.now());
        mobileDeviceLink.setToken("token");
        mobileDeviceLink.setEndpointArn("endpoint");

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(mobileDeviceLink));
    }

    @Test
    void persist_withNullLinkedDate_throwsPersistenceException() {
        // Arrange
        MobileDeviceLink mobileDeviceLink = new MobileDeviceLink();
        mobileDeviceLink.setUserId(1L);
        mobileDeviceLink.setDeviceGuid("device-guid");
        mobileDeviceLink.setLinkedDate(null);
        mobileDeviceLink.setToken("token");
        mobileDeviceLink.setEndpointArn("endpoint");

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(mobileDeviceLink));
    }

    @Test
    void persist_withNullToken_throwsPersistenceException() {
        // Arrange
        MobileDeviceLink mobileDeviceLink = new MobileDeviceLink();
        mobileDeviceLink.setUserId(1L);
        mobileDeviceLink.setDeviceGuid("device-guid");
        mobileDeviceLink.setLinkedDate(LocalDateTime.now());
        mobileDeviceLink.setToken(null);
        mobileDeviceLink.setEndpointArn("endpoint");

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(mobileDeviceLink));
    }

    @Test
    void persist_withNullEndpointArn_throwsPersistenceException() {
        // Arrange
        MobileDeviceLink mobileDeviceLink = new MobileDeviceLink();
        mobileDeviceLink.setUserId(1L);
        mobileDeviceLink.setDeviceGuid("device-guid");
        mobileDeviceLink.setLinkedDate(LocalDateTime.now());
        mobileDeviceLink.setToken("token");
        mobileDeviceLink.setEndpointArn(null);

        // Assert
        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> testEntityManager.persistFlushFind(mobileDeviceLink));
    }

    @Test
    void persist_withValidMobileDeviceLink_mapsGame() {
        // Arrange
        MobileDeviceLink mobileDeviceLink = new MobileDeviceLink();
        mobileDeviceLink.setUserId(1L);
        mobileDeviceLink.setDeviceGuid("device-guid");
        mobileDeviceLink.setLinkedDate(LocalDateTime.now());
        mobileDeviceLink.setToken("token");
        mobileDeviceLink.setEndpointArn("endpoint-arn");

        // Act
        MobileDeviceLink result = testEntityManager.persistFlushFind(mobileDeviceLink);

        // Assert
        Assertions.assertThat(result.getId()).isGreaterThan(0L);
        Assertions.assertThat(result.getUserId()).isEqualTo(mobileDeviceLink.getUserId());
        Assertions.assertThat(result.getDeviceGuid()).isEqualTo(mobileDeviceLink.getDeviceGuid());
        Assertions.assertThat(result.getLinkedDate()).isEqualTo(mobileDeviceLink.getLinkedDate());
        Assertions.assertThat(result.getToken()).isEqualTo(mobileDeviceLink.getToken());
        Assertions.assertThat(result.getEndpointArn()).isEqualTo(mobileDeviceLink.getEndpointArn());
        Assertions.assertThat(result.getVersion()).isNotNull().isGreaterThanOrEqualTo(0L);
    }
}
