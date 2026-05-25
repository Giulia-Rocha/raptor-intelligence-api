package com.ford.raptorapi.controller;

import com.ford.raptorapi.dto.response.VehicleDetailResponse;
import com.ford.raptorapi.dto.response.VehicleSummaryResponse;
import com.ford.raptorapi.model.enums.FuelType;
import com.ford.raptorapi.model.enums.VehicleCategory;
import com.ford.raptorapi.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public ResponseEntity<List<VehicleSummaryResponse>> listAll() {
        return ResponseEntity.ok(vehicleService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleDetailResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(vehicleService.findById(id));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<VehicleSummaryResponse>> findByCategory(@PathVariable VehicleCategory category) {
        return ResponseEntity.ok(vehicleService.findByCategory(category));
    }

    @GetMapping("/fuel/{fuelType}")
    public ResponseEntity<List<VehicleSummaryResponse>> findByFuelType(@PathVariable FuelType fuelType) {
        return ResponseEntity.ok(vehicleService.findByFuelType(fuelType));
    }

    @GetMapping("/references")
    public ResponseEntity<List<VehicleSummaryResponse>> findReferences() {
        return ResponseEntity.ok(vehicleService.findReferences());
    }
}
