package com.ford.raptorapi.controller;

import com.ford.raptorapi.dto.request.VehicleCreateRequest;
import com.ford.raptorapi.dto.response.VehicleDetailResponse;
import com.ford.raptorapi.dto.response.VehicleSummaryResponse;
import com.ford.raptorapi.model.enums.FuelType;
import com.ford.raptorapi.model.enums.VehicleCategory;
import com.ford.raptorapi.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
@Tag(name = "Veículos", description = "Catálogo de veículos comparados (leitura pública; escrita exclusiva de ADMIN)")
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

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cria um veículo", description = "Apenas ADMIN.")
    public ResponseEntity<VehicleSummaryResponse> create(@Valid @RequestBody VehicleCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.create(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Exclui um veículo", description = "Apenas ADMIN.")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
