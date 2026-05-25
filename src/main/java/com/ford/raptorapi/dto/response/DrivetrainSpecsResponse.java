package com.ford.raptorapi.dto.response;

import lombok.Data;

@Data
public class DrivetrainSpecsResponse {
    private String transmission;
    private String drivetrainType;
    private String differentialLock;
    private String tractionControl;
}
