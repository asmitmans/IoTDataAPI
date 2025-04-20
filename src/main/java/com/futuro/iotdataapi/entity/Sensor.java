package com.futuro.iotdataapi.entity;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sensor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "sensor_name", nullable = false, length = 100)
    private String sensorName;

    @Column(name = "sensor_api_key", nullable = false, unique = true, length = 255)
    private String sensorApiKey;

    @Column(name = "sensor_category")
    private String category; // categoria es opcional

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sensor_meta", columnDefinition = "jsonb")
    private String sensorMeta;
}
