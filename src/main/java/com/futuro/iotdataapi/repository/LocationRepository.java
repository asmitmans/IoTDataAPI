package com.futuro.iotdataapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.futuro.iotdataapi.entity.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
}
