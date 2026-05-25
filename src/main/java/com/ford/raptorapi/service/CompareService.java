package com.ford.raptorapi.service;

import com.ford.raptorapi.dto.response.*;
import com.ford.raptorapi.mapper.VehicleMapper;
import com.ford.raptorapi.model.SpecCategoryMap;
import com.ford.raptorapi.model.Vehicle;
import com.ford.raptorapi.model.VehicleFeature;
import com.ford.raptorapi.repository.SpecCategoryMapRepository;
import com.ford.raptorapi.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CompareService {

    private final VehicleRepository vehicleRepository;
    private final SpecCategoryMapRepository specCategoryMapRepository;
    private final VehicleMapper vehicleMapper;

    // Campos onde MENOR valor = melhor desempenho
    private static final List<String> INVERTED_FIELDS = List.of(
            "acceleration_0_100_s",
            "avg_service_cost_brl"
    );

    public CompareResponse compare(List<Integer> ids, List<String> categories) {
        List<Vehicle> vehicles = vehicleRepository.findAllById(ids);

        List<SpecCategoryMap> mappings = (categories == null || categories.isEmpty())
                ? specCategoryMapRepository.findAll()
                : specCategoryMapRepository.findByCategoryKeyIn(categories);

        Map<String, List<SpecCategoryMap>> groupedMappings = mappings.stream()
                .collect(Collectors.groupingBy(SpecCategoryMap::getCategoryKey));

        Map<String, CategoryCompareData> categoriesData = new LinkedHashMap<>();

        for (Map.Entry<String, List<SpecCategoryMap>> entry : groupedMappings.entrySet()) {
            String categoryKey = entry.getKey();
            List<SpecCategoryMap> categoryMappings = entry.getValue();

            categoryMappings.sort(Comparator.comparing(
                    SpecCategoryMap::getDisplayOrder,
                    Comparator.nullsLast(Comparator.naturalOrder())
            ));

            String label = categoryMappings.get(0).getCategoryLabel();
            Map<Integer, Double> radarScores = new HashMap<>();
            List<SpecRow> specRows = new ArrayList<>();

            // Monta linhas de specs
            for (SpecCategoryMap mapping : categoryMappings) {
                Map<Integer, String> valuesMap = new HashMap<>();
                for (Vehicle vehicle : vehicles) {
                    Object value = getSpecValue(vehicle, mapping.getFieldName());
                    valuesMap.put(vehicle.getId(), value != null ? value.toString() : "-");
                }
                specRows.add(SpecRow.builder()
                        .label(mapping.getFieldLabel())
                        .unit(mapping.getDisplayUnit())
                        .values(valuesMap)
                        .build());
            }

            // Calcula radarScore por veiculo nessa categoria
            for (Vehicle vehicle : vehicles) {
                double totalScore = 0;
                double totalWeight = 0;

                for (SpecCategoryMap mapping : categoryMappings) {
                    Object value = getSpecValue(vehicle, mapping.getFieldName());
                    double score = calculateScore(value, mapping.getFieldName(), mapping, vehicles);
                    double weight = mapping.getRadarWeight() != null
                            ? mapping.getRadarWeight().doubleValue()
                            : 1.0;
                    totalScore += score * weight;
                    totalWeight += weight;
                }

                double finalScore = totalWeight > 0 ? totalScore / totalWeight : 0;
                radarScores.put(vehicle.getId(), Math.round(finalScore * 100.0) / 100.0);
            }

            categoriesData.put(categoryKey, CategoryCompareData.builder()
                    .label(label)
                    .radarScore(radarScores)
                    .specs(specRows)
                    .build());
        }

        return CompareResponse.builder()
                .vehicles(vehicles.stream()
                        .map(vehicleMapper::toSummaryResponse)
                        .collect(Collectors.toList()))
                .categories(categoriesData)
                .build();
    }

    /**
     * Busca o valor de um campo em um veículo.
     * Tenta primeiro nas vehicle_features (N:M), depois via reflection nas tabelas de spec.
     */
    private Object getSpecValue(Vehicle vehicle, String fieldName) {
        // 1. Busca em vehicle_features pelo nome da feature
        if (vehicle.getVehicleFeatures() != null) {
            Optional<VehicleFeature> feature = vehicle.getVehicleFeatures().stream()
                    .filter(vf -> vf.getFeature() != null
                            && vf.getFeature().getName().equalsIgnoreCase(fieldName))
                    .findFirst();
            if (feature.isPresent()) {
                return feature.get().getValue();
            }
        }

        // 2. Busca via reflection nos objetos de spec
        List<Object> specObjects = new ArrayList<>(Arrays.asList(
                vehicle.getEngineSpecs(),
                vehicle.getDrivetrainSpecs(),
                vehicle.getSuspensionSpecs(),
                vehicle.getDimensions(),
                vehicle.getWarranty(),
                vehicle
        ));

        for (Object obj : specObjects) {
            if (obj == null) continue;
            try {
                Field field = obj.getClass().getDeclaredField(toCamelCase(fieldName));
                field.setAccessible(true);
                return field.get(obj);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // Campo nao encontrado neste objeto, continua
            }
        }

        return null;
    }

    /**
     * Calcula o score normalizado (0-100) de um valor dentro do contexto dos veiculos comparados.
     */
    private double calculateScore(Object value, String fieldName,
                                  SpecCategoryMap mapping, List<Vehicle> allVehicles) {
        if (value == null) return 0;

        // Boolean: true = 100, false = 0
        if (value instanceof Boolean) {
            return (Boolean) value ? 100.0 : 0.0;
        }

        // Texto nao numerico: presente = 100, ausente = 0
        try {
            double currentVal = parseDouble(value.toString());

            List<Double> allVals = allVehicles.stream()
                    .map(v -> getSpecValue(v, fieldName))
                    .filter(Objects::nonNull)
                    .map(v -> {
                        try {
                            return parseDouble(v.toString());
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (allVals.isEmpty()) return 100.0;

            if (INVERTED_FIELDS.contains(fieldName)) {
                // Menor e melhor: normaliza invertido
                double minVal = allVals.stream().mapToDouble(v -> v).min().orElse(currentVal);
                return minVal > 0 ? (minVal / currentVal) * 100.0 : 0.0;
            } else {
                // Maior e melhor: normaliza direto
                double maxVal = allVals.stream().mapToDouble(v -> v).max().orElse(currentVal);
                return maxVal > 0 ? (currentVal / maxVal) * 100.0 : 0.0;
            }

        } catch (Exception e) {
            // Valor textual nao nulo = presente = 100
            return 100.0;
        }
    }

    /**
     * Converte snake_case para camelCase para uso no reflection.
     * Ex: "power_hp" -> "powerHp"
     */
    private String toCamelCase(String snakeCase) {
        String[] parts = snakeCase.split("_");
        StringBuilder result = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            if (!parts[i].isEmpty()) {
                result.append(Character.toUpperCase(parts[i].charAt(0)));
                result.append(parts[i].substring(1));
            }
        }
        return result.toString();
    }

    private double parseDouble(String value) {
        String cleaned = value.replaceAll("[^0-9.,]", "").replace(",", ".");
        return Double.parseDouble(cleaned);
    }
}