package com.ford.raptorapi.service;

import com.ford.raptorapi.dto.response.BrandResponse;
import com.ford.raptorapi.mapper.VehicleMapper;
import com.ford.raptorapi.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {

    private final BrandRepository brandRepository;
    private final VehicleMapper vehicleMapper;

    public List<BrandResponse> listAll() {
        return brandRepository.findAll().stream()
                .map(vehicleMapper::toResponse)
                .collect(Collectors.toList());
    }
}