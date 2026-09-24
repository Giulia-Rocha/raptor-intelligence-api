package com.ford.raptorapi.controller;

import com.ford.raptorapi.dto.request.ProfileDetectionRequest;
import com.ford.raptorapi.dto.response.CustomerProfileResponse;
import com.ford.raptorapi.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
@Tag(name = "Perfil do Cliente", description = "Detecção de perfil e argumentos de venda para o consultor")
public class ProfileController {

    private final ProfileService profileService;

    @Operation(summary = "Busca um perfil de cliente por tipo",
            description = "Retorna label, descrição, sinais de detecção e argumentos de venda "
                    + "para um dos tipos: enthusiast, lifestyle, rational ou tech.")
    @GetMapping("/{type}")
    public ResponseEntity<CustomerProfileResponse> getByType(@PathVariable String type) {
        return ResponseEntity.ok(profileService.getByType(type));
    }

    @Operation(summary = "Detecta o perfil do cliente",
            description = "Recebe os parâmetros da busca (marca, modelo, versão, atributos) e decide "
                    + "o perfil com base em palavras-chave gravadas no banco. Sem correspondência, retorna 'tech'.")
    @PostMapping("/detect")
    public ResponseEntity<CustomerProfileResponse> detect(
            @RequestBody ProfileDetectionRequest request) {
        return ResponseEntity.ok(profileService.detect(request));
    }
}