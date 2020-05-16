package com.sparky.trak.game.repository;

import com.sparky.trak.game.domain.Company;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CompanyRepository extends PagingAndSortingRepository<Company, Long>, JpaSpecificationExecutor<Company> {
}
