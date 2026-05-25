package com.ford.raptorapi.dto.response;

import lombok.Data;

@Data
public class EngineSpecsResponse {
    private String engineType;
    private String displacement;
    private String horsepower;
    private String torque;
    private String fuelSystem;
    private String cylinders;
}
