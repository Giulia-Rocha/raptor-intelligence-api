package com.ford.raptorapi.dto.response;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private Long expiresIn;          // segundos até expirar
    private String name;
    private String dealership;
    private String role;
}
