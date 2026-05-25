package com.ford.raptorapi.model;

import com.ford.raptorapi.model.enums.FuelType;
import com.ford.raptorapi.model.enums.VehicleCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"engineSpecs", "drivetrainSpecs", "suspensionSpecs", "dimensions", "warranty", "vehicleFeatures", "brand"})
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String version;

    @Column(name = "model_year")
    private Short modelYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type")
    private FuelType fuelType;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private VehicleCategory category;

    @Column(name = "is_reference")
    private Boolean isReference;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToOne(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private EngineSpecs engineSpecs;

    @OneToOne(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DrivetrainSpecs drivetrainSpecs;

    @OneToOne(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private SuspensionSpecs suspensionSpecs;

    @OneToOne(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Dimensions dimensions;

    @OneToOne(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Warranty warranty;

    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    private List<VehicleFeature> vehicleFeatures;
}
