package com.ford.raptorapi.controller;

import com.ford.raptorapi.dto.request.AuthRequest;
import com.ford.raptorapi.dto.response.AuthResponse;
import com.ford.raptorapi.dto.response.UserProfileResponse;
import com.ford.raptorapi.model.AppUser;
import com.ford.raptorapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Login e emissão do token JWT")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Autentica um usuário e emite o JWT",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login bem-sucedido, token gerado"),
                    @ApiResponse(responseCode = "400", description = "Payload inválido"),
                    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
            })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Retorna os dados do usuário autenticado",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Perfil do usuário"),
                    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
            })
    @PostMapping("/me")
    public ResponseEntity<UserProfileResponse> me(@AuthenticationPrincipal AppUser user) {
        return ResponseEntity.ok(new UserProfileResponse(
                user.getEmail(), user.getName(), user.getDealership(), user.getRole().name()));
    }
}
