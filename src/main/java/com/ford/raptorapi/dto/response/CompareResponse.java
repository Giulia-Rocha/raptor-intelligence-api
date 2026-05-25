package com.ford.raptorapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompareResponse {
    private List<VehicleSummaryResponse> vehicles;
    private Map<String, CategoryCompareData> categories;
}
