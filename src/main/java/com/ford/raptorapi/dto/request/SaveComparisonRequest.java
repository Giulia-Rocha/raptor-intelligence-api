package com.ford.raptorapi.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveComparisonRequest {
    
    @NotNull(message = "Vehicle A ID is required")
    private Integer vehicleAId;
    
    @NotNull(message = "Vehicle B ID is required")
    private Integer vehicleBId;
    
    private String notes;
}
