package com.ford.raptorapi.dto.response;

import lombok.Data;

@Data
public class DimensionsResponse {
    private String length;
    private String width;
    private String height;
    private String wheelbase;
    private String curbWeight;
    private String payload;
    private String towingCapacity;
    private String fuelTankCapacity;
}
