package com.ford.raptorapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.math.BigDecimal;

@Entity
@Table(name = "warranty")
@Getter
@Setter
@ToString(exclude = "vehicle")
@NoArgsConstructor
public class Warranty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(name = "warranty_years")
    private Short warrantyYears;

    @Column(name = "powertrain_warranty_years")
    private Short powertrainWarrantyYears;

    @Column(name = "battery_warranty_years")
    private Short batteryWarrantyYears;

    @Column(name = "service_interval_km")
    private Short serviceIntervalKm;

    @Column(name = "avg_service_cost_brl", precision = 10, scale = 2)
    private BigDecimal avgServiceCostBrl;

    @Column(name = "has_roadside_assistance")
    private Boolean hasRoadsideAssistance;

    @Column(name = "warranty_notes", columnDefinition = "TEXT")
    private String warrantyNotes;
}
