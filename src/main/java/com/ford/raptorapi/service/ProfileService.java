package com.ford.raptorapi.service;

import com.ford.raptorapi.dto.request.ProfileDetectionRequest;
import com.ford.raptorapi.dto.response.CustomerProfileResponse;
import com.ford.raptorapi.dto.response.SalesArgumentResponse;
import com.ford.raptorapi.exception.ResourceNotFoundException;
import com.ford.raptorapi.model.CustomerProfile;
import com.ford.raptorapi.model.ProfileDetectionKeyword;
import com.ford.raptorapi.model.ProfileSignal;
import com.ford.raptorapi.model.SalesArgument;
import com.ford.raptorapi.repository.CustomerProfileRepository;
import com.ford.raptorapi.repository.ProfileDetectionKeywordRepository;
import com.ford.raptorapi.repository.ProfileSignalRepository;
import com.ford.raptorapi.repository.SalesArgumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private static final String DEFAULT_TYPE = "tech";

    private final CustomerProfileRepository customerProfileRepository;
    private final ProfileDetectionKeywordRepository keywordRepository;
    private final ProfileSignalRepository signalRepository;
    private final SalesArgumentRepository salesArgumentRepository;

    @Transactional(readOnly = true)
    public CustomerProfileResponse getByType(String type) {
        CustomerProfile profile = customerProfileRepository.findById(type)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Perfil de cliente não encontrado: " + type));

        List<String> signals = signalRepository
                .findByProfileTypeOrderByDisplayOrderAsc(type)
                .stream()
                .map(ProfileSignal::getSignal)
                .toList();

        List<SalesArgumentResponse> arguments = salesArgumentRepository
                .findByProfileTypeOrderByDisplayOrderAsc(type)
                .stream()
                .map(this::toResponse)
                .toList();

        return new CustomerProfileResponse(
                profile.getType(),
                profile.getLabel(),
                profile.getDescription(),
                signals,
                arguments);
    }

    @Transactional(readOnly = true)
    public CustomerProfileResponse detect(ProfileDetectionRequest request) {
        String searchSpace = normalize(request);
        Map<String, Integer> scores = new LinkedHashMap<>();

        for (ProfileDetectionKeyword keyword : keywordRepository.findAllByOrderByIdAsc()) {
            if (searchSpace.contains(keyword.getKeyword().toLowerCase(Locale.ROOT))) {
                scores.merge(keyword.getProfileType(), keyword.getWeight(), Integer::sum);
            }
        }

        String type = scores.entrySet().stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse(DEFAULT_TYPE);

        return getByType(type);
    }

    private String normalize(ProfileDetectionRequest request) {
        String brand = request.getBrand() == null ? "" : request.getBrand();
        String model = request.getModel() == null ? "" : request.getModel();
        String version = request.getVersion() == null ? "" : request.getVersion();
        String attributes = request.getAttributes() == null
                ? ""
                : String.join(" ", request.getAttributes());
        return (brand + " " + model + " " + version + " " + attributes)
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private SalesArgumentResponse toResponse(SalesArgument argument) {
        return new SalesArgumentResponse(
                argument.getId(),
                argument.getTitle(),
                argument.getDescription(),
                argument.getUrgency());
    }
}