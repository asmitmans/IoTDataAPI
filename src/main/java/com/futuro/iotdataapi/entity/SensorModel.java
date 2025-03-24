package com.futuro.iotdataapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sensor_model")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "category", nullable = false, length = 50)
    private String category;
}
