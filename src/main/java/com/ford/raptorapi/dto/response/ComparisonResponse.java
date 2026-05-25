package com.ford.raptorapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComparisonResponse {
    private Integer id;
    private VehicleSummaryResponse vehicleA;
    private VehicleSummaryResponse vehicleB;
    private String notes;
    private OffsetDateTime createdAt;
}
