package com.ford.raptorapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "drivetrain_specs")
@Getter
@Setter
@ToString(exclude = "vehicle")
@NoArgsConstructor
public class DrivetrainSpecs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(name = "transmission_type")
    private String transmissionType;

    @Column(name = "gears")
    private Short gears;

    @Column(name = "traction_type")
    private String tractionType;

    @Column(name = "has_low_range")
    private Boolean hasLowRange;

    @Column(name = "diff_lock_type")
    private String diffLockType;

    @Column(name = "has_hill_descent")
    private Boolean hasHillDescent;

    @Column(name = "has_hill_start")
    private Boolean hasHillStart;

    @Column(name = "drive_modes", columnDefinition = "jsonb")
    private String driveModes;
}
