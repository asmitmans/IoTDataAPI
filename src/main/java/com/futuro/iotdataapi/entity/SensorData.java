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

    @ManyToOne(optional = false)
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    @Column(nullable = false)
    private Long timestamp;

    @Column(name = "value_name", nullable = false, length = 50)
    private String valueName;

    @Column(nullable = false)
    private Double value;
}
