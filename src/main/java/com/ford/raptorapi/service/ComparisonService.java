package com.ford.raptorapi.service;

import com.ford.raptorapi.dto.request.SaveComparisonRequest;
import com.ford.raptorapi.dto.response.ComparisonResponse;
import com.ford.raptorapi.exception.ResourceNotFoundException;
import com.ford.raptorapi.exception.UnauthorizedException;
import com.ford.raptorapi.mapper.ComparisonMapper;
import com.ford.raptorapi.model.AppUser;
import com.ford.raptorapi.model.Comparison;
import com.ford.raptorapi.model.Vehicle;
import com.ford.raptorapi.repository.AppUserRepository;
import com.ford.raptorapi.repository.ComparisonRepository;
import com.ford.raptorapi.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComparisonService {

    private final ComparisonRepository comparisonRepository;
    private final VehicleRepository vehicleRepository;
    private final AppUserRepository userRepository;
    private final ComparisonMapper comparisonMapper;

    @Transactional(readOnly = true)
    public List<ComparisonResponse> findByUser(Integer userId) {
        return comparisonRepository.findByUserId(userId).stream()
                .map(comparisonMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ComparisonResponse save(Integer userId, SaveComparisonRequest request) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Vehicle vehicleA = vehicleRepository.findById(request.getVehicleAId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle A not found"));

        Vehicle vehicleB = vehicleRepository.findById(request.getVehicleBId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle B not found"));

        Comparison comparison = new Comparison();
        comparison.setUser(user);
        comparison.setVehicleA(vehicleA);
        comparison.setVehicleB(vehicleB);
        comparison.setNotes(request.getNotes());

        Comparison saved = comparisonRepository.save(comparison);
        return comparisonMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Integer id, Integer userId) {
        Comparison comparison = comparisonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comparison not found"));

        if (!comparison.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to delete this comparison");
        }

        comparisonRepository.delete(comparison);
    }
}
