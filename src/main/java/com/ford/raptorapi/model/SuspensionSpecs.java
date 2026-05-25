package com.ford.raptorapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.math.BigDecimal;

@Entity
@Table(name = "suspension_specs")
@Getter
@Setter
@ToString(exclude = "vehicle")
@NoArgsConstructor
public class SuspensionSpecs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(name = "front_suspension")
    private String frontSuspension;

    @Column(name = "rear_suspension")
    private String rearSuspension;

    @Column(name = "front_shock_brand")
    private String frontShockBrand;

    @Column(name = "ground_clearance_mm")
    private Short groundClearanceMm;

    @Column(name = "approach_angle_deg", precision = 4, scale = 1)
    private BigDecimal approachAngleDeg;

    @Column(name = "departure_angle_deg", precision = 4, scale = 1)
    private BigDecimal departureAngleDeg;

    @Column(name = "water_crossing_mm")
    private Short waterCrossingMm;

    @Column(name = "tire_type")
    private String tireType;

    @Column(name = "tire_size_inches")
    private Short tireSizeInches;

    @Column(name = "tire_spec")
    private String tireSpec;

    @Column(name = "offroad_profile")
    private String offroadProfile;
}
