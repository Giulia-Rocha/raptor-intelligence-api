package com.ford.raptorapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProfileResponse {
    private String type;
    private String label;
    private String description;
    private List<String> detectedSignals;
    private List<SalesArgumentResponse> salesArguments;
}