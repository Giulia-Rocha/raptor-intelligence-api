package com.ford.raptorapi.controller;

import com.ford.raptorapi.dto.response.BrandResponse;
import com.ford.raptorapi.mapper.VehicleMapper;
import com.ford.raptorapi.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandRepository brandRepository;
    private final VehicleMapper vehicleMapper;

    @GetMapping
    public ResponseEntity<List<BrandResponse>> listAll() {
        List<BrandResponse> brands = brandRepository.findAll().stream()
                .map(vehicleMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(brands);
    }
}
