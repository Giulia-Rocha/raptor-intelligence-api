package com.ford.raptorapi.dto.response;

import lombok.Data;

@Data
public class SuspensionSpecsResponse {
    private String frontSuspension;
    private String rearSuspension;
    private String groundClearance;
    private String approachAngle;
    private String departureAngle;
    private String waterWading;
}
