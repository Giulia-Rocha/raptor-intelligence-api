package com.ford.raptorapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCompareData {
    private String label;
    private Map<Integer, Double> radarScore;
    private List<SpecRow> specs;
}
