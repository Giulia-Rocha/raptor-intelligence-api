package com.ford.raptorapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.math.BigDecimal;

@Entity
@Table(name = "engine_specs")
@Getter
@Setter
@ToString(exclude = "vehicle")
@NoArgsConstructor
public class EngineSpecs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(name = "engine_type")
    private String engineType;

    @Column(name = "displacement_liters", precision = 4, scale = 2)
    private BigDecimal displacementLiters;

    @Column(name = "cylinders")
    private Short cylinders;

    @Column(name = "cylinder_layout")
    private String cylinderLayout;

    @Column(name = "turbo_type")
    private String turboType;

    @Column(name = "power_hp")
    private Short powerHp;

    @Column(name = "torque_nm")
    private Short torqueNm;

    @Column(name = "torque_rpm")
    private Short torqueRpm;

    @Column(name = "acceleration_0_100_s", precision = 4, scale = 1)
    private BigDecimal acceleration0100s;

    @Column(name = "top_speed_kmh")
    private Short topSpeedKmh;

    @Column(name = "fuel_consumption_urban_kml", precision = 5, scale = 2)
    private BigDecimal fuelConsumptionUrbanKml;

    @Column(name = "fuel_consumption_hwy_kml", precision = 5, scale = 2)
    private BigDecimal fuelConsumptionHwyKml;

    @Column(name = "tank_capacity_liters")
    private Short tankCapacityLiters;

    @Column(name = "estimated_range_km")
    private Short estimatedRangeKm;
}
