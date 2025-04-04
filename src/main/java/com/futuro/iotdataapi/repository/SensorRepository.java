package com.futuro.iotdataapi.repository;

import com.futuro.iotdataapi.entity.Sensor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SensorRepository extends JpaRepository<Sensor,Integer> {
    Optional<Sensor> findBySensorApiKey(String sensorApiKey);
    
    Page<Sensor> findByLocationId(Integer locationId, Pageable pageable);

}
