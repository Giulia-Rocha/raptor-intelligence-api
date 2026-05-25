package com.ford.raptorapi.dto.response;

import com.ford.raptorapi.model.enums.FuelType;
import com.ford.raptorapi.model.enums.VehicleCategory;
import lombok.Data;

@Data
public class VehicleSummaryResponse {
    private Integer id;
    private String brandName;
    private String model;
    private String version;
    private Short modelYear;
    private FuelType fuelType;
    private VehicleCategory category;
    private Boolean isReference;
    private String imageUrl;
}
