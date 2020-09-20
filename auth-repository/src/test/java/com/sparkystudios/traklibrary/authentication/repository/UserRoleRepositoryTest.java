package com.sparkystudios.traklibrary.authentication.repository;

import com.sparkystudios.traklibrary.authentication.domain.UserRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataJpaTest
class UserRoleRepositoryTest {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @BeforeAll
    public void beforeAll() {
        userRoleRepository.deleteAll();
    }

    @Test
    void findByRole_withNonExistentUserRole_returnsEmptyOptional() {
        // Act
        Optional<UserRole> result = userRoleRepository.findByRole("ROLE_TEST");

        // Assert
        Assertions.assertThat(result).isNotPresent();
    }

    @Test
    void findByRole_withUserRole_returnsUserRole() {
        // Arrange
        UserRole userRole = new UserRole();
        userRole.setRole("ROLE_TEST");

        userRole = userRoleRepository.save(userRole);

        // Act
        Optional<UserRole> result = userRoleRepository.findByRole("ROLE_TEST");

        // Assert
        Assertions.assertThat(result).isPresent()
                .isEqualTo(Optional.of(userRole));
    }
}
