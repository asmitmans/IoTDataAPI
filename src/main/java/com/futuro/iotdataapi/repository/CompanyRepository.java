package com.futuro.iotdataapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.futuro.iotdataapi.entity.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {
}
