package com.ford.raptorapi.service;

import com.ford.raptorapi.dto.request.AuthRequest;
import com.ford.raptorapi.dto.response.AuthResponse;
import com.ford.raptorapi.model.AppUser;
import com.ford.raptorapi.repository.AppUserRepository;
import com.ford.raptorapi.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        AppUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String jwtToken = jwtService.generateToken(user);

        AuthResponse response = new AuthResponse();
        response.setToken(jwtToken);
        response.setExpiresIn(jwtService.getExpirationMillis() / 1000);
        response.setName(user.getName());
        response.setDealership(user.getDealership());
        response.setRole(user.getRole().name());

        return response;
    }
}
