package com.ford.raptorapi.service;

import com.ford.raptorapi.dto.response.VehicleDetailResponse;
import com.ford.raptorapi.dto.response.VehicleSummaryResponse;
import com.ford.raptorapi.exception.ResourceNotFoundException;
import com.ford.raptorapi.mapper.VehicleMapper;
import com.ford.raptorapi.model.Vehicle;
import com.ford.raptorapi.model.enums.FuelType;
import com.ford.raptorapi.model.enums.VehicleCategory;
import com.ford.raptorapi.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    public List<VehicleSummaryResponse> listAll() {
        return vehicleRepository.findAll().stream()
                .map(vehicleMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    public VehicleDetailResponse findById(Integer id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));
        return vehicleMapper.toDetailResponse(vehicle);
    }

    public List<VehicleSummaryResponse> findByCategory(VehicleCategory category) {
        return vehicleRepository.findByCategory(category).stream()
                .map(vehicleMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    public List<VehicleSummaryResponse> findByFuelType(FuelType fuelType) {
        return vehicleRepository.findByFuelType(fuelType).stream()
                .map(vehicleMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    public List<VehicleSummaryResponse> findReferences() {
        return vehicleRepository.findByIsReferenceTrue().stream()
                .map(vehicleMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }
}
