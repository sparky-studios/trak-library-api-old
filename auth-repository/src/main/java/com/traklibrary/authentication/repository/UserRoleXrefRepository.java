package com.traklibrary.authentication.repository;

import com.traklibrary.authentication.domain.UserRoleXref;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleXrefRepository extends CrudRepository<UserRoleXref, Long> {
}
