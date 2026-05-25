package com.ford.raptorapi.controller;

import com.ford.raptorapi.dto.request.AuthRequest;
import com.ford.raptorapi.dto.response.AuthResponse;
import com.ford.raptorapi.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    @GetMapping("/test-password")
    public String testPassword() {

        String raw = "password";

        String hash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPVKen6/KXi";

        boolean matches =
                new BCryptPasswordEncoder().matches(raw, hash);

        return String.valueOf(matches);
    }
    @GetMapping("/generate-password")
    public String generatePassword() {
        return new BCryptPasswordEncoder().encode("password");
    }
}
