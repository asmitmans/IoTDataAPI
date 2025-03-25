package com.futuro.iotdataapi.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "sensor_meta", columnDefinition = "jsonb")
    private String sensorMeta;
}
