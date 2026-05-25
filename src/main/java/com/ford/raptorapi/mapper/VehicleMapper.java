package com.ford.raptorapi.mapper;

import com.ford.raptorapi.dto.response.*;
import com.ford.raptorapi.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    @Named("toSummaryResponse")
    @Mapping(target = "brandName", source = "brand.name")
    VehicleSummaryResponse toSummaryResponse(Vehicle vehicle);

    @Mapping(target = "brandName", source = "brand.name")
    @Mapping(target = "engineSpecs", source = "engineSpecs")
    @Mapping(target = "drivetrainSpecs", source = "drivetrainSpecs")
    @Mapping(target = "suspensionSpecs", source = "suspensionSpecs")
    @Mapping(target = "dimensions", source = "dimensions")
    @Mapping(target = "dimensions.fuelTankCapacity", source = "engineSpecs.tankCapacityLiters")
    @Mapping(target = "warranty", source = "warranty")
    VehicleDetailResponse toDetailResponse(Vehicle vehicle);

    BrandResponse toResponse(Brand brand);

    @Mapping(target = "displacement", source = "displacementLiters")
    @Mapping(target = "horsepower", source = "powerHp")
    @Mapping(target = "torque", source = "torqueNm")
    @Mapping(target = "cylinders", source = "cylinders")
    @Mapping(target = "fuelSystem", source = "turboType")
    EngineSpecsResponse toResponse(EngineSpecs specs);

    @Mapping(target = "transmission", source = "transmissionType")
    @Mapping(target = "drivetrainType", source = "tractionType")
    @Mapping(target = "differentialLock", source = "diffLockType")
    @Mapping(target = "tractionControl", source = "hasHillDescent")
    DrivetrainSpecsResponse toResponse(DrivetrainSpecs specs);

    @Mapping(target = "groundClearance", source = "groundClearanceMm")
    @Mapping(target = "approachAngle", source = "approachAngleDeg")
    @Mapping(target = "departureAngle", source = "departureAngleDeg")
    @Mapping(target = "waterWading", source = "waterCrossingMm")
    SuspensionSpecsResponse toResponse(SuspensionSpecs specs);

    @Mapping(target = "length", source = "lengthMm")
    @Mapping(target = "width", source = "widthMm")
    @Mapping(target = "height", source = "heightMm")
    @Mapping(target = "wheelbase", source = "wheelbaseMm")
    @Mapping(target = "curbWeight", source = "curbWeightKg")
    @Mapping(target = "payload", source = "payloadKg")
    @Mapping(target = "towingCapacity", source = "towingCapacityKg")
    @Mapping(target = "fuelTankCapacity", ignore = true)
    DimensionsResponse toResponse(Dimensions specs);

    @Mapping(target = "basicWarranty", source = "warrantyYears")
    @Mapping(target = "powertrainWarranty", source = "powertrainWarrantyYears")
    @Mapping(target = "roadsideAssistance", source = "hasRoadsideAssistance")
    WarrantyResponse toResponse(Warranty specs);
}
