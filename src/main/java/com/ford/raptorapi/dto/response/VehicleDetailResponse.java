package com.ford.raptorapi.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VehicleDetailResponse extends VehicleSummaryResponse {
    private EngineSpecsResponse engineSpecs;
    private DrivetrainSpecsResponse drivetrainSpecs;
    private SuspensionSpecsResponse suspensionSpecs;
    private DimensionsResponse dimensions;
    private WarrantyResponse warranty;
}
