package com.ford.raptorapi.dto.request;

import com.ford.raptorapi.model.enums.FuelType;
import com.ford.raptorapi.model.enums.VehicleCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VehicleCreateRequest {

    @NotNull(message = "brandId é obrigatório")
    private Integer brandId;

    @NotBlank(message = "model é obrigatório")
    private String model;

    @NotBlank(message = "version é obrigatório")
    private String version;

    private Short modelYear;

    @NotNull(message = "fuelType é obrigatório")
    private FuelType fuelType;

    @NotNull(message = "category é obrigatório")
    private VehicleCategory category;

    private Boolean isReference;

    private String imageUrl;
}