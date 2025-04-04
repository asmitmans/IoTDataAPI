package com.futuro.iotdataapi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.futuro.iotdataapi.entity.SensorData;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Integer> {

	@Query("SELECT s FROM SensorData s WHERE s.sensor.id IN :sensorIds AND s.timestamp BETWEEN :fromEpoch AND :toEpoch")
    Page<SensorData> findAllBySensorIdAndTimestampBetween(
            @Param("sensorIds") List<Integer> sensorIds,
            @Param("fromEpoch") long fromEpoch,
            @Param("toEpoch") long toEpoch,
            Pageable pageable);
	
}
