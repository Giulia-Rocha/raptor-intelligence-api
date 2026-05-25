package com.ford.raptorapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "dimensions")
@Getter
@Setter
@ToString(exclude = "vehicle")
@NoArgsConstructor
public class Dimensions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(name = "length_mm")
    private Short lengthMm;

    @Column(name = "width_mm")
    private Short widthMm;

    @Column(name = "height_mm")
    private Short heightMm;

    @Column(name = "wheelbase_mm")
    private Short wheelbaseMm;

    @Column(name = "curb_weight_kg")
    private Short curbWeightKg;

    @Column(name = "payload_kg")
    private Short payloadKg;

    @Column(name = "towing_capacity_kg")
    private Short towingCapacityKg;

    @Column(name = "bed_volume_liters")
    private Short bedVolumeLiters;

    @Column(name = "passenger_capacity")
    private Short passengerCapacity;
}