package com.sparkystudios.traklibrary.game.repository;

import com.sparkystudios.traklibrary.game.domain.CompanyImage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyImageRepository extends CrudRepository<CompanyImage, Long> {

    boolean existsByCompanyId(long companyId);

    Optional<CompanyImage> findByCompanyId(long companyId);
}
