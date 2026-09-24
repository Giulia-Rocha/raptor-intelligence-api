package com.ford.raptorapi.service;

import com.ford.raptorapi.dto.request.VehicleCreateRequest;
import com.ford.raptorapi.dto.response.VehicleDetailResponse;
import com.ford.raptorapi.dto.response.VehicleSummaryResponse;
import com.ford.raptorapi.exception.ResourceNotFoundException;
import com.ford.raptorapi.mapper.VehicleMapper;
import com.ford.raptorapi.model.Brand;
import com.ford.raptorapi.model.Vehicle;
import com.ford.raptorapi.model.enums.FuelType;
import com.ford.raptorapi.model.enums.VehicleCategory;
import com.ford.raptorapi.repository.BrandRepository;
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
    private final BrandRepository brandRepository;
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

    @Transactional
    public VehicleSummaryResponse create(VehicleCreateRequest request) {
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + request.getBrandId()));

        Vehicle vehicle = new Vehicle();
        vehicle.setBrand(brand);
        vehicle.setModel(request.getModel());
        vehicle.setVersion(request.getVersion());
        vehicle.setModelYear(request.getModelYear());
        vehicle.setFuelType(request.getFuelType());
        vehicle.setCategory(request.getCategory());
        vehicle.setIsReference(request.getIsReference() != null ? request.getIsReference() : false);
        vehicle.setImageUrl(request.getImageUrl());

        return vehicleMapper.toSummaryResponse(vehicleRepository.save(vehicle));
    }

    @Transactional
    public void delete(Integer id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));
        vehicleRepository.delete(vehicle);
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
