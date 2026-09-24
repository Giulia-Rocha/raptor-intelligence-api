package com.ford.raptorapi.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ProfileDetectionRequest {

    private String brand;
    private String model;
    private String version;
    private List<String> attributes;
}