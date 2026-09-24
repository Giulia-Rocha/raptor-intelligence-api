package com.ford.raptorapi.dto.response;

public record UserProfileResponse(
        String email,
        String name,
        String dealership,
        String role
) {
}