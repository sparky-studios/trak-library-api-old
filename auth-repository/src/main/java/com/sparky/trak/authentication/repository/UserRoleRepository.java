package com.sparky.trak.authentication.repository;

import com.sparky.trak.authentication.domain.UserRole;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRoleRepository extends CrudRepository<UserRole, Long> {

    Optional<UserRole> findByRole(String role);
}
