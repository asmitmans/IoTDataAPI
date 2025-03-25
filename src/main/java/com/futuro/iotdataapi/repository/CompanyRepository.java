package com.futuro.iotdataapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.futuro.iotdataapi.entity.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {

	Optional<Company> findByCompanyApiKey(String uuid);
}
