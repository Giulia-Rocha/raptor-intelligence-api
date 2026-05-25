package com.ford.raptorapi.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleFeatureId implements Serializable {

    private Integer vehicleId;
    private Integer featureId;
}
