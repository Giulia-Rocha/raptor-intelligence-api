package com.ford.raptorapi.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "spec_category_map")
@Data
@NoArgsConstructor
public class SpecCategoryMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "category_key", nullable = false)
    private String categoryKey;

    @Column(name = "category_label", nullable = false)
    private String categoryLabel;

    @Column(name = "field_source", nullable = false)
    private String fieldSource;

    @Column(name = "field_name", nullable = false)
    private String fieldName;

    @Column(name = "field_label", nullable = false)
    private String fieldLabel;

    @Column(name = "display_unit")
    private String displayUnit;

    @Column(name = "display_order", nullable = false)
    private Short displayOrder;

    @Column(name = "radar_weight", nullable = false, precision = 3, scale = 2)
    private BigDecimal radarWeight;
}
