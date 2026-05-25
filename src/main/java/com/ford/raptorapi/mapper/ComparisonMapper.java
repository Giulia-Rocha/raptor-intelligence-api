package com.ford.raptorapi.mapper;

import com.ford.raptorapi.dto.response.ComparisonResponse;
import com.ford.raptorapi.model.Comparison;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {VehicleMapper.class})
public interface ComparisonMapper {

    @Mapping(target = "vehicleA", qualifiedByName = "toSummaryResponse")
    @Mapping(target = "vehicleB", qualifiedByName = "toSummaryResponse")
    ComparisonResponse toResponse(Comparison comparison);
}
