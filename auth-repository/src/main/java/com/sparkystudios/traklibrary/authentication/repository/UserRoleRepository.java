package com.sparkystudios.traklibrary.authentication.repository;

import com.sparkystudios.traklibrary.authentication.domain.UserRole;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends CrudRepository<UserRole, Long> {

    Optional<UserRole> findByRole(String role);
}
