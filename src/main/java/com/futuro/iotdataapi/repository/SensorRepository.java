package com.futuro.iotdataapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.futuro.iotdataapi.dto.SensorCategoryDto;
import com.futuro.iotdataapi.entity.Sensor;

@Repository
public interface SensorRepository extends JpaRepository<Sensor,Integer> {
    Optional<Sensor> findBySensorApiKey(String sensorApiKey);
    
    Page<Sensor> findByLocationId(Integer locationId, Pageable pageable);

    List<Sensor> findByLocationIdIn(List<Integer> locationIds);
    
    @Query("SELECT new com.futuro.iotdataapi.dto.SensorCategoryDto(s.category, COUNT(s)) FROM Sensor s GROUP BY s.category")
     List<SensorCategoryDto> findSensorCategoriesWithCount();

}
