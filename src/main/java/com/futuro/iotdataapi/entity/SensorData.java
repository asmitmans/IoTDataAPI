package com.futuro.iotdataapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sensor_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    private Long timestamp;

    private String valueName;

    private String valueUnit;

    private Double value;
}
